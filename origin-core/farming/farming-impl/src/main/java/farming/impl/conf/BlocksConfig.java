
package farming.impl.conf;

import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.builder.EffectHolder;
import blocks.impl.aspect.effect.type.OriginParticle;
import blocks.impl.aspect.effect.type.OriginSound;
import blocks.impl.builder.OriginBlock;
import blocks.impl.factory.BlockFactoryImpl;
import blocks.impl.factory.EffectFactoryImpl;
import commons.conf.BukkitConfig;
import commons.util.StringUtil;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BlocksConfig extends BukkitConfig {

	private static final NamespacedKey DROP_PRICE = new NamespacedKey("farming", "drop.price");

	public BlocksConfig(ResourceProvider resourceProvider) {
		super("blocks.yml", resourceProvider);
		setDefaultTemplate();
	}

	@Override
	public YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

	public PlaceholderMessage getBlockName(String path) {
		return getConfigurationAccessor().getPlaceholder(path + "blockName");
	}

	public double getRegenTime(String path) {
		return getConfiguration().getDouble(path + "regenTime");
	}

	public boolean hasParticles(String path) {
		return getConfiguration().getBoolean(path + "hasParticle");
	}

	public boolean hasSound(String path) {
		return getConfiguration().getBoolean(path + "hasSound");
	}

	public EffectHolder createParticle(String path) {
		final String particleType = getConfiguration().getString(path + "effects.particles.type");
		final int particleCount = getConfiguration().getInt(path + "effects.particles.amount");

		final OriginParticle originParticles = new OriginParticle(particleCount);
		originParticles.setType(Particle.valueOf(particleType));

		final EffectHolder particleEffect = new EffectFactoryImpl().newEffect();
		particleEffect.setEffectType(originParticles);

		return particleEffect;
	}

	public EffectHolder createSound(String path) {
		final String soundType = getConfiguration().getString(path + "effects.sounds.type");

		final OriginSound originSound = new OriginSound(soundType);

		final EffectHolder soundEffect = new EffectFactoryImpl().newEffect();
		soundEffect.setEffectType(originSound);

		return soundEffect;

	}

	public List<ItemStack> getBlockDrops(String mainBlocksPath, String blockKey) { // "Blocks." + blockKey + ".drops"
		final List<ItemStack> stackList = new ArrayList<>();
		for (String dropKey : getConfiguration().getConfigurationSection("Blocks." + blockKey + ".drops").getKeys(false)) {
			final String dropPath = mainBlocksPath + "drops." + dropKey + ".";

			final PlaceholderMessage dropName = getDropName(dropPath);
			final Material material = getDropType(dropPath);
			final List<PlaceholderMessage> lore = getDropLore(dropPath);
			final double sellPrice = getSellPrice(dropPath);
			final int data = getDropData(dropPath);
			final boolean glowing = isGlowing(dropPath);

			final Placeholder pl = StringPlaceholder.builder()
					.set("drop_price", StringUtil.formatNumber(sellPrice))
					.build();

			final UnformattedItem unformattedItem = new UnformattedItem(material, dropName, lore);
			final ItemBuilder builder = unformattedItem.format(pl);

			if (glowing) {
				builder.enchantment(Enchantment.LURE, 1);
				builder.flag(ItemFlag.HIDE_ENCHANTS);
			}

			builder.customModelData(data);

			final ItemStack stack = builder.build();
			setDropPrice(stack, sellPrice);

			stackList.add(stack);
		}
		return stackList;
	}

	public PlaceholderMessage getDropName(String path) {
		return getConfigurationAccessor().getPlaceholder(path + "itemName");
	}

	public Material getDropType(String path) {
		return Material.matchMaterial(getConfiguration().getString(path + "material"));
	}

	public List<PlaceholderMessage> getDropLore(String path) {
		List<PlaceholderMessage> placeholderMessages = new ArrayList<>();
		getConfiguration().getStringList(path + "lore").forEach(loreLine -> {
			placeholderMessages.add(new UnformattedMessage(loreLine));
		});
		return placeholderMessages;
	}

	public boolean isGlowing(String path) {
		return getConfiguration().getBoolean(path + "glowing");
	}

	public int getDropData(String path) {
		return getConfiguration().getInt(path + "data");
	}

	public double getSellPrice(String path) {
		return getConfiguration().getDouble(path + "sellPrice");
	}

	public Material getRegenMaterial() {
		return Material.matchMaterial(getConfiguration().getString("regenMaterial"));
	}
	public OriginBlock createOriginBlock(String mainBlocksPath, String blockKey) {
		final OriginBlock originBlock = new BlockFactoryImpl().newBlock().setName(getBlockName(mainBlocksPath).raw());
		final Dropable drop = originBlock.getFactory().newDropable();
		getBlockDrops(mainBlocksPath, blockKey).forEach(drop::addDrop);

		final Effectable effectable = originBlock.getFactory().newEffectable();
		if (hasSound(mainBlocksPath)) effectable.addEffect(createSound(mainBlocksPath));
		if (hasParticles(mainBlocksPath)) effectable.addEffect(createParticle(mainBlocksPath));

		final Regenable regenable = originBlock.getFactory().newRegenable();
		regenable.setRegenTime(getRegenTime(mainBlocksPath));

		final Projectable projectable = originBlock.getFactory().newProjectable();
		projectable.setProjectedBlockData(getRegenMaterial().createBlockData());

		originBlock.createAspect(effectable)
				.createAspect(regenable)
				.createAspect(projectable)
				.createAspect(drop);

		return originBlock;
	}

	public static void setDropPrice(ItemStack drop, Double newPrice) {
		if (!drop.hasItemMeta()) throw new IllegalArgumentException("drop");
		drop.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if (newPrice != null)
				pdc.set(DROP_PRICE, PersistentDataType.DOUBLE, newPrice);
			else
				pdc.remove(DROP_PRICE);
		});
	}


}
