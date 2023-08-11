package tools.impl;

import commons.Commons;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.impl.ToolAugmentFactory;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.attribute.registry.impl.BaseAttributeRegistry;

public class ToolsPlugin extends ExtendedJavaPlugin {

	@Getter
	private static ToolsPlugin plugin;
	@Getter private BaseAttributeRegistry<Enchant> enchantRegistry;
	@Getter private CustomEnchantFactory enchantFactory;

	@Getter private BaseAttributeRegistry<Augment> augmentRegistry;
	@Getter private ToolAugmentFactory augmentFactory;
	private DetachedSubscriber<PlayerJoinEvent> playerJoinEventDetachedSubscriber;

	@Override
	protected void enable() {
		plugin = this;

		final EventRegistry registry = Commons.events();

		this.enchantRegistry = new BaseAttributeRegistry<>(registry);
		this.enchantFactory = new CustomEnchantFactory();

		playerJoinEventDetachedSubscriber = new DetachedSubscriber<>(PlayerJoinEvent.class, ((context, event) -> {
		}));
	}
}
