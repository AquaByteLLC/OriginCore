package enchants.impl.item;

import commons.BukkitUtil;
import commons.StringUtil;
import enchants.EnchantAPI;
import enchants.impl.conf.EnchantPlaceholder;
import enchants.item.EnchantFactory;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.item.Enchant;
import enchants.item.EnchantedItem;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class EnchantedItemImpl implements EnchantedItem {

	private static final NamespacedKey reqKey = new NamespacedKey("enchants", "_enchantable");
	private static final String reqValue = "isEnchantable";

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

	@Override
	public ItemStack formatMenuItemFor(EnchantKey key) {
		Enchant enchant = getRegistry().getByKey(key);

		Placeholder pl = EnchantPlaceholder.builder()
										   .set("maxLevel", StringUtil.formatNumber(enchant.getMaxLevel()))
										   .set("maxChance", StringUtil.formatNumber(enchant.getMaxChance()))
										   .set("maxCost", StringUtil.formatNumber(enchant.getMaxCost()))
										   .set("currentLevel", StringUtil.formatNumber(getLevel(key)))
										   .set("currentChance", StringUtil.formatNumber(getChance(key)))
										   .set("currentCost", StringUtil.formatNumber(getCost(key)))
										   .set("name", key.getName())
										   .build();

		ItemStack item = enchant.getMenuItem();
		BukkitUtil.formatItem(pl, item);
		return item;
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
		if(!enchant.targetsItem(itemStack.getType()))
			return;
		writeContainer(pdc -> {
			if (canEnchant(pdc))
				pdc.set(enchantKey.getNamespacedKey(), PersistentDataType.INTEGER, Math.min(enchant.getMaxLevel(), level));
		});
		updateMeta();
	}

	@Override
	public void removeEnchant(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		writeContainer(pdc -> pdc.remove(enchantKey.getNamespacedKey()));
		updateMeta();
	}

	@Override
	public void removeAllEnchants() {
		PersistentDataContainer pdc = readContainer();
		if (canEnchant(pdc)) {
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
		writeContainer(pdc -> setCanEnchant(pdc, true));
	}

	@Override
	public boolean isEnchantable() {
		return canEnchant(readContainer());
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

	public static double calc(Enchant holder, Enchant.ProgressionType type, int lvl, double start, double max) {
		final int maxLvl = holder.getMaxLevel();

		if (maxLvl == 1)
			return max;
		else {
			switch (type) {
				case EXPONENTIAL -> {
					return start * Math.pow(max / start, (double) (lvl - 1) / (maxLvl - 1));
				}
				case LOGARITHMIC -> {
					return handleLog(start, max, lvl, maxLvl);
				}
			}
			return 0.0;
		}
	}

	@SneakyThrows
	@Override
	public double getChance(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getChanceType(), getLevel(enchantKey), holder.getStartChance(), holder.getMaxChance());
	}

	@SneakyThrows
	@Override
	public double getCost(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0.0;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getCostType(), getLevel(enchantKey), holder.getStartCost(), holder.getMaxCost());
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
							 .replaceAll("%name%", enchant.getKey().getName()));
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
	private static double handleLog(double startC, double maxC, int current, int max) {
		final double logValue = Math.log(current) / Math.log(max);
		double currentChance = startC + (maxC - startC) * logValue;
		currentChance = Math.max(startC, currentChance);
		currentChance = Math.min(maxC, currentChance);
		return currentChance;
	}

	public static boolean canEnchant(ItemStack item) {
		return item.hasItemMeta() && canEnchant(item.getItemMeta().getPersistentDataContainer());
	}

	@SuppressWarnings("DataFlowIssue")
	public static boolean canEnchant(PersistentDataContainer container) {
		return container.has(reqKey) && container.get(reqKey, PersistentDataType.STRING).equals(reqValue);
	}

	public static void setCanEnchant(ItemStack item, boolean canEnchant) {
		if(!item.hasItemMeta())
			return;
		item.editMeta(meta -> setCanEnchant(meta.getPersistentDataContainer(), canEnchant));
	}

	public static void setCanEnchant(PersistentDataContainer container, boolean canEnchant) {
		if(canEnchant)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}

}