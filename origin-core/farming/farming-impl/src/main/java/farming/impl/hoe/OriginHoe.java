package farming.impl.hoe;

import farming.impl.FarmingPlugin;
import farming.impl.conf.GeneralConfig;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.enchants.impl.types.GeneralEnchantTypes;
import tools.impl.attribute.skins.impl.types.GeneralSkinTypes;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;
import tools.impl.tool.impl.SkinnedTool;

public class OriginHoe {

	private static final NamespacedKey BLOCKS_BROKEN = new NamespacedKey("farming", "hoe.blocks.broken");
	private static final NamespacedKey ORIGIN_HOE = new NamespacedKey("farming", "hoe.tool");

	private final ItemStack baseItem;
	private static final GeneralConfig cfg = FarmingPlugin.lfc.open(GeneralConfig.class);

	public OriginHoe() {
		this.baseItem = cfg.getHoe();
	}

	public void give(Player player) {
		ItemStack stack = UniqueItemBuilder.create(baseItem)
				.asSpecialTool(SkinnedTool.class, item -> {
					item.makeSkinnable();
					item.addSkin(GeneralSkinTypes.FLAMINGO_PICKAXE);
				}).asSpecialTool(EnchantedTool.class, item -> {
					item.makeEnchantable();
					item.addEnchant(GeneralEnchantTypes.PLAYER_SPEED_BOOST, 10);
				}).asSpecialTool(AugmentedTool.class, item -> {
					item.makeAugmentable(1);
				}).createCustomDataUpdate("gtb", "blocks", PersistentDataType.INTEGER, 0, BlockBreakEvent.class, (ctx, breakEvent) -> {
					final Player playea = breakEvent.getPlayer();
					final ItemStack playerHand = playea.getInventory().getItemInMainHand();

					if (playerHand.getItemMeta() == null) return;

					final UniqueItemBuilder temp = UniqueItemBuilder.fromStack(playerHand);
					final NamespacedKey key = new NamespacedKey("gtb", "blocks");

					if (playerHand.getItemMeta().getPersistentDataContainer().has(key)) {
						int current = temp.getData("gtb", "blocks", PersistentDataType.INTEGER);
						temp.createCustomData("gtb", "blocks", PersistentDataType.INTEGER, (current + 1));
					}

					final EnchantedTool tool = new EnchantedTool(playerHand);
					final AugmentedTool otherTool = new AugmentedTool(playerHand);
					final SkinnedTool anotherTool = new SkinnedTool(playerHand);

					UniqueItemBuilder.updateItem(playerHand, StringPlaceholder.builder()
							.set("enchants", String.join("\n", tool.getEnchants()))
							.set("augments", String.join("\n", otherTool.getAugments()))
							.set("skin", anotherTool.getSkin() == null ? "None applied" : ToolsPlugin.getPlugin().getSkinRegistry().getByKey(anotherTool.getSkin()).getAppliedLore())
							.set("blocks", String.valueOf(temp.getData("gtb", "blocks", PersistentDataType.INTEGER)))
							.build()
					);

				}).create().build();
		player.getInventory().addItem(stack);
		/*
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

		 */
	}

	/*

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

	 */
}
