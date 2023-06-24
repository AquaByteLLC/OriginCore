package blocks.handler;


import blocks.BlocksAPI;
import blocks.factory.OriginBlockFactory;
import blocks.factory.effects.BukkitEffectFactory;
import blocks.factory.effects.types.OriginEffect;
import blocks.factory.effects.types.OriginParticle;
import blocks.factory.effects.types.OriginSound;
import blocks.factory.interfaces.OriginBlock;
import blocks.factory.interfaces.OriginEffects;
import blocks.registry.BlockRegistry;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public interface BlockHandler {

	BlockRegistry registry = BlocksAPI.get().getInstance(BlockRegistry.class);

	default void init(FileConfiguration configSection) {
		for (String blockKey : configSection.getConfigurationSection("Blocks").getKeys(false)) {
			String mainPath = "Blocks." + blockKey + ".";
			String blockName = configSection.getString(mainPath + "blockName");
			int customModelData = configSection.getInt(mainPath + "modelData");
			double hardnessMultiplier = configSection.getDouble(mainPath + "hardnessMultiplier");
			double regenTime = configSection.getDouble(mainPath + "regenTime");
			boolean hasParticles = configSection.getBoolean(mainPath + "hasParticle");
			boolean hasEffect = configSection.getBoolean(mainPath + "hasEffect");
			boolean hasSound = configSection.getBoolean(mainPath + "hasSound");

			OriginBlock originBlock = new OriginBlockFactory()
					.newBlock()
					.setName(blockName)
					.setModelData(customModelData)
					.setHardnessMultiplier(hardnessMultiplier)
					.setRegenTime(regenTime);

			if (hasParticles) {
				String particleType = configSection.getString(mainPath + "effects.particles.type");
				int amount = configSection.getInt(mainPath + "effects.particles.amount");

				OriginParticle worldBlocksParticles = new OriginParticle(amount);
				worldBlocksParticles.setType(Particle.valueOf(particleType));

				OriginEffects particleEffect = new BukkitEffectFactory().newEffect()
						.setEffectType(worldBlocksParticles);

				originBlock.addEffect(particleEffect);
			}

			if (hasEffect) {
				String effectType = configSection.getString(mainPath + "effects.effect.type");
				int data = configSection.getInt(mainPath + "effects.effect.data");
				int radius = configSection.getInt(mainPath + "effects.effect.radius");

				OriginEffect worldBlocksEffect = new OriginEffect(data, radius);

				worldBlocksEffect.setType(Effect.valueOf(effectType));

				OriginEffects effect = new BukkitEffectFactory().newEffect()
						.setEffectType(worldBlocksEffect);

				originBlock.addEffect(effect);
			}

			if (hasSound) {
				String soundType = configSection.getString(mainPath + "effects.sounds.type");
				int pitch = configSection.getInt(mainPath + "effects.sounds.pitch");
				int volume = configSection.getInt(mainPath + "effects.sounds.volume");

				OriginSound worldBlocksSound = new OriginSound(volume, pitch);
				// TODO fix the sound.valueOf(soundType) issue
				worldBlocksSound.setType(Sound.BLOCK_AMETHYST_BLOCK_BREAK);

				OriginEffects soundEffect = new BukkitEffectFactory().newEffect()
						.setEffectType(worldBlocksSound);

				originBlock.addEffect(soundEffect);
			}

			for (String dropKey : configSection.getConfigurationSection("Blocks." + blockKey + ".drops").getKeys(false)) {
				String dropPath = mainPath + "drops." + dropKey + ".";

				String material = configSection.getString(dropPath + "material");
				String dropName = configSection.getString(dropPath + "itemName");
				List<String> lore = configSection.getStringList(dropPath + "lore");
				int data = configSection.getInt(dropPath + "data");
				boolean glowing = configSection.getBoolean(dropPath + "glowing");

				if (material == null || dropName == null) return;

				ItemStackBuilder builder = ItemStackBuilder.of(Material.valueOf(material)).name(dropName).lore(lore).data(data);

				if (glowing) {
					builder.enchant(Enchantment.LURE);
					builder.flag(ItemFlag.HIDE_ENCHANTS);
				}

				System.out.println("Drops");
				originBlock.addDrop(builder.build());
			}

			if (blockName == null) return;
			registry.getBlocks().put(blockName, originBlock);
		}
	}
}