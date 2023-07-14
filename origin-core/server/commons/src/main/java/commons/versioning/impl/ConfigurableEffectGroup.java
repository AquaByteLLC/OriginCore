package commons.versioning.impl;

import commons.versioning.VersionSender;
import commons.versioning.api.EffectGroupVersioned;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.bukkit.wrapper.EffectParticle;
import me.vadim.util.conf.bukkit.wrapper.EffectSound;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurableEffectGroup implements EffectGroupVersioned {
	private final VersionSender sender;
	private final String effectKey;
	private final YamlConfiguration cfg;
	private final EffectGroup legacy;
	private final EffectGroup nonLegacy;

	public ConfigurableEffectGroup(VersionSender sender, String effectKey) {
		this.sender = sender;
		this.effectKey = effectKey;
		this.cfg = sender.cfg();

		EffectGroupPaths.createSectionsAndCreateKey(effectKey, cfg);

		this.legacy = createGroup(true);
		this.nonLegacy = createGroup(false);

		if (legacy == null || nonLegacy == null)
			throw new RuntimeException("The group with the key " + effectKey + " was null, please check the configuration!");
	}


	@Override
	public EffectGroup getLegacy() {
		return this.legacy;
	}

	@Override
	public EffectGroup getNonLegacy() {
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

		static void createSectionsAndCreateKey(String key, YamlConfiguration configuration) {
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

			assert effectsSectionCfg != null;
			assert baseSection != null;
			assert legacySection != null;
			assert nonLegacySection != null;
			assert soundsSectionNonLegacyCfg != null;
			assert soundsSectionLegacyCfg != null;
			assert particlesSectionLegacyCfg != null;
			assert particlesSectionNonLegacyCfg != null;

			soundsSectionLegacyCfg.set(getAsRelative(soundTypeLegacy), "ENTITY_PLAYER_LEVELUP");
			soundsSectionNonLegacyCfg.set(getAsRelative(soundTypeNonLegacy), "ENTITY_PLAYER_LEVELUP");

			soundsSectionLegacyCfg.set(getAsRelative(volumeLegacy), 1.0);
			soundsSectionNonLegacyCfg.set(getAsRelative(volumeNonLegacy), 1.0);

			soundsSectionLegacyCfg.set(getAsRelative(pitchLegacy), 1.0);
			soundsSectionNonLegacyCfg.set(getAsRelative(pitchNonLegacy), 1.0);

			particlesSectionLegacyCfg.set(getAsRelative(particleTypeLegacy), "DRAGON_BREATH");
			particlesSectionNonLegacyCfg.set(getAsRelative(particleTypeNonLegacy), "DRAGON_BREATH");

			particlesSectionLegacyCfg.set(getAsRelative(particleCountLegacy), 10);
			particlesSectionNonLegacyCfg.set(getAsRelative(particleCountNonLegacy), 10);
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
