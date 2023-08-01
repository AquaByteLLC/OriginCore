package enchants.impl.item;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import enchants.EnchantAPI;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.impl.conf.EnchantPlaceholder;
import enchants.item.Enchant;
import enchants.item.EnchantFactory;
import enchants.item.EnchantedItem;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	public void addEnchant(EnchantKey enchantKey, long level) {
		final Enchant enchant = getRegistry().getByKey(enchantKey);
		if(!enchant.targetsItem(itemStack.getType()))
			return;
		writeContainer(pdc -> {
			if (canEnchant(pdc))
				pdc.set(enchantKey.getNamespacedKey(), PersistentDataType.LONG, Math.min(enchant.getMaxLevel(), level));
		});
		// updateMeta();
	}

	@Override
	public void removeEnchant(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		writeContainer(pdc -> pdc.remove(enchantKey.getNamespacedKey()));
		// updateMeta();
	}

	@Override
	public void removeAllEnchants() {
		PersistentDataContainer pdc = readContainer();
		if (canEnchant(pdc)) {
			Set<EnchantKey> keys = getAllEnchants();
			if (!keys.isEmpty())
				keys.forEach(this::removeEnchant);
			// updateMeta();
		}
	}

	@Override
	public boolean hasEnchant(EnchantKey enchantKey) {
		return readContainer().has(enchantKey.getNamespacedKey(), PersistentDataType.LONG);
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

	public static BigDecimal calc(Enchant holder, Enchant.ProgressionType type, long lvl, BigDecimal start, BigDecimal max) {
		long maxLvl = holder.getMaxLevel();

		if (maxLvl == 1)
			return max;
		else {
			switch (type) {
				case EXPONENTIAL -> {
					//start * Math.pow(max / start, (double) (lvl - 1) / (maxLvl - 1))
					return start.multiply(max.divide(start, RoundingMode.HALF_EVEN).pow(Math.toIntExact((lvl - 1) / (maxLvl - 1))));
				}
				case LOGARITHMIC -> {
					return handleLog(start, max, lvl, maxLvl);
				}
			}
			return BigDecimal.ZERO;
		}
	}

	@SneakyThrows
	@Override
	public BigDecimal getChance(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return BigDecimal.ZERO;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getChanceType(), getLevel(enchantKey), holder.getStartChance(), holder.getMaxChance());
	}

	@SneakyThrows
	@Override
	public BigDecimal getCost(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return BigDecimal.ZERO;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getCostType(), getLevel(enchantKey), holder.getStartCost(), holder.getMaxCost());
	}

	@SuppressWarnings("all")
	@SneakyThrows
	@Override
	public long getLevel(EnchantKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0L;
		return readContainer().get(enchantKey.getNamespacedKey(), PersistentDataType.LONG);
	}

	public List<String> getEnchants() {
		final List<String> enchantList = new ArrayList<>();
		{
			final Set<EnchantKey> keys = getAllEnchants();

			for (EnchantKey key : keys) {
				final Enchant enchant = getRegistry().getByKey(key);
				final long level = getLevel(key);

				final Placeholder placeholder = StringPlaceholder.builder()
						.set("level", StringUtil.formatNumber(level))
						.set("name", enchant.getKey().getName())
						.build();

				enchantList.add(Text.colorize(placeholder.format(enchant.getLore())));
			}

		}
		return enchantList;
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
				final long level = getLevel(key);

				lore.add(Text.colorize(enchant.getLore())
							 .replaceAll("%level%", StringUtil.formatNumber(level))
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
		final BigDecimal chance = getChance(enchantKey);
		System.out.println(chance);
		System.out.println(chance.compareTo(BigDecimal.valueOf(randomNumber)));
		return (randomNumber <= chance.doubleValue()); // randomNumber <= chance;
	}

	/**
	 * @param startC  This will function as the starting chance or cost variable.
	 * @param maxC    This will function as the maximum chance or cost variable.
	 * @param current This will be the current level of the players enchant.
	 * @param max     This will be the maximum level of the players enchant.
	 * @return The return value will be the calculated chance of the enchantment.
	 */
	private static BigDecimal handleLog(BigDecimal startC, BigDecimal maxC, long current, long max) {
		final double logValue = Math.log(current) / Math.log(max);
		BigDecimal currentChance = startC.add(maxC.subtract(startC)).multiply(BigDecimal.valueOf(logValue)); // startC + (maxC - startC) * logValue;
		return currentChance.max(startC).min(maxC);
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