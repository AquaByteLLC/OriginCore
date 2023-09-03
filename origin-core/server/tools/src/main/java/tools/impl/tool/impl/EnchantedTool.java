package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.EnchantBuilder;
import tools.impl.progression.ProgressionType;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.type.IEnchantedTool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EnchantedTool extends ToolBase<Enchant, IEnchantedTool, EnchantBuilder> implements IEnchantedTool {


	public EnchantedTool(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	protected AttributeRegistry<Enchant> getRegistry() {
		return ToolsPlugin.getPlugin().getEnchantRegistry();
	}

	@Override
	protected AttributeFactory<IEnchantedTool, EnchantBuilder> getFactory() {
		return ToolsPlugin.getPlugin().getEnchantFactory();
	}

	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public ItemStack formatMenuItemFor(AttributeKey key) {
		Enchant enchant = getRegistry().getByKey(key);

		Placeholder pl = StringPlaceholder.builder()
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

	@Override
	public void addEnchant(AttributeKey enchantKey, long level) {
		final Enchant enchant = getRegistry().getByKey(enchantKey);
		if (!enchant.targetsItem(itemStack.getType()))
			return;
		writeContainer(pdc -> {
			if (canEnchant(pdc))
				pdc.set(enchantKey.getNamespacedKey(), PersistentDataType.LONG, Math.min(enchant.getMaxLevel(), level));
		});
		// updateMeta();
	}

	@Override
	public void removeEnchant(AttributeKey enchantKey) {
		if (!hasEnchant(enchantKey)) return;
		writeContainer(pdc -> pdc.remove(enchantKey.getNamespacedKey()));
		// updateMeta();
	}

	@Override
	public void removeAllEnchants() {
		PersistentDataContainer pdc = readContainer();
		if (canEnchant(pdc)) {
			Set<AttributeKey> keys = getAllEnchants();
			if (!keys.isEmpty())
				keys.forEach(this::removeEnchant);
			// updateMeta();
		}
	}

	@Override
	public boolean hasEnchant(AttributeKey enchantKey) {
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
	public Set<AttributeKey> getAllEnchants() {
		Set<AttributeKey> enchants = new HashSet<>();
		for (NamespacedKey nsk : readContainer().getKeys()) {
			AttributeKey key = getRegistry().adaptKey(nsk);
			if (key == null) continue;
			enchants.add(key);
		}
		return enchants;
	}

	public static BigDecimal calc(Enchant holder, ProgressionType type, long lvl, BigDecimal start, BigDecimal max) {
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
	public BigDecimal getChance(AttributeKey enchantKey) {
		if (!hasEnchant(enchantKey)) return BigDecimal.ZERO;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getChanceType(), getLevel(enchantKey), holder.getStartChance(), holder.getMaxChance());
	}

	@SneakyThrows
	@Override
	public BigDecimal getCost(AttributeKey enchantKey) {
		if (!hasEnchant(enchantKey)) return BigDecimal.ZERO;
		Enchant holder = getRegistry().getByKey(enchantKey);
		return calc(holder, holder.getCostType(), getLevel(enchantKey), holder.getStartCost(), holder.getMaxCost());
	}

	@SuppressWarnings("all")
	@SneakyThrows
	@Override
	public long getLevel(AttributeKey enchantKey) {
		if (!hasEnchant(enchantKey)) return 0L;
		return readContainer().get(enchantKey.getNamespacedKey(), PersistentDataType.LONG);
	}

	public List<String> getEnchants() {
		final List<String> enchantList = new ArrayList<>();
		{
			final Set<AttributeKey> keys = getAllEnchants();

			for (AttributeKey key : keys) {
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

	/*
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

	 */

	@Override
	public boolean activate(AttributeKey enchantKey) {
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

	public static boolean canEnchant(PersistentDataContainer container) {
		return container.has(reqKey) && reqValue.equals(container.get(reqKey, PersistentDataType.STRING));
	}

	public static void setCanEnchant(ItemStack item, boolean canEnchant) {
		if (!item.hasItemMeta())
			return;
		item.editMeta(meta -> setCanEnchant(meta.getPersistentDataContainer(), canEnchant));
	}

	public static void setCanEnchant(PersistentDataContainer container, boolean canEnchant) {
		if (canEnchant)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}
}
