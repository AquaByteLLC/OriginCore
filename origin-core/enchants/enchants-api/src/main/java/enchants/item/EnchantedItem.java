package enchants.item;

import enchants.records.OriginEnchant;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class EnchantedItem {


	/**
	 * This class is used to store enchantments and the level onto the players item.
	 */
	static class EnchantHolder implements Serializable {
		@Setter @Getter
		private int level;
		@Getter
		private final OriginEnchant enchant;

		EnchantHolder(OriginEnchant enchant) {
			this.level = 1;
			this.enchant = enchant;
		}
	}
	static class EnchantDataTypeAdapter implements PersistentDataType<byte[], EnchantHolder> {

		@Override		@Nonnull
		public Class<byte[]> getPrimitiveType() {
			return byte[].class;
		}

		@Override		@Nonnull
		public Class<EnchantHolder> getComplexType() {
			return EnchantHolder.class;
		}

		@Override		@Nonnull
		public byte[] toPrimitive(EnchantHolder complex, PersistentDataAdapterContext context) {
			return SerializationUtils.serialize(complex);
		}

		@Override		@Nonnull
		public EnchantHolder fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
			return SerializationUtils.deserialize(primitive);
		}
	}

	private final EnchantDataTypeAdapter adapter = new EnchantDataTypeAdapter();
	private final PersistentDataContainer container = getPdc();
	private final ItemStack itemStack;
	private final ArrayDeque<EnchantHolder> enchantDeque;

	public EnchantedItem(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.enchantDeque = new ArrayDeque<>();
	}

	private PersistentDataContainer getPdc() {
		if (this.itemStack == null) throw new NullPointerException("The ItemStack being used is null!");
		if (this.itemStack.getItemMeta() == null) throw new NullPointerException("The ItemMeta of the ItemStack is null!");
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	public void addEnchant(OriginEnchant enchant) {
		final EnchantHolder holder = new EnchantHolder(enchant);

		if (container.has(enchant.getKey(), adapter)) throw new IllegalArgumentException("You aren't able to add this enchant to this item because it is already added.");
		else {
			container.set(enchant.getKey(), adapter, holder);
			updateLore();
		}
	}

	@SneakyThrows
	public void updateEnchant(OriginEnchant enchant, int newLevel) {
		if (!container.has(enchant.getKey(), adapter)) throw new IllegalArgumentException("You aren't able to update this enchant because it hasn't been added to the item.");
		else {
			final EnchantHolder holder = container.get(enchant.getKey(), adapter);
			if (holder == null) throw new Exception("The enchant holder is null!");

			holder.setLevel(newLevel);
			container.set(enchant.getKey(), adapter, holder);
			updateLore();
		}
	}

	public void removeEnchant(OriginEnchant enchant) {
		if (!container.has(enchant.getKey(), adapter)) throw new IllegalArgumentException("You aren't able to remove this enchant because it hasn't been added to the item.");
		else {
			container.remove(enchant.getKey());
			updateLore();
		}
	}

	public void removeAllEnchants() {
		if (!container.getKeys().isEmpty()) container.getKeys().forEach(container::remove);
		updateLore();
	}

	@SneakyThrows
	public double getChance(NamespacedKey enchant) {
		if (!container.has(enchant, adapter)) throw new IllegalArgumentException("You aren't able to update this enchant because it hasn't been added to the item.");
		final EnchantHolder holder = container.get(enchant, adapter);
		if (holder == null) throw new Exception("The enchant holder is null!");

		OriginEnchant.EnchantProgressionType chanceType = holder.enchant.chanceType();
		final double startChance = holder.getEnchant().startChance();
		final double maxChance = holder.getEnchant().maxChance();
		final int maxLvl = holder.getEnchant().maxLevel();
		final int currentLvl = holder.getLevel();

		if (maxLvl == 1) return maxChance;
		else {
			switch (chanceType) {
				case EXPONENTIAL -> {return startChance * Math.pow(maxChance / startChance, (double) (currentLvl - 1) / (maxLvl - 1));}
				case LOGARITHMIC -> {return handleLog(startChance, maxChance, currentLvl, maxLvl);}
			}
			return 0.0;
		}
	}

	@SneakyThrows
	public double getCost(NamespacedKey enchant) {
		if (!container.has(enchant, adapter)) throw new IllegalArgumentException("You aren't able to update this enchant because it hasn't been added to the item.");
		final EnchantHolder holder = container.get(enchant, adapter);
		if (holder == null) throw new Exception("The enchant holder is null!");

		OriginEnchant.EnchantProgressionType costType = holder.enchant.costType();
		final double startCost = holder.getEnchant().startCost();
		final double maxCost = holder.getEnchant().maxCost();
		final int maxLvl = holder.getEnchant().maxLevel();
		final int currentLvl = holder.getLevel();

		if (maxLvl == 1) return maxCost;
		else {
			switch (costType) {
				case EXPONENTIAL -> {return startCost * Math.pow(maxCost / startCost, (double) (currentLvl - 1) / (maxLvl - 1));}
				case LOGARITHMIC -> {return handleLog(startCost, maxCost, currentLvl, maxLvl);}
			}
			return 0.0;
		}
	}

	@SneakyThrows
	public int getLevel(NamespacedKey enchant) {
		if (!container.has(enchant, adapter)) throw new IllegalArgumentException("You aren't able to update this enchant because it hasn't been added to the item.");
		final EnchantHolder holder = container.get(enchant, adapter);
		if (holder == null) throw new Exception("The enchant holder is null!");
		return holder.getLevel();
	}

	public boolean hasEnchant(NamespacedKey enchant) {
		return container.has(enchant, adapter);
	}

	private void updateLore() {
		updateDeque();
		if (itemStack.hasItemMeta()) {
			ItemMeta meta = itemStack.getItemMeta();
			if (meta == null) throw new RuntimeException("The ItemMeta is null!");

			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				if (lore != null) {
					lore.add(" ");
					lore.add("&f&cEnchants");
					for (EnchantHolder enchant : enchantDeque) {
						lore.add(enchant.enchant.lore().replaceAll("%level%", String.valueOf(enchant.level)));
					}
					meta.setLore(lore);
				}
			} else {
				List<String> lore = new ArrayList<>();
				lore.add(" ");
				lore.add("&f&cEnchants");
				for (EnchantHolder enchant : enchantDeque) {
					lore.add(enchant.enchant.lore().replaceAll("%level%", String.valueOf(enchant.level)));
				}
				meta.setLore(lore);
			}
		}
	}

	private void updateDeque() {
		container.getKeys().forEach(key -> {
			final EnchantHolder holder = container.get(key, adapter);
			if (holder != null) {
				enchantDeque.add(holder);
			}
		});
	}

	public void giveItem(Player player) {
		player.getInventory().addItem(itemStack);
	}

	/**
	 *
	 * @param startC This will function as the starting chance or cost variable.
	 * @param maxC This will function as the maximum chance or cost variable.
	 * @param current This will be the current level of the players enchant.
	 * @param max This will be the maximum level of the players enchant.
	 *
	 * @return The return value will be the calculated chance of the enchantment.
	 *
	 */
	private double handleLog(double startC, double maxC, int current, int max) {
		final double logValue = Math.log(current) / Math.log(max);
		double currentChance = startC + (maxC - startC) * logValue;
		currentChance = Math.max(startC, currentChance);
		currentChance = Math.min(maxC, currentChance);
		return currentChance;
	}
}
