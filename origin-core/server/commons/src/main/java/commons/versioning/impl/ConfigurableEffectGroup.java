package commons.versioning.impl;

import commons.versioning.VersionSender;
import commons.versioning.api.EffectGroupVersioned;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.bukkit.wrapper.EffectParticle;
import me.vadim.util.conf.bukkit.wrapper.EffectSound;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class ConfigurableEffectGroup implements EffectGroupVersioned {
	private final VersionSender sender;
	private final String effectKey;
	private final FileConfiguration cfg;
	private final EffectGroup legacy;
	private final EffectGroup nonLegacy;

	public ConfigurableEffectGroup(VersionSender sender,
	                               String effectKey,
	                               EffectSound soundEffectLegacy,
	                               EffectSound soundEffectNonLegacy,
	                               EffectParticle particleEffectLegacy,
	                               EffectParticle particleEffectNonLegacy) {
		this.sender = sender;
		this.effectKey = effectKey;
		this.cfg = sender.cfg();

		EffectGroupPaths.createSectionsAndCreateKey(sender, effectKey, cfg, soundEffectLegacy,
				soundEffectNonLegacy,
				particleEffectLegacy,
				particleEffectNonLegacy);

		this.legacy = createGroup(true);
		this.nonLegacy = createGroup(false);

		if (legacy == null || nonLegacy == null)
			throw new RuntimeException("The group with the key " + effectKey + " was null, please check the configuration!");
	}


	@Override
	public EffectGroup legacy() {
		return this.legacy;
	}

	@Override
	public EffectGroup nonLegacy() {
		return this.nonLegacy;
	}

	public interface EffectGroupPaths {

		// BASES
		String effects = "Effects";
		String base = effects + ".%key%";
		String legacy = base + ".legacy";
		String nonLegacy = base + ".nonLegacy";

		String soundsSectionLegacy = legacy + ".sounds";
		String soundsSectionNonLegacy = nonLegacy + ".sounds";

		String particlesSectionLegacy = legacy + ".particles";
		String particlesSectionNonLegacy = nonLegacy + ".particles";

		String soundTypeLegacy  = soundsSectionLegacy  + ".type";
		String soundTypeNonLegacy  = soundsSectionNonLegacy  + ".type";

		String volumeLegacy = soundsSectionLegacy + ".volume";
		String volumeNonLegacy = soundsSectionNonLegacy + ".volume";

		String pitchLegacy = soundsSectionLegacy + ".pitch";
		String pitchNonLegacy = soundsSectionNonLegacy + ".pitch";

		String particleTypeLegacy = particlesSectionLegacy + ".type";
		String particleTypeNonLegacy = particlesSectionNonLegacy + ".type";

		String particleCountLegacy = particlesSectionLegacy + ".count";
		String particleCountNonLegacy = particlesSectionNonLegacy + ".count";

		static String getAndReplace(String current, String enchantKey) {
			return current.replaceAll("%key%", enchantKey);
		}

		static String getAsRelative(String global) {
			return global.substring(global.lastIndexOf('.') + 1);
		}

		static ConfigurationSection getOrCreate(ConfigurationSection section, String path) {
			return section.isConfigurationSection(path) ? section.getConfigurationSection(path) : section.createSection(path);
		}

		static void createSectionsAndCreateKey(VersionSender sender, String key, FileConfiguration configuration, EffectSound soundEffectLegacy, EffectSound soundEffectNonLegacy, EffectParticle particleEffectLegacy, EffectParticle particleEffectNonLegacy) {
			final String particleSectionLegacy = getAndReplace(particlesSectionLegacy, key);
			final String particleSectionNonLegacy = getAndReplace(particlesSectionNonLegacy, key);

			final String soundsSectionLegacy = getAndReplace(EffectGroupPaths.soundsSectionLegacy, key);
			final String soundsSectionNonLegacy = getAndReplace(EffectGroupPaths.soundsSectionNonLegacy, key);

			final String legacyBaseReplaced = getAndReplace(legacy, key);
			final String nonLegacyBaseReplaced = getAndReplace(nonLegacy, key);
			final String baseSectionReplaced = getAndReplace(base, key);

			if (configuration.isConfigurationSection(baseSectionReplaced)) return;

			final ConfigurationSection effectsSectionCfg = getOrCreate(configuration, effects);

			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection legacySection = getOrCreate(configuration, legacyBaseReplaced);
			final ConfigurationSection nonLegacySection = getOrCreate(configuration, nonLegacyBaseReplaced);

			final ConfigurationSection soundsSectionLegacyCfg = getOrCreate(configuration, soundsSectionLegacy);
			final ConfigurationSection soundsSectionNonLegacyCfg = getOrCreate(configuration, soundsSectionNonLegacy);

			final ConfigurationSection particlesSectionLegacyCfg = getOrCreate(configuration, particleSectionLegacy);
			final ConfigurationSection particlesSectionNonLegacyCfg = getOrCreate(configuration, particleSectionNonLegacy);

			soundsSectionLegacyCfg.set(getAsRelative(soundTypeLegacy), soundEffectLegacy.sound.toString());
			soundsSectionNonLegacyCfg.set(getAsRelative(soundTypeNonLegacy), soundEffectNonLegacy.sound.toString());

			soundsSectionLegacyCfg.set(getAsRelative(volumeLegacy), soundEffectLegacy.volume);
			soundsSectionNonLegacyCfg.set(getAsRelative(volumeNonLegacy), soundEffectNonLegacy.volume);

			soundsSectionLegacyCfg.set(getAsRelative(pitchLegacy), soundEffectLegacy.pitch);
			soundsSectionNonLegacyCfg.set(getAsRelative(pitchNonLegacy), soundEffectNonLegacy.pitch);

			particlesSectionLegacyCfg.set(getAsRelative(particleTypeLegacy), particleEffectLegacy.particle.toString());
			particlesSectionNonLegacyCfg.set(getAsRelative(particleTypeNonLegacy), particleEffectNonLegacy.particle.toString());

			particlesSectionLegacyCfg.set(getAsRelative(particleCountLegacy), particleEffectLegacy.count);
			particlesSectionNonLegacyCfg.set(getAsRelative(particleCountNonLegacy), particleEffectNonLegacy.count);

			try {
				configuration.save(sender.file());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getCount(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.particleCountLegacy : EffectGroupPaths.particleCountNonLegacy;
		final String particleCount = EffectGroupPaths.getAndReplace(path, effectKey);
		return cfg.getInt(particleCount);
	}

	public float getVolume(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.volumeLegacy : EffectGroupPaths.volumeNonLegacy;
		final String volume = EffectGroupPaths.getAndReplace(path, effectKey);
		return (float) cfg.getDouble(volume);
	}

	public float getPitch(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.pitchLegacy : EffectGroupPaths.pitchNonLegacy;
		final String pitch = EffectGroupPaths.getAndReplace(path, effectKey);
		return (float) cfg.getDouble(pitch);
	}

	public Particle getParticleType(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.particleTypeLegacy : EffectGroupPaths.particleTypeNonLegacy;
		final String type = EffectGroupPaths.getAndReplace(path, effectKey);
		return Particle.valueOf(cfg.getString(type));
	}

	public Sound getSoundType(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.soundTypeLegacy : EffectGroupPaths.soundTypeNonLegacy;
		final String type = EffectGroupPaths.getAndReplace(path, effectKey);
		return Sound.valueOf(cfg.getString(type));
	}

	public EffectGroup createGroup(boolean legacy) {
		return new EffectGroup(
				new EffectSound(getSoundType(legacy), getVolume(legacy), getPitch(legacy)),
				new EffectParticle(getParticleType(legacy), getCount(legacy))
		);
	}
}
