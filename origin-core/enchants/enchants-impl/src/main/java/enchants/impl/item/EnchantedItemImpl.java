package enchants.impl.item;

import enchants.EnchantAPI;
import enchants.item.EnchantFactory;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.item.Enchant;
import enchants.item.EnchantedItem;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class EnchantedItemImpl implements EnchantedItem {

	// static abuse :sillychamp:

	private static EnchantRegistry getRegistry() {
		return EnchantAPI.get().getInstance(EnchantRegistry.class);
	}

	private static EnchantFactory getFactory() {
		return EnchantAPI.get().getInstance(EnchantFactory.class);
	}
	
	private final ItemStack itemStack;

	public EnchantedItemImpl(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}

	private PersistentDataContainer readContainer() {
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	private void writeContainer(Consumer<PersistentDataContainer> consumer) {
		itemStack.editMeta(meta -> consumer.accept(meta.getPersistentDataContainer()));
	}

	@Override
	public void addEnchant(EnchantKey enchantKey, int level) {
		final Enchant enchant = getRegistry().getByKey(enchantKey);
		writeContainer(pdc -> {
			if (getFactory().canEnchant(pdc)) {
				if (enchant.getMaxLevel() <= level) {
					pdc.set(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER, enchant.getMaxLevel());
					System.out.println(pdc.getKeys());
				} else {
					pdc.set(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER, level);
				}
			}
		});
		updateMeta();
	}

	@Override
	public void removeEnchant(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		writeContainer(pdc -> {
			if (getFactory().canEnchant(pdc))
				pdc.remove(enchantKey.getNamespacedKey());
		});
		updateMeta();
	}

	@Override
	public void removeAllEnchants() {
		PersistentDataContainer pdc = readContainer();
		if (getFactory().canEnchant(pdc)) {
			Set<EnchantKey> keys = getAllEnchants();
			if (!keys.isEmpty())
				keys.forEach(this::removeEnchant);
			updateMeta();
		}
	}

	@Override
	public boolean hasEnchant(EnchantKey enchantKey) {
		return readContainer().has(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER);
	}

	@Override
	public void makeEnchantable() {
		writeContainer(pdc -> getFactory().setCanEnchant(pdc, true));
	}

	@Override
	public boolean isEnchantable() {
		return getFactory().canEnchant(readContainer());
	}

	@Override
	public Set<EnchantKey> getAllEnchants() {
		Set<EnchantKey> enchants = new HashSet<>();
		for (NamespacedKey nsk : readContainer().getKeys()) {
			EnchantKey key = getRegistry().adaptKey(nsk);
			if(key == null) continue;
			enchants.add(key);
		}
		return enchants;
	}

	@SuppressWarnings("all")
	@SneakyThrows
	@Override
	public double getChance(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		Enchant holder = getRegistry().getByKey(enchantKey);
		Enchant.ProgressionType chanceType = holder.getChanceType();
		final double startChance = holder.getStartChance();
		final double maxChance = holder.getMaxChance();
		final int maxLvl = holder.getMaxLevel();
		final int currentLvl = readContainer().get(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER);

		if (maxLvl == 1)
			return maxChance;
		else {
			switch (chanceType) {
				case EXPONENTIAL -> {
					return startChance * Math.pow(maxChance / startChance, (double) (currentLvl - 1) / (maxLvl - 1));
				}
				case LOGARITHMIC -> {
					return handleLog(startChance, maxChance, currentLvl, maxLvl);
				}
			}
			return 0.0;
		}
	}

	@SuppressWarnings("all")
	@SneakyThrows
	@Override
	public double getCost(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		Enchant holder = getRegistry().getByKey(enchantKey);

		Enchant.ProgressionType costType = holder.getCostType();
		final double startCost = holder.getStartCost();
		final double maxCost = holder.getMaxCost();
		final int maxLvl = holder.getMaxLevel();
		final int currentLvl = readContainer().get(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER);

		if (maxLvl == 1)
			return maxCost;
		else {
			switch (costType) {
				case EXPONENTIAL -> {
					return startCost * Math.pow(maxCost / startCost, (double) (currentLvl - 1) / (maxLvl - 1));
				}
				case LOGARITHMIC -> {
					return handleLog(startCost, maxCost, currentLvl, maxLvl);
				}
			}
			return 0.0;
		}
	}

	@SuppressWarnings("all")
	@SneakyThrows
	@Override
	public int getLevel(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0;
		return readContainer().get(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER);
	}

	private void updateMeta() {
		itemStack.editMeta(meta -> {
			final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

			if (lore == null) return;

			final List<String> header = EnchantAPI.getGeneralConfig().getStringList("enchantHeader");

			if (lore.contains(Text.colorize(header.get(0))))
				lore.removeIf(string -> lore.indexOf(Text.colorize(string)) >= lore.indexOf(Text.colorize(header.get(0))));

			Set<EnchantKey> keys = getAllEnchants();

			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if (!keys.isEmpty()) {
				meta.addEnchant(Enchantment.OXYGEN, 1, true);
				for (String str : header)
					lore.add(Text.colorize(str));
			} else {
				meta.removeEnchant(Enchantment.OXYGEN);
			}

			for (EnchantKey key : keys) {
				final Enchant enchant = getRegistry().getByKey(key);
				final int level = getLevel(key);

				lore.add(Text.colorize(enchant.getLore())
							 .replaceAll("%level%", Integer.toString(level))
							 .replaceAll("%name%", enchant.getName()));
			}

			meta.setLore(lore);
		});
	}

	@Override
	public boolean activate(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return false;
		final Random random = new Random();
		final int randomNumber = random.nextInt(100);
		final double chance = getChance(enchantKey);
		return randomNumber <= chance;
	}

	/**
	 * @param startC  This will function as the starting chance or cost variable.
	 * @param maxC    This will function as the maximum chance or cost variable.
	 * @param current This will be the current level of the players enchant.
	 * @param max     This will be the maximum level of the players enchant.
	 * @return The return value will be the calculated chance of the enchantment.
	 */
	private double handleLog(double startC, double maxC, int current, int max) {
		final double logValue = Math.log(current) / Math.log(max);
		double currentChance = startC + (maxC - startC) * logValue;
		currentChance = Math.max(startC, currentChance);
		currentChance = Math.min(maxC, currentChance);
		return currentChance;
	}
}