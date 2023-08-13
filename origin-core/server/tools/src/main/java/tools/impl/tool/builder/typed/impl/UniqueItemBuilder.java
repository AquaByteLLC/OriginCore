package tools.impl.tool.builder.typed.impl;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import commons.Commons;
import commons.events.api.EventContext;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.Placeholders;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.ItemBuilder;
import me.vadim.util.item.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;
import tools.impl.tool.impl.SkinnedTool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UniqueItemBuilder implements ItemBuilder {

	private final Map<String, Pair<String, Object>> placeholderData = new HashMap<>();
	public static final Map<String, Pair<String, List<String>>> initial = new ConcurrentHashMap<>();
	public static final NamespacedKey uniqueIdentifier = new NamespacedKey("builder", "id");
	private Placeholder pl;
	private AugmentedTool augmentedTool;
	private SkinnedTool skinnedTool;
	private EnchantedTool enchantedTool;
	private Material material;
	private ItemStack item;
	private String displayName;
	private String localizedName;
	private List<String> lore = new ArrayList();
	private List<ItemFlag> flags = new ArrayList();
	private Map<Enchantment, Integer> enchantments = new HashMap();
	private Multimap<Attribute, AttributeModifier> attributeModifiers = HashMultimap.create();
	private PlayerProfile playerProfile;
	private Integer customModelData;
	private boolean unbreakable = false;
	private int repairCost = 0;
	private int damage = 0;
	private int amount = 1;

	UniqueItemBuilder(Material material) {
		if (material == null) {
			throw new NullPointerException("material");
		} else {
			this.item = null;
			this.material = material;
		}
	}

	UniqueItemBuilder(ItemStack item) {
		if (item == null) {
			throw new NullPointerException("item");
		} else {
			this.item = item;
			this.material = null;
		}
	}

	public UniqueItemBuilder meta(ItemMeta meta) {
		if (meta == null) {
			return this;
		} else {
			if (meta.hasDisplayName()) {
				this.displayName(meta.getDisplayName());
			}

			if (meta.hasLocalizedName()) {
				this.localizedName(meta.getLocalizedName());
			}

			if (meta.hasLore()) {
				this.lore(meta.getLore());
			}

			this.flags((List) (new ArrayList(meta.getItemFlags())));
			if (meta.hasEnchants()) {
				this.enchantments(meta.getEnchants());
			}

			if (meta.hasAttributeModifiers()) {
				this.attributeModifiers(meta.getAttributeModifiers());
			}

			if (meta.hasCustomModelData()) {
				this.customModelData(meta.getCustomModelData());
			}

			this.unbreakable(meta.isUnbreakable());
			if (meta instanceof Repairable && ((Repairable) meta).hasRepairCost()) {
				this.repairCost(((Repairable) meta).getRepairCost());
			}

			if (meta instanceof SkullMeta && ((SkullMeta) meta).getOwningPlayer() != null) {
				this.skullOwner(((SkullMeta) meta).getOwningPlayer().getUniqueId());
			}

			if (this.item != null) {
				this.item.setItemMeta(meta);
			}

			return this;
		}
	}

	public static UniqueItemBuilder create(@NotNull Material material) {
		return new UniqueItemBuilder(material);
	}

	public static UniqueItemBuilder create(@NotNull ItemStack stack) {
		return new UniqueItemBuilder(stack);
	}

	public UniqueItemBuilder material(@NotNull Material material) {
		this.material = material;
		return this;
	}

	public UniqueItemBuilder item(@NotNull ItemStack item) {
		this.item = item;
		return this.meta(item.getItemMeta());
	}

	public UniqueItemBuilder displayName(String displayName) {
		this.displayName = Text.CHAT_COLOR_CLEAR + displayName;
		return this;
	}

	public UniqueItemBuilder localizedName(String localizedName) {
		this.localizedName = localizedName;
		return this;
	}

	public UniqueItemBuilder lore(List<String> lore) {
		this.lore.clear();
		return this.addLore(lore);
	}

	public UniqueItemBuilder lore(String... lore) {
		return this.lore(Arrays.asList(lore));
	}

	public UniqueItemBuilder addLore(List<String> lore) {
		if (this.lore == null) {
			this.lore = new ArrayList();
		}

		this.lore.addAll(lore.stream().map((it) -> {
			return Text.CHAT_COLOR_CLEAR + it;
		}).toList());
		return this;
	}

	public UniqueItemBuilder addLore(String... lore) {
		return this.addLore(Arrays.asList(lore));
	}

	public UniqueItemBuilder allFlags() {
		return this.flags(ItemFlag.values());
	}

	public UniqueItemBuilder flags(List<ItemFlag> flags) {
		this.flags = new ArrayList(flags);
		return this;
	}

	public UniqueItemBuilder flags(ItemFlag... flags) {
		this.flags = new ArrayList(Arrays.asList(flags));
		return this;
	}

	public UniqueItemBuilder flag(ItemFlag flag) {
		this.flags.add(flag);
		return this;
	}

	public UniqueItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = new HashMap(enchantments);
		return this;
	}

	public UniqueItemBuilder enchantment(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	public UniqueItemBuilder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
		this.attributeModifiers = HashMultimap.create(attributeModifiers);
		return this;
	}

	public UniqueItemBuilder attributeModifiers(Attribute attribute, AttributeModifier... attributeModifiers) {
		this.attributeModifiers.get(attribute).addAll(Arrays.asList(attributeModifiers));
		return this;
	}

	public UniqueItemBuilder attributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
		this.attributeModifiers.get(attribute).add(attributeModifier);
		return this;
	}

	public UniqueItemBuilder customModelData(Integer customModelData) {
		this.customModelData = customModelData;
		return this;
	}

	public UniqueItemBuilder unbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public UniqueItemBuilder repairCost(int repairCost) {
		this.repairCost = repairCost;
		return this;
	}

	public UniqueItemBuilder skullOwner(PlayerProfile playerProfile) {
		this.playerProfile = playerProfile;
		return this;
	}

	public UniqueItemBuilder skullOwner(OfflinePlayer owningPlayer) {
		this.playerProfile = Bukkit.createProfile(owningPlayer.getUniqueId(), owningPlayer.getName());
		return this;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public UniqueItemBuilder skullOwner(UUID uniqueId) {
		this.playerProfile = Bukkit.createProfile(uniqueId);
		return this;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public UniqueItemBuilder skullOwner(String playerName) {
		this.playerProfile = Bukkit.createProfile(playerName);
		return this;
	}

	public UniqueItemBuilder damage(int damage) {
		this.damage = damage;
		return this;
	}

	public UniqueItemBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	public UniqueItemBuilder editMeta(Consumer<ItemMeta> consumer) {
		ItemStack item = this.build();
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			return this;
		} else {
			consumer.accept(meta);
			item.setItemMeta(meta);
			return this.item(item);
		}
	}

	public ItemStack build() {
		if (this.material == null && this.item == null) {
			throw new NullPointerException("Material or item must be set.");
		} else {
			ItemStack item = this.material == null ? this.item : new ItemStack(this.material, this.amount, (short) this.damage);
			ItemMeta meta = item.getItemMeta();
			if (meta == null) {
				return item;
			} else {
				meta.setDisplayName(Text.colorize(this.displayName));
				meta.setLocalizedName(this.localizedName);
				meta.setLore(Text.colorize(this.lore));
				meta.addItemFlags((ItemFlag[]) this.flags.toArray((x$0) -> {
					return new ItemFlag[x$0];
				}));
				meta.setAttributeModifiers(this.attributeModifiers);
				meta.setCustomModelData(this.customModelData);
				meta.setUnbreakable(this.unbreakable);
				if (meta instanceof Repairable) {
					Repairable repairable = (Repairable) meta;
					repairable.setRepairCost(this.repairCost);
				}

				if (meta instanceof SkullMeta) {
					SkullMeta skullMeta = (SkullMeta) meta;
					skullMeta.setPlayerProfile(this.playerProfile);
				}

				item.setItemMeta(meta);
				item.addUnsafeEnchantments(this.enchantments);
				return item;
			}
		}
	}

	public UniqueItemBuilder removeData(String namespace, String keyName) {
		final ItemStack item = this.build();
		placeholderData.remove(keyName);
		return removeData(item, namespace, keyName);
	}

	public static UniqueItemBuilder removeData(ItemStack stack, String namespace, String keyName) {
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
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
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
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

		editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			if (!pdc.has(uniqueIdentifier)) {
				final String uid = UUID.randomUUID().toString();
				pdc.set(uniqueIdentifier, PersistentDataType.STRING, uid);
				initial.put(uid, Pair.of(meta.getDisplayName(), meta.getLore()));
			}
		});
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
			final String uid = pdc.get(uniqueIdentifier, PersistentDataType.STRING);
			if (uid.isBlank()) return;

			final String name = initial.get(uid).getFirst();
			final List<String> lore = initial.get(uid).getSecond();

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

	public <T, Z> UniqueItemBuilder createCustomData(String namespace, String keyName, PersistentDataType<T, Z> type, Z data) {
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
		final UniqueItemBuilder builder = UniqueItemBuilder.fromStack(stack);
		builder.editMeta(meta -> {
			final PersistentDataContainer pdc = meta.getPersistentDataContainer();
			final NamespacedKey key = new NamespacedKey(namespace, keyName);
			pdc.set(key, type, data);
		});
		return builder;
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
