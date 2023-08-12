package tools.impl.attribute.enchants.impl.types;

import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.Consumer3;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.attribute.registry.impl.BaseAttributeRegistry;
import tools.impl.target.ToolTarget;
import tools.impl.tool.impl.EnchantedTool;

public enum GeneralEnchantTypes implements AttributeKey {

	PLAYER_SPEED_BOOST("Speed Boost",
		subscribe(BlockBreakEvent.class, (key, ctx, event) -> {
			final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
			if (playersItem.getType().isAir()) return;
			final EnchantedTool item = new EnchantedTool(playersItem);

			if (item.activate(key)) {
				/*
				Modify player speed
				 */
				event.getPlayer().sendMessage(StringUtil.colorize("&eWorking!"));
			}
		}), ToolTarget.all());

	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final ToolTarget[] targets;

	GeneralEnchantTypes(String name, EventSubscriber subscriber, ToolTarget... targets) {
		this.name = name;
		this.key = name2key(name);
		this.subscriber = subscriber;
		this.targets = targets;
	}

	@Override
	public NamespacedKey getNamespacedKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	public static NamespacedKey name2key(String name) {
		return new NamespacedKey("enchants", "enchant." + ChatColor.stripColor(name.toLowerCase()).replace(' ', '_'));
	}

	/* we perform miniscule amount of static abuse */

	private static int v = 0;

	private static <T> EventSubscriber subscribe(Class<T> clazz, Consumer3<T> cons) {
		final int vf = v++;
		return new DetachedSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}

	public static @Nullable AttributeKey fromName(String name) {
		for (GeneralEnchantTypes value : values())
			if (value.name.equalsIgnoreCase(name))
				return value;
		return null;
	}

	private static boolean init = false;

	public static void init(BaseAttributeRegistry<Enchant> registry, CustomEnchantFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (GeneralEnchantTypes value : values())
			registry.register(factory.newAttributeBuilder(value).build(value.subscriber, value.targets));
	}
}
