package levels.conf.action;

import commons.CommonsPlugin;
import commons.conf.SettableConfig;
import commons.versioning.VersionSender;
import commons.versioning.impl.ConfigurableEffectGroup;
import me.vadim.util.conf.bukkit.wrapper.EffectParticle;
import me.vadim.util.conf.bukkit.wrapper.EffectSound;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class LevelEffects {
	private static SettableConfig effectCfg;
	public static VersionSender versionSender;

	public static ConfigurableEffectGroup LEVEL_UP_EFFECT;

	public static void init() {
		effectCfg = new SettableConfig("level_effects.yml", "levels", CommonsPlugin.commons());

		versionSender = new VersionSender(effectCfg.getFile(), effectCfg.getFileConfiguration());

		LEVEL_UP_EFFECT = new ConfigurableEffectGroup(versionSender, "level_up",
				new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
				new EffectSound(Sound.BLOCK_ANVIL_BREAK, 1, 1),
				new EffectParticle(Particle.DRAGON_BREATH, 10),
				new EffectParticle(Particle.DRAGON_BREATH, 10)
		);

	}
}

