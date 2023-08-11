package tools.impl.tool.builder.typed.impl;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.vadim.util.item.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class GeneralToolBuilder {
	private Material material;
	private ItemStack item;
	private String displayName;
	private String localizedName;
	private List<String> lore = new ArrayList<>();
	private List<ItemFlag> flags = new ArrayList<>();
	private Map<Enchantment, Integer> enchantments = new HashMap();
	private Multimap<Attribute, AttributeModifier> attributeModifiers = HashMultimap.create();
	private PlayerProfile playerProfile;
	private Integer customModelData;
	private boolean unbreakable = false;
	private int repairCost = 0;
	private int damage = 0;
	private int amount = 1;

	public GeneralToolBuilder(Material material) {
		if (material == null) {
			throw new NullPointerException("material");
		} else {
			this.item = null;
			this.material = material;
		}
	}

	GeneralToolBuilder(ItemStack item) {
		if (item == null) {
			throw new NullPointerException("item");
		} else {
			this.item = item;
			this.material = null;
		}
	}

	public GeneralToolBuilder meta(ItemMeta meta) {
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

			this.flags(new ArrayList(meta.getItemFlags()));
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

	public GeneralToolBuilder material(@NotNull Material material) {
		this.material = material;
		return this;
	}

	public GeneralToolBuilder item(@NotNull ItemStack item) {
		this.item = item;
		return this.meta(item.getItemMeta());
	}

	public GeneralToolBuilder displayName(String displayName) {
		this.displayName = Text.CHAT_COLOR_CLEAR + displayName;
		return this;
	}

	public GeneralToolBuilder localizedName(String localizedName) {
		this.localizedName = localizedName;
		return this;
	}

	public GeneralToolBuilder lore(List<String> lore) {
		this.lore.clear();
		return this.addLore(lore);
	}

	public GeneralToolBuilder lore(String... lore) {
		return this.lore(Arrays.asList(lore));
	}

	public GeneralToolBuilder addLore(List<String> lore) {
		if (this.lore == null) {
			this.lore = new ArrayList();
		}

		this.lore.addAll(lore.stream().map((it) -> {
			return Text.CHAT_COLOR_CLEAR + it;
		}).toList());
		return this;
	}

	public GeneralToolBuilder addLore(String... lore) {
		return this.addLore(Arrays.asList(lore));
	}

	public GeneralToolBuilder allFlags() {
		return this.flags(ItemFlag.values());
	}

	public GeneralToolBuilder flags(List<ItemFlag> flags) {
		this.flags = new ArrayList(flags);
		return this;
	}

	public GeneralToolBuilder flags(ItemFlag... flags) {
		this.flags = new ArrayList(Arrays.asList(flags));
		return this;
	}

	public GeneralToolBuilder flag(ItemFlag flag) {
		this.flags.add(flag);
		return this;
	}

	public GeneralToolBuilder enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = new HashMap(enchantments);
		return this;
	}

	public GeneralToolBuilder enchantment(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	public GeneralToolBuilder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
		this.attributeModifiers = HashMultimap.create(attributeModifiers);
		return this;
	}

	public GeneralToolBuilder attributeModifiers(Attribute attribute, AttributeModifier... attributeModifiers) {
		this.attributeModifiers.get(attribute).addAll(Arrays.asList(attributeModifiers));
		return this;
	}

	public GeneralToolBuilder attributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
		this.attributeModifiers.get(attribute).add(attributeModifier);
		return this;
	}

	public GeneralToolBuilder customModelData(Integer customModelData) {
		this.customModelData = customModelData;
		return this;
	}

	public GeneralToolBuilder unbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public GeneralToolBuilder repairCost(int repairCost) {
		this.repairCost = repairCost;
		return this;
	}

	public GeneralToolBuilder skullOwner(PlayerProfile playerProfile) {
		this.playerProfile = playerProfile;
		return this;
	}

	public GeneralToolBuilder skullOwner(OfflinePlayer owningPlayer) {
		this.playerProfile = Bukkit.createProfile(owningPlayer.getUniqueId(), owningPlayer.getName());
		return this;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public GeneralToolBuilder skullOwner(UUID uniqueId) {
		this.playerProfile = Bukkit.createProfile(uniqueId);
		return this;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public GeneralToolBuilder skullOwner(String playerName) {
		this.playerProfile = Bukkit.createProfile(playerName);
		return this;
	}

	public GeneralToolBuilder damage(int damage) {
		this.damage = damage;
		return this;
	}

	public GeneralToolBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	public GeneralToolBuilder editMeta(Consumer<ItemMeta> consumer) {
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

	/*
	@SafeVarargs
	public final GeneralToolBuilder setBaseCustomEnchants(Pair<AttributeKey, Integer>... enchant) {
		return createEnchantedItem(enchantItem -> {
			if (enchantedItem == null) throw new RuntimeException("You must first use ToolBuilder#makeEnchantable");

			Arrays.stream(enchant).forEach(pair ->
					enchantItem.addEnchant(pair.getFirst(), pair.getSecond())
			);

		});
	}

	public ToolBuilder createEnchantedItem(Consumer<IEnchantedTool> enchantedItemConsumer) {
		final ItemStack item = this.build();
		this.enchantedItem = new EnchantedTool(item);
		enchantedItemConsumer.accept(enchantedItem);
		return this;
	}

	public ToolBuilder makeEnchantable() {
		if (enchantedItem == null) throw new RuntimeException("You must first use ToolBuilder#createEnchantedItem");
		this.enchantedItem.makeEnchantable();
		return this;
	}

	 */


	public static <T extends GeneralToolBuilder> T fromStack(Class<T> clazz, ItemStack stack) {
		return ((T) new GeneralToolBuilder(stack));
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
				meta.addItemFlags(this.flags.toArray(ItemFlag[]::new));
				meta.setAttributeModifiers(this.attributeModifiers);
				meta.setCustomModelData(this.customModelData);
				meta.setUnbreakable(this.unbreakable);

				if (meta instanceof Repairable repairable) {
					repairable.setRepairCost(this.repairCost);
				}

				if (meta instanceof SkullMeta skullMeta) {
					skullMeta.setPlayerProfile(this.playerProfile);
				}



				item.setItemMeta(meta);
				item.addUnsafeEnchantments(this.enchantments);
				return item;
			}
		}
	}
}

