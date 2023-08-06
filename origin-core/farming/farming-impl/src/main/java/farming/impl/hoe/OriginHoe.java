package farming.impl.hoe;

import commons.util.StringUtil;
import enchants.impl.item.EnchantedItemImpl;
import enchants.item.EnchantedItem;
import farming.impl.FarmingPlugin;
import farming.impl.conf.GeneralConfig;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.Placeholders;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OriginHoe {

	private static final NamespacedKey BLOCKS_BROKEN = new NamespacedKey("farming", "hoe.blocks.broken");
	private static final NamespacedKey ORIGIN_HOE = new NamespacedKey("farming", "hoe.tool");

	private final ItemStack baseItem;
	private final EnchantedItem enchantItem;
	private static final GeneralConfig cfg = FarmingPlugin.lfc.open(GeneralConfig.class);

	public OriginHoe() {
		this.baseItem = cfg.getHoe();
		this.enchantItem = new EnchantedItemImpl(baseItem);
	}

	public void give(Player player) {
		enchantItem.makeEnchantable();

		baseItem.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();
			pdc.set(BLOCKS_BROKEN, PersistentDataType.INTEGER, 0);
			pdc.set(ORIGIN_HOE, PersistentDataType.STRING, "true");

			List<String> loreList = cfg.getConfiguration().getStringList("hoe.item.lore");
			List<String> newList = new ArrayList<>();

			System.out.println("THE OLD LORE : " + loreList);
			for (String s : loreList) {
				newList.add(ChatColor.translateAlternateColorCodes('&', s));
			}

			System.out.println("THE NEW LORE: " + newList);

			meta.setLore(newList);
			meta.setDisplayName(Text.colorize(cfg.getConfiguration().getString("hoe.item.name")));
		});

		updateHoe(baseItem);

		System.out.println("It is a hoe: " + isHoe(baseItem));

		player.getInventory().addItem(baseItem);
	}

	public static boolean isHoe(ItemStack stack) {
		return stack.getItemMeta().getPersistentDataContainer().has(ORIGIN_HOE);
	}

	public static void updateBlocksBroken(ItemStack stack, int newBroken) {
		stack.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();
			if (pdc.has(BLOCKS_BROKEN)) {
				final int current = pdc.get(BLOCKS_BROKEN, PersistentDataType.INTEGER);
				pdc.set(BLOCKS_BROKEN, PersistentDataType.INTEGER, (current + newBroken));
			} else {
				pdc.set(BLOCKS_BROKEN, PersistentDataType.INTEGER, 0);
			}
		});
		System.out.println("Is it a hoe? " + isHoe(stack));
		updateHoe(stack);
	}

	public static void updateHoe(ItemStack stack) {
		if (!isHoe(stack)) return;
		stack.editMeta(meta -> {
			List<String> loreList = new ArrayList<>(cfg.getConfiguration().getStringList("hoe.item.lore"));
			final String displayName = Text.colorize(cfg.getConfiguration().getString("hoe.item.name"));

			EnchantedItem enchantedItem = new EnchantedItemImpl(stack);
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			Placeholder placeholder = StringPlaceholder.builder()
					.set("enchants", String.join("\n", enchantedItem.getEnchants()))
					.set("enchant_count", String.valueOf(enchantedItem.getEnchants().size()))
					.set("blocks_broken", String.valueOf(pdc.get(BLOCKS_BROKEN, PersistentDataType.INTEGER)))
					.set("skin_equipped", "None")
					.set("augment_count", String.valueOf(0))
					.set("augments", "None")
					.set("prestige_boosts", "None")
					.build();

			System.out.println(pdc.get(BLOCKS_BROKEN, PersistentDataType.INTEGER));

			loreList = Placeholders.reformat(placeholder, loreList).stream()
					.flatMap(it -> Arrays.stream(it.split("\n")))
					.map(StringUtil::colorize).collect(Collectors.toList());

			meta.setLore(loreList);
			meta.setDisplayName(placeholder.format(displayName));
		});
	}
}
