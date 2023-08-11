package tools.impl.tool.builder.typed.impl;

import com.mojang.datafixers.util.Pair;
import commons.Commons;
import commons.events.api.EventContext;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.Placeholders;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UniqueItemBuilder extends GeneralToolBuilder {


	private final Map<String, Pair<String, Object>> placeholderData = new HashMap<>();
	public static final Map<String, Pair<String, List<String>>> initial = new ConcurrentHashMap<>();
	public static final NamespacedKey uniqueIdentifier = new NamespacedKey("builder", "id");
	private Placeholder pl;
	private AugmentedTool augmentedTool;
	private EnchantedTool enchantedTool;

	public UniqueItemBuilder(Material material) {
		super(material);
	}

	UniqueItemBuilder(ItemStack item) {
		super(item);
	}

	public GeneralToolBuilder removeData(String namespace, String keyName) {
		final ItemStack item = this.build();
		placeholderData.remove(keyName);
		return removeData(item, namespace, keyName);
	}

	public static GeneralToolBuilder removeData(ItemStack stack, String namespace, String keyName) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(UniqueItemBuilder.class, stack);
		builder.editMeta(meta -> {
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
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(UniqueItemBuilder.class, stack);
		AtomicReference<Z> data = new AtomicReference<>();

		builder.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final NamespacedKey key = new NamespacedKey(namespace, keyName);
			data.set(pdc.get(key, type));
		});

		return data.get();
	}

	public Placeholder manipulate() {
		final ItemStack item = this.build();
		return this.pl;
	}

	public static Placeholder manipulate(ItemStack stack) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(UniqueItemBuilder.class, stack);
		final ItemStack item = builder.build();
		return builder.pl;
	}

	@Override
	public ItemStack build() {
		final StringPlaceholder.Builder plBuilder = StringPlaceholder.builder();
		placeholderData.forEach(((s, stringObjectPair) -> {
			plBuilder.set(stringObjectPair.getFirst(), String.valueOf(stringObjectPair.getSecond()));
		}));
		this.pl = plBuilder.build();

		editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			if (!pdc.has(uniqueIdentifier)) {
				final String uid = UUID.randomUUID().toString();
				pdc.set(uniqueIdentifier, PersistentDataType.STRING, uid);
				initial.put(uid, Pair.of(meta.getDisplayName(), meta.getLore()));
			}
		});

		return super.build();
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


	public <T, Z> GeneralToolBuilder createCustomData(String namespace, String keyName, PersistentDataType<T, Z> type, Z data) {
		final ItemStack item = this.build();
		return createCustomData(item, namespace, keyName, type, data);
	}

	public <T, Z, C extends Event> UniqueItemBuilder createCustomDataUpdate(String namespace, String keyName, PersistentDataType<T, Z> type, Z data, Class<C> clazz, BiConsumer<EventContext, C> builderConsumer) {
		final ItemStack item = this.build();

		new DetachedSubscriber<>(clazz, (builderConsumer::accept)).bind(Commons.events());
		placeholderData.put(keyName, Pair.of(keyName, data));
		return createCustomData(item, namespace, keyName, type, data);
	}

	public static <T, Z> UniqueItemBuilder createCustomData(ItemStack stack, String namespace, String keyName, PersistentDataType<T, Z> type, Z data) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(UniqueItemBuilder.class, stack);
		builder.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final NamespacedKey key = new NamespacedKey(namespace, keyName);
			pdc.set(key, type, data);
		});
		return builder;
	}

	public <T extends IBaseTool> UniqueItemBuilder asSpecialTool(Class<T> clazz, Consumer<T> customToolConsumer) {

		if (clazz.equals(EnchantedTool.class)) this.enchantedTool = new EnchantedTool(build());
		else if (clazz.equals(AugmentedTool.class)) this.augmentedTool = new AugmentedTool(build());

		final T tool = clazz.cast((IBaseTool) this::build);
		customToolConsumer.accept(tool);

		return this;
	}

	public <T extends IBaseTool> UniqueItemBuilder asSpecialTool(Class<T> clazz, T tool) {
		if (clazz.equals(EnchantedTool.class)) this.enchantedTool = (EnchantedTool) tool;
		else if (clazz.equals(AugmentedTool.class)) this.augmentedTool = (AugmentedTool) tool;

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
