package mining.impl;

import blocks.BlocksAPI;
import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.EffectHolder;
import blocks.impl.BlocksPlugin;
import blocks.impl.aspect.effect.type.OriginEffect;
import blocks.impl.aspect.effect.type.OriginParticle;
import blocks.impl.builder.OriginBlock;
import blocks.impl.factory.BlockFactoryImpl;
import blocks.impl.factory.EffectFactoryImpl;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import me.lucko.helper.item.ItemStackBuilder;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import mining.MiningAPI;
import mining.impl.cmd.RegionCommands;
import mining.impl.conf.BlocksConfig;
import mining.impl.conf.GeneralConfig;
import mining.impl.events.MiningEvents;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MiningPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private static Injector injector;
	private BlocksAPI blocksAPI;
	private BlocksPlugin blocksPlugin;
	private MiningAPI miningAPI;
	private LiteConfig lfc;

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	@Override
	public AccountStorage<?> getAccounts() { return null; }

	@Override
	public void onEnable() {
		EventRegistry eventRegistry = Commons.events();
		lfc = new LiteConfig(this);
		lfc.register(GeneralConfig.class, GeneralConfig::new);
		lfc.register(BlocksConfig.class, BlocksConfig::new);
		lfc.reload();

		Commons.commons().registerModule(this);

		this.miningAPI = new MiningAPI(this);
		this.blocksAPI = BlocksAPI.getInstance();
		this.blocksPlugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

		injector = Guice.createInjector(new MiningModule(this, lfc, blocksAPI, miningAPI));

		MiningEvents.init(eventRegistry);
		setup();
	}

	@Override
	public void onDisable() {
		lfc.open(BlocksConfig.class).save();

		Bukkit.getOnlinePlayers().forEach(player ->
				RegenerationRegistry.cancelRegenerations(player,
						blocksPlugin.getAccounts()
								.getAccount(player)
								.getRegenerationRegistry()
								.getRegenerations()));
	}

	public GeneralConfig getGeneralConfig() {
		return lfc.open(GeneralConfig.class);
	}

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The FarmingPlugin hasn't been initialized.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private void setup() {
		PaperCommandManager commands = new PaperCommandManager(this);
		commands.registerCommand(new RegionCommands(blocksAPI.getBlockRegistry(), blocksAPI.getRegionRegistry()));

		YamlConfiguration conf = lfc.open(BlocksConfig.class).getConfiguration();
		for (String blockKey : conf.getConfigurationSection("Blocks").getKeys(false)) {

			String mainPath = "Blocks." + blockKey + ".";
			String blockName = conf.getString(mainPath + "blockName");
			int customModelData = conf.getInt(mainPath + "modelData");
			double hardnessMultiplier = conf.getDouble(mainPath + "hardnessMultiplier");
			double regenTime = conf.getDouble(mainPath + "regenTime");
			boolean hasParticles = conf.getBoolean(mainPath + "hasParticle");
			boolean hasEffect = conf.getBoolean(mainPath + "hasEffect");

			final OriginBlock originBlock = new BlockFactoryImpl().newBlock()
					.setName(blockName)
					.setModelData(customModelData);

			final Dropable drop = originBlock.getFactory().newDropable();
			final Effectable effectable = originBlock.getFactory().newEffectable();
			final Hardenable hardenable = originBlock.getFactory().newHardenable();
			final Regenable regenable = originBlock.getFactory().newRegenable();
			final Projectable projectable = originBlock.getFactory().newProjectable();

			hardenable.setHardnessMultiplier(hardnessMultiplier);
			regenable.setRegenTime(regenTime);

			if (hasParticles) {
				String particleType = conf.getString(mainPath + "effects.particles.type");
				int amount = conf.getInt(mainPath + "effects.particles.amount");

				OriginParticle originParticles = new OriginParticle(amount);
				originParticles.setType(Particle.valueOf(particleType));
				EffectHolder particleEffect = new EffectFactoryImpl().newEffect();
				particleEffect.setEffectType(originParticles);
				effectable.addEffect(particleEffect);
			}

			if (hasEffect) {
				String effectType = conf.getString(mainPath + "effects.effect.type");
				int data = conf.getInt(mainPath + "effects.effect.data");

				OriginEffect originEffect = new OriginEffect(data);
				originEffect.setType(Effect.valueOf(effectType));
				EffectHolder effect = new EffectFactoryImpl().newEffect();
				effect.setEffectType(originEffect);
				effectable.addEffect(effect);
			}

			for (String dropKey : conf.getConfigurationSection("Blocks." + blockKey + ".drops").getKeys(false)) {
				String dropPath = mainPath + "drops." + dropKey + ".";

				String material = conf.getString(dropPath + "material");
				String dropName = conf.getString(dropPath + "itemName");
				List<String> lore = conf.getStringList(dropPath + "lore");
				int data = conf.getInt(dropPath + "data");
				boolean glowing = conf.getBoolean(dropPath + "glowing");

				if (material == null || dropName == null) return;

				ItemStackBuilder builder = ItemStackBuilder.of(Material.valueOf(material)).name(dropName).lore(lore).data(data);

				if (glowing) {
					builder.enchant(Enchantment.LURE);
					builder.flag(ItemFlag.HIDE_ENCHANTS);
				}
				drop.addDrop(builder.build());
			}

			projectable.setProjectedBlockData(
					Material.matchMaterial(conf.getString("regenMaterial")).createBlockData()
			);

			originBlock.createAspect(effectable)
					.createAspect(hardenable)
					.createAspect(regenable)
					.createAspect(projectable)
					.createAspect(drop);

			if (blockName == null) return;
			blocksAPI.getBlockRegistry().createBlock(originBlock);
		}

		MiningAPI.map(new BukkitBossBarFactory(Bukkit.getServer()).newBossBar().color(BossBarColor.PINK).style(BossBarStyle.SOLID));
	}

	protected static class MiningModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final LiteConfig lfc;
		private final BlocksAPI blocksAPI;
		private final MiningAPI miningApi;

		MiningModule(JavaPlugin plugin, LiteConfig lfc, BlocksAPI blocksAPI, MiningAPI api) {
			this.plugin = plugin;
			this.lfc = lfc;
			this.blocksAPI = blocksAPI;
			this.miningApi = api;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(LiteConfig.class).toInstance(lfc);
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
			this.bind(MiningAPI.class).toInstance(miningApi);
		}
	}
}
