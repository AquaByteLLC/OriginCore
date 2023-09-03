package tools.impl.tool.builder.typed.impl;

import com.mojang.datafixers.util.Pair;
import commons.util.BukkitUtil;
import commons.util.StringUtil;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.Placeholders;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.Text;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;
import tools.impl.tool.impl.SkinnedTool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UniqueItemBuilder {

	private final Map<String, Pair<String, Object>> placeholderData = new HashMap<>();
	@Getter
	public static final Map<String, Pair<String, List<String>>> initial = new ConcurrentHashMap<>();
	public static final NamespacedKey uniqueLore = new NamespacedKey("builder", "lore");
	public static final NamespacedKey uniqueName = new NamespacedKey("builder", "name");
	private Placeholder pl;
	private AugmentedTool augmentedTool;
	private SkinnedTool skinnedTool;
	private EnchantedTool enchantedTool;

	private final ItemStack item;

	public UniqueItemBuilder(ItemStack item) {
		this.item = item;
	}

	public ItemStack build() {
		return ItemStackBuilder.of(this.item).build();
	}

	public UniqueItemBuilder write(Consumer<PersistentDataContainer> consumer) {
		BukkitUtil.writeContainer(build(), consumer);
		return this;
	}

	public UniqueItemBuilder removeData(String namespace, String keyName) {
		final ItemStack item = this.build();
		placeholderData.remove(keyName);
		return removeData(item, namespace, keyName);
	}

	public static UniqueItemBuilder removeData(ItemStack stack, String namespace, String keyName) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
		builder.item.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final NamespacedKey key = new NamespacedKey(namespace, keyName);
			pdc.remove(key);
		});
		return builder;
	}


	public <T, Z> Z getData(String namespace, String keyName, PersistentDataType<T, Z> type) {
		final ItemStack item = this.build();
		return getData(item, namespace, keyName, type);
	}

	public static <T, Z> Z getData(ItemStack stack, String namespace, String keyName, PersistentDataType<T, Z> type) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
		AtomicReference<Z> data = new AtomicReference<>();

		builder.item.editMeta((meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final NamespacedKey key = new NamespacedKey(namespace, keyName);
			data.set(pdc.get(key, type));
		}));

		return data.get();
	}

	public Placeholder manipulate() {
		final ItemStack item = this.build();
		return this.pl;
	}

	public static Placeholder manipulate(ItemStack stack) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
		final ItemStack item = builder.build();
		return builder.pl;
	}

	public UniqueItemBuilder create() {
		final StringPlaceholder.Builder plBuilder = StringPlaceholder.builder();
		placeholderData.forEach(((s, stringObjectPair) -> {
			plBuilder.set(stringObjectPair.getFirst(), String.valueOf(stringObjectPair.getSecond()));
		}));
		this.pl = plBuilder.build();

		this.item.editMeta((meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			if (!pdc.has(uniqueLore) && !pdc.has(uniqueName)) {
				pdc.set(uniqueLore, PersistentDataType.STRING, String.join("\n", meta.getLore()));
				pdc.set(uniqueName, PersistentDataType.STRING, meta.getDisplayName());
			}
		}));
		return this;
	}


	public static void updateItem(ItemStack stack, List<String> baseLore, String baseName, Placeholder pl) {
		stack.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();

			String displayName = Text.colorize(baseName);
			List<String> loreList = new ArrayList<>(baseLore);

			loreList = Placeholders.reformat(pl, loreList).stream()
					.flatMap(it -> Arrays.stream(it.split("\n")))
					.map(StringUtil::colorize).collect(Collectors.toList());

			meta.setLore(loreList);
			meta.setDisplayName(pl.format(displayName));
		});
	}

	public static UniqueItemBuilder fromStack(ItemStack stack) {
		return new UniqueItemBuilder(stack);
	}

	public static void updateItem(ItemStack stack, Placeholder pl) {
		stack.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if (!(pdc.has(uniqueName) && pdc.has(uniqueLore))) return;

			final String name = pdc.get(uniqueName, PersistentDataType.STRING);
			final List<String> lore = Arrays.asList(pdc.get(uniqueLore, PersistentDataType.STRING).split("\n"));

			if (lore.isEmpty()) return;
			if (name.isBlank()) return;

			String displayName = Text.colorize(name);
			List<String> loreList = new ArrayList<>(lore);

			loreList = Placeholders.reformat(pl, loreList).stream()
					.flatMap(it -> Arrays.stream(it.split("\n")))
					.map(StringUtil::colorize).collect(Collectors.toList());

			meta.setLore(loreList);
			meta.setDisplayName(pl.format(displayName));
		});
	}

	public <T extends IBaseTool> UniqueItemBuilder asSpecialTool(Class<T> clazz, Consumer<T> customToolConsumer) {

		if (clazz.equals(EnchantedTool.class)) {
			this.enchantedTool = new EnchantedTool(build());
			customToolConsumer.accept((T) enchantedTool);
		} else if (clazz.equals(AugmentedTool.class)) {
			this.augmentedTool = new AugmentedTool(build());
			customToolConsumer.accept((T) augmentedTool);
		} else if (clazz.equals(SkinnedTool.class)) {
			this.skinnedTool = new SkinnedTool(build());
			customToolConsumer.accept((T) skinnedTool);
		}

		return this;
	}

	public <T extends IBaseTool> UniqueItemBuilder toolBuilder(Class<T> clazz, T tool) {
		if (clazz.equals(EnchantedTool.class)) this.enchantedTool = (EnchantedTool) tool;
		else if (clazz.equals(AugmentedTool.class)) this.augmentedTool = (AugmentedTool) tool;
		else if (clazz.equals(SkinnedTool.class)) this.skinnedTool = (SkinnedTool) tool;
		return this;
	}

	public static void updateItem(ItemStack stack, List<String> baseLore, String baseName) {
		stack.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final Placeholder pl = manipulate(stack);

			String displayName = Text.colorize(baseName);
			List<String> loreList = new ArrayList<>(baseLore);

			loreList = Placeholders.reformat(pl, loreList).stream()
					.flatMap(it -> Arrays.stream(it.split("\n")))
					.map(StringUtil::colorize).collect(Collectors.toList());

			meta.setLore(loreList);
			meta.setDisplayName(pl.format(displayName));
		});
	}
}
