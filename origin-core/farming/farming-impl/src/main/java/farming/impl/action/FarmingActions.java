package farming.impl.action;

import farming.impl.FarmingPlugin;
import farming.impl.conf.EffectsConfig;
import lombok.Getter;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.bukkit.wrapper.EffectParticle;
import me.vadim.util.conf.bukkit.wrapper.EffectSound;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public enum FarmingActions {
	REGION_REGISTER_SUCCESS("region_register_success"),
	REGION_REGISTER_ERROR("region_register_error"),
	REGION_REMOVE_SUCCESS("region_remove_success"),
	REGION_REMOVE_ERROR("region_remove_error"),
	EXPLOSION_ENCHANT_FINISH("explosion_enchant_finish"),
	EXPLOSION_ENCHANT_TICKING("explosion_enchant_ticking"),
	CROP_REGENERATING("crop_regenerating"),
	CROP_NOT_IN_REGION("crop_not_in_region"),

	;

	@Getter private final EffectGroup group;
	private final YamlConfiguration lfc = FarmingPlugin.lfc.open(EffectsConfig.class).getConfiguration();
	private final static String SOUND_PATH = "effects.sound.";
	private static final String PARTICLE_PATH = "effects.particle.";

	FarmingActions(String str) {
		final ConfigurationSection section = lfc.getConfigurationSection(str);

		final Sound soundType = Sound.valueOf(section.getString(SOUND_PATH + "type"));
		final float volume = (float) section.getDouble(SOUND_PATH + "volume");
		final float pitch = (float) section.getDouble(SOUND_PATH + "pitch");

		final Particle particleType = Particle.valueOf(section.getString(PARTICLE_PATH + "type"));
		final int particleCount = section.getInt(PARTICLE_PATH  + "count");

		final EffectSound sound = new EffectSound(soundType, volume, pitch);
		final EffectParticle particle = new EffectParticle(particleType, particleCount);
		this.group = new EffectGroup(sound, particle);
	}
	static void init() {}
}
