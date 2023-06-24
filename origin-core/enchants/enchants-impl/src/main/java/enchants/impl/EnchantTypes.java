package enchants.impl;

import commons.events.api.EventContext;
import commons.events.impl.EventSubscriber;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import commons.events.impl.packet.PacketEventSubscriber;
import enchants.item.Enchant;
import enchants.item.EnchantFactory;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.impl.item.EnchantedItemImpl;
import enchants.item.EnchantBuilder;
import enchants.item.EnchantedItem;
import net.minecraft.network.protocol.Packet;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public enum EnchantTypes implements EnchantKey {

	SPEED("Speed",
		  bukkit(BlockBreakEvent.class, (key, ctx, event) -> {
			  final ItemStack playersItem = event.getPlayer().getInventory().getItemInMainHand();

			  if (playersItem.getType().isAir()) return;

			  final EnchantedItem item = new EnchantedItemImpl(playersItem);

			  if (item.activate(key)) {
				  event.getPlayer().sendMessage(item.getChance(key) + "%");
				  event.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(10 * 20, 1));
			  }
		  }),
		  builder -> {
			builder.setStartChance(10.0)
				   .setMaxChance(100.0)
				   .setChanceType(Enchant.ProgressionType.EXPONENTIAL)
				   .setCostType(Enchant.ProgressionType.EXPONENTIAL);
		  }),

	;

	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final Consumer<EnchantBuilder> build;

	EnchantTypes(String name, EventSubscriber subscriber, Consumer<EnchantBuilder> build) {
		this.name       = name;
		this.key        = name2key(name);
		this.subscriber = subscriber;
		this.build      = build;
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

	private static <T extends Event> EventSubscriber bukkit(Class<T> clazz, SubscriberKeyConsumer<T> cons) {
		final int vf = v++;
		return new BukkitEventSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}

	private static <T extends Packet<?>> EventSubscriber packet(Class<T> clazz, SubscriberKeyConsumer<T> cons) {
		final int vf = v++;
		return new PacketEventSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}

	@FunctionalInterface
	private interface SubscriberKeyConsumer<T> {
		void consume(EnchantKey key, EventContext context, T event);
	}

	public static @Nullable EnchantKey fromName(String name) {
		for (EnchantTypes value : values())
			if(value.name.equalsIgnoreCase(name))
				return value;
		return null;
	}

	private static boolean init = false;
	public static void init(EnchantRegistry registry, EnchantFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (EnchantTypes value : values()) {
			EnchantBuilder builder = factory.newEnchantBuilder(value);
			value.build.accept(builder);
			registry.register(builder.build(value.subscriber));
		}
	}

}
