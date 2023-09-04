package farming.impl.hoe.enchants.abilities;

import blocks.BlocksAPI;
import blocks.block.builder.FixedAspectHolder;
import blocks.impl.events.BreakEvent;
import commons.util.StringUtil;
import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import farming.impl.action.FarmingActions;
import farming.impl.hoe.enchants.EnchantTypes;
import lombok.Getter;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Location;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import tools.impl.ToolsPlugin;
import tools.impl.ability.builder.impl.AbilityCreator;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.enchants.Enchant;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public enum Abilities {
	FIRE_FEET(creator -> {
		final ExpiringShelf<PlayerCachedAttribute<Enchant>> shelf = Shelf.<PlayerCachedAttribute<Enchant>>builder()
				.concurrent()
				.expiring()
				.usePolicy(TimedExpiringPolicy.create((cachedAttribute) -> {
					final Player player = cachedAttribute.getPlayer();
					final Enchant enchant = cachedAttribute.getAttribute();
					final FileConfiguration configuration = enchant.getConfig().getConf().getFileConfiguration();
					return new TimedExpiringPolicy.TimedExpirationData(TimeUnit.valueOf(configuration.getString("Ability.Unit")), configuration.getInt("Ability.Length"), false);
				}))
				.build();

		creator.setExpiringShelf(shelf);

		creator.setExpirationHandler(handler -> {
			final Player player = handler.getPlayer();
			final Enchant enchant = handler.getAttribute();
			final FileConfiguration configuration = enchant.getConfig().getConf().getFileConfiguration();

			final Placeholder pl = StringPlaceholder.builder()
					.set("name", enchant.getKey().getName())
					.set("length", configuration.getInt("Ability.Length"))
					.set("unit", StringUtil.convertToUserFriendlyCase(configuration.getString("Ability.Unit")))
					.build();

			FarmingActions.send(FarmingActions.FIRE_FEET_ENCHANT_DEACTIVATION, player, pl);
		});

		creator.setWhileInCache(PlayerMoveEvent.class, event -> {
			final Player player = event.getPlayer();
			final AttributeKey key = ToolsPlugin.getPlugin().getEnchantRegistry().adaptKey(EnchantTypes.FIRE_FEET.getNamespacedKey());

			if (key == null) return;

			final Enchant enchant = ToolsPlugin.getPlugin().getEnchantRegistry().getByKey(key);
			final Location prev = event.getFrom().getBlock().getLocation().add(0, 1, 0);
			final PlayerCachedAttribute<Enchant> playerCachedAttribute = PlayerCachedAttribute.of(Enchant.class, player, enchant);

			if (shelf.contains(playerCachedAttribute))
				if (BlocksAPI.inRegion(prev)) {
					final FixedAspectHolder fah = BlocksAPI.getBlock(prev);
					if (fah == null) return;
					if (fah.getBlock().getBlockData() instanceof Ageable ageable) {
						if (ageable.getAge() == ageable.getMaximumAge())
							new BreakEvent("farming", fah.getBlock(), player, true).callEvent();
					}
				}
		});
	});

	@Getter
	private final AbilityCreator<Enchant, PlayerCachedAttribute<Enchant>> abilityCreator;

	Abilities(Consumer<AbilityCreator<Enchant, PlayerCachedAttribute<Enchant>>> ability) {
		this.abilityCreator = new AbilityCreator<>();
		ability.accept(abilityCreator);
	}

	public static void init() {
		for (Abilities abilityCreators : values()) {
			abilityCreators.abilityCreator.build();
		}
	}
}
