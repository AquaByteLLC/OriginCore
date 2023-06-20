package enchants.item;

import enchants.records.OriginEnchant;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnchantedItem {

	private final ItemStack itemStack;

	public EnchantedItem(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	private PersistentDataContainer readContainer() {
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	private void writeContainer(Consumer<PersistentDataContainer> consumer) {
		itemStack.editMeta(meta -> consumer.accept(meta.getPersistentDataContainer()));
	}

	public void addEnchant(NamespacedKey enchantKey, int level) {
		writeContainer(pdc -> {
			pdc.set(enchantKey, PersistentDataType.INTEGER, level);;
		});
		updateLore();
	}

	public void removeEnchant(NamespacedKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		itemStack.editMeta(meta -> {
			readContainer().remove(enchantKey);
		});
		updateLore();
	}

	public void removeAllEnchants() {
		if (!readContainer().getKeys().isEmpty())
			readContainer().getKeys().forEach(this::removeEnchant);
		updateLore();
	}

	@SuppressWarnings("all")
	@SneakyThrows
	public double getChance(NamespacedKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		OriginEnchant holder = OriginEnchant.enchantRegistry.get(enchantKey);
		OriginEnchant.EnchantProgressionType chanceType = holder.chanceType();
		final double startChance = holder.startChance();
		final double maxChance = holder.maxChance();
		final int maxLvl = holder.maxLevel();
		final int currentLvl = readContainer().get(enchantKey, PersistentDataType.INTEGER);

		if (maxLvl == 1) return maxChance;
		else {
			switch (chanceType) {
				case EXPONENTIAL -> {return startChance * Math.pow(maxChance / startChance, (double) (currentLvl - 1) / (maxLvl - 1));}
				case LOGARITHMIC -> {return handleLog(startChance, maxChance, currentLvl, maxLvl);}
			}
			return 0.0;
		}
	}

	@SuppressWarnings("all")
	@SneakyThrows
	public double getCost(NamespacedKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		OriginEnchant holder = OriginEnchant.enchantRegistry.get(enchantKey);

		OriginEnchant.EnchantProgressionType costType = holder.costType();
		final double startCost = holder.startCost();
		final double maxCost = holder.maxCost();
		final int maxLvl = holder.maxLevel();
		final int currentLvl = readContainer().get(enchantKey, PersistentDataType.INTEGER);

		if (maxLvl == 1) return maxCost;
		else {
			switch (costType) {
				case EXPONENTIAL -> {return startCost * Math.pow(maxCost / startCost, (double) (currentLvl - 1) / (maxLvl - 1));}
				case LOGARITHMIC -> {return handleLog(startCost, maxCost, currentLvl, maxLvl);}
			}
			return 0.0;
		}
	}
	@SuppressWarnings("all")
	@SneakyThrows
	public int getLevel(NamespacedKey enchantKey) {
		if (!hasEnchant(enchantKey)) throw new IllegalArgumentException("You aren't able to update this enchant because it hasn't been added to the item.");
		return readContainer().get(enchantKey, PersistentDataType.INTEGER);
	}

	public boolean hasEnchant(NamespacedKey enchant) {
		return readContainer().has(enchant, PersistentDataType.INTEGER);
	}

	private void updateLore() {
		itemStack.editMeta(meta -> {
			List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

			if (lore == null) return;

			if (!lore.contains("&c&lEnchants")) {
				lore.add(Text.colorize("&c&lEnchants"));
				lore.add(" ");
			}

			for (NamespacedKey enchantKey : readContainer().getKeys()) {
				final int level = getLevel(enchantKey);
				final OriginEnchant enchant = OriginEnchant.enchantRegistry.get(enchantKey);
				lore.add(Text.colorize(enchant.lore().replaceAll("%level%", String.valueOf(level))));
			}

			System.out.println(readContainer().getKeys());
			meta.setLore(lore);
		});
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
