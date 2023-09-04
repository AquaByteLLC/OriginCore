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

public enum FarmingActions {
	REGION_REGISTER("region_register",
			List.of("&aThe region {region_name} has been registered with the block {block_name}."),
			List.of("&aThe region {region_name} has been registered with the block {block_name}."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	REGION_REMOVE("region_remove",
			List.of("&aThe region {region_name} was removed."),
			List.of("&aThe region {region_name} was removed."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	BLOCK_NOT_FOUND("block_not_found",
			List.of("&cThe block {block_name} was not found!"),
			List.of("&cThe block {block_name} was not found!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	REGION_NOT_REGISTERED("region_not_registered",
			List.of("&cThe region {region_name} is not registered at the moment."),
			List.of("&cThe region {region_name} is not registered at the moment."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	WG_REGION_NOT_FOUND("wg_region_not_found",
			List.of("&cThere is no region with the name {region_name} registered in World Guard."),
			List.of("&cThere is no region with the name {region_name} registered in World Guard."),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	FIRE_FEET_ENCHANT_ACTIVATION("fire_feet_activation",
			List.of("&aThe &cFireFeet &aenchant has been activated!"),
			List.of("&aThe &cFireFeet &aenchant has been activated!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	FIRE_FEET_ENCHANT_DEACTIVATION("fire_feet_deactivation",
			List.of("&aThe &cFireFeet &aenchant has been deactivated!"),
			List.of("&aThe &cFireFeet &aenchant has been dactivated!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	EXPLOSIVE_ENCHANT_ACTIVATION("explosive_activation",
			List.of("&aThe &cExplosive &aenchant has been activated!"),
			List.of("&aThe &cExplosive &aenchant has been activated!"),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
			new EffectParticle(Particle.CLOUD, 10),
			new EffectParticle(Particle.CLOUD, 10)
	),
	;

	@Getter private final ConfigurableMessage message;
	@Getter private final ConfigurableEffectGroup effects;
	@Getter private final SettableConfig effectsCfg = new SettableConfig("effects.yml", "general", FarmingPlugin.getInjector().getInstance(FarmingPlugin.class));
	@Getter private final SettableConfig messageCfg = new SettableConfig("messages.yml", "general", FarmingPlugin.getInjector().getInstance(FarmingPlugin.class));
	@Getter private final VersionSender messageSender = new VersionSender(messageCfg.getFile(), messageCfg.getFileConfiguration());
	@Getter private final VersionSender effectsSender = new VersionSender(effectsCfg.getFile(), effectsCfg.getFileConfiguration());

	FarmingActions(String key, List<String> legacyMessage, List<String> modernMessage, EffectSound soundEffectLegacy, EffectSound soundEffectNonLegacy, EffectParticle particleEffectLegacy, EffectParticle particleEffectNonLegacy) {
		this.message = new ConfigurableMessage(messageSender, key, legacyMessage, modernMessage);
		this.effects = new ConfigurableEffectGroup(effectsSender, key, soundEffectLegacy, soundEffectNonLegacy, particleEffectLegacy, particleEffectNonLegacy);
	}

	public static void send(FarmingActions action, Player player, Placeholder placeholder) {
		action.getMessageSender().sendMessage(player, action.getMessage(), placeholder);
		action.getEffectsSender().sendEffect(player, action.getEffects());
	}

	public static void init() {}
}
