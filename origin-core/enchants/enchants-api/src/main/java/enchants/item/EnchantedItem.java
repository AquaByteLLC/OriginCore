package enchants.item;

import enchants.EnchantAPI;
import enchants.config.EnchantsConfig;
import enchants.records.OriginEnchant;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
			final OriginEnchant enchant = OriginEnchant.enchantRegistry.get(enchantKey);
			if (OriginEnchant.canEnchant(pdc)) {
				if (enchant.maxLevel() <= level) {
					pdc.set(enchantKey, PersistentDataType.INTEGER, enchant.maxLevel());
				} else {
					pdc.set(enchantKey, PersistentDataType.INTEGER, level);
				}
			}
		});
		updateLore();
	}

	public void makeEnchantable() {
		writeContainer(pdc -> pdc.set(OriginEnchant.requiredKey, PersistentDataType.BOOLEAN, true));
	}

	public void removeEnchant(NamespacedKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		writeContainer(pdc -> {
			if (OriginEnchant.canEnchant(pdc)) {
				pdc.remove(enchantKey);
			}
		});
		updateLore();
	}

	public void removeAllEnchants() {
		if (OriginEnchant.canEnchant(readContainer())) {
			if (!readContainer().getKeys().isEmpty())
				readContainer().getKeys().forEach(this::removeEnchant);
			updateLore();
		}
	}


	@SuppressWarnings("DuplicatedCode,DataFlowIssue")
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

	@SuppressWarnings("DuplicatedCode,DataFlowIssue")
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
	@SuppressWarnings("DataFlowIssue")
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
			final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

			if (lore == null) return;

			final List<String> header = EnchantAPI.get().getInstance(EnchantsConfig.class).getEnchantHeader();
			final ArrayDeque<String> deque = new ArrayDeque<>(header);

			if (new HashSet<>(lore).containsAll(deque)) {
				lore.removeIf(string -> lore.indexOf(Text.colorize(string)) >= lore.indexOf(deque.getLast()));
			}

			lore.addAll(deque);

			for (NamespacedKey enchantKey : readContainer().getKeys()) {
				final int level = getLevel(enchantKey);
				final OriginEnchant enchant = OriginEnchant.enchantRegistry.get(enchantKey);
				lore.add(Text.colorize(enchant.lore())
						.replaceAll("%level%", Integer.toString(level))
						.replaceAll("%name%", enchant.name()));
			}

			meta.setLore(lore);
		});
	}

	public boolean activate(NamespacedKey key) {
		if (!hasEnchant(key)) return false;
		final Random random = new Random();
		final int randomNumber = random.nextInt(100);
		final double chance = getChance(key);
		return randomNumber <= chance;
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