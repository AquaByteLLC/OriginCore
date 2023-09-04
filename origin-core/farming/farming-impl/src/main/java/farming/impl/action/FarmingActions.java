package farming.impl.action;

import commons.conf.SettableConfig;
import commons.versioning.VersionSender;
import commons.versioning.impl.ConfigurableEffectGroup;
import commons.versioning.impl.ConfigurableMessage;
import farming.impl.FarmingPlugin;
import lombok.Getter;
import me.vadim.util.conf.bukkit.wrapper.EffectParticle;
import me.vadim.util.conf.bukkit.wrapper.EffectSound;
import me.vadim.util.conf.wrapper.Placeholder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

import static farming.impl.action.FarmingActions.Senders.*;

public enum FarmingActions {
	REGION_REGISTER("region_register",
			List.of("&2&l[!] &aThe region &f{region_name} &ahas been successfully registered with block type &f{block_name}&a."), // legacy colors
			List.of("&#3cdd3c&l[!] &#93ec93The region &f{region_name}&#93ec93 has been successfully registered with block type &f{block_name}&#93ec93."), // modern (hex) colors
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	REGION_REMOVE("region_remove",
			List.of("&6&l[!] &eYou have successfully removed the &f{region_name}&e region."),
			List.of("&#e7c932[!] &#ecd55fYou have successfully removed the &f{region_name}&#ecd55f region."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	BLOCK_NOT_FOUND("block_not_found",
			List.of("&4&l[!] &cThe block &f{block_name}&c was not found!"),
			List.of("&#dd3c3c&l[!] &#e46767The block &f{block_name}&#e46767 was not found!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	REGION_NOT_REGISTERED("region_not_registered",
			List.of("&4&l[!] &cThe &f{region_name}&c region hasn't been registered yet!"),
			List.of("&#dd3c3c&l[!] &#e46767The &f{region_name}&#e46767 region hasn't been registered yet!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	WG_REGION_NOT_FOUND("wg_region_not_found",
			List.of("&4&l[!] &cNo region with the name &f{region_name}&c could be found in WorldGuard."),
			List.of("&#dd3c3c&l[!] &#e46767No region with the name &f{region_name}&#e46767 could be found in WorldGuard."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	FIRE_FEET_ENCHANT_ACTIVATION("fire_feet_activation",
			List.of("&6&l[!] &eYour &f{name}&e enchantment has activated, burning crops below your feet for the next &f{length} {unit}&e!"),
			List.of("&#f37324&l[!] &#f9b286Your &f{name}&#f9b286 enchantment has activated, burning crops below your feet for the next &f{length} {unit}&#f9b286!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	FIRE_FEET_ENCHANT_DEACTIVATION("fire_feet_deactivation",
			List.of("&4&l[!] &cYour &f{name}&c enchantment has worn off."),
			List.of("&#dd3c3c&l[!] &#e46767Your &f{name}&#e46767 enchantment has worn off."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	EXPLOSIVE_ENCHANT_ACTIVATION("explosive_activation",
			List.of("&4&l[!] &cYour &f{name} &cenchantment has activated, vaporizing all crops within a &f{radius} block &cradius!"),
			List.of("&#cc1d0a&l[!] &#f65846Your &f{name} &#f65846enchantment has activated, vaporizing all crops within a &f{radius} block &#f65846radius!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	;

	protected static class Senders {
		@Getter
		public static SettableConfig effectsCfg = new SettableConfig("effects.yml", "general", FarmingPlugin.getInjector().getInstance(FarmingPlugin.class));
		@Getter
		public static SettableConfig messageCfg = new SettableConfig("messages.yml", "general", FarmingPlugin.getInjector().getInstance(FarmingPlugin.class));
		@Getter
		public static VersionSender messageSender = new VersionSender(messageCfg.getFile(), messageCfg.getFileConfiguration());
		@Getter
		public static VersionSender effectsSender = new VersionSender(effectsCfg.getFile(), effectsCfg.getFileConfiguration());
	}

	@Getter
	private final ConfigurableMessage message;
	@Getter
	private final ConfigurableEffectGroup effects;

	FarmingActions(String key, List<String> legacyMessage, List<String> modernMessage, EffectSound soundEffectLegacy, EffectSound soundEffectNonLegacy, EffectParticle particleEffectLegacy, EffectParticle particleEffectNonLegacy) {
		this.message = new ConfigurableMessage(messageSender, key, legacyMessage, modernMessage);
		this.effects = new ConfigurableEffectGroup(effectsSender, key, soundEffectLegacy, soundEffectNonLegacy, particleEffectLegacy, particleEffectNonLegacy);
	}

	public static void send(FarmingActions action, Player player, Placeholder placeholder) {
		messageSender.sendMessage(player, action.getMessage(), placeholder);
		effectsSender.sendEffect(player, action.getEffects());
	}

	public static void init() {}
}
