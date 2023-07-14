package farming.impl;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.builder.OriginBlock;
import co.aikar.commands.PaperCommandManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import enchants.impl.EnchantPlugin;
import farming.impl.commands.RegionCommands;
import farming.impl.conf.BlocksConfig;
import farming.impl.conf.EffectsConfig;
import farming.impl.conf.GeneralConfig;
import farming.impl.conf.MessagesConfig;
import farming.impl.enchants.EnchantTypes;
import farming.impl.events.CropBreakEvent;
import farming.impl.events.FarmingBreakEvent;
import farming.impl.events.RegenEvent;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FarmingPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	public static final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[EnchantPool]").build());

	private static Injector injector;
	private BlocksAPI blocksAPI;
	private BlocksPlugin blocksPlugin;
	public static LiteConfig lfc;

	public GeneralConfig getGeneralConfig() {
		return lfc.open(GeneralConfig.class);
	}

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
		lfc.register(EffectsConfig.class, EffectsConfig::new);
		lfc.register(MessagesConfig.class, MessagesConfig::new);

		lfc.reload();

		Commons.commons().registerModule(this);

		this.blocksAPI = BlocksAPI.getInstance();
		this.blocksPlugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

		injector = Guice.createInjector(new FarmingModule(this, lfc, blocksAPI));

		EnchantPlugin enchantPlugin = EnchantPlugin.get().getInstance(EnchantPlugin.class);

		EnchantTypes.init(enchantPlugin.getRegistry(), enchantPlugin.getFactory());
		//FarmingEvents.init(eventRegistry);

	//	FarmingSettings.init(this);
		setupEvents(eventRegistry);
		setupCommands();
		setupBlocksYml();
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

	private void setupCommands() {
		PaperCommandManager commands = new PaperCommandManager(this);
		commands.registerCommand(new RegionCommands(lfc, blocksAPI.getBlockRegistry(), blocksAPI.getRegionRegistry()));
	}

	private void setupEvents(EventRegistry registry) {
		new CropBreakEvent(blocksPlugin, registry);
		new RegenEvent(blocksPlugin, registry);
		new FarmingBreakEvent(blocksPlugin, registry);
	}

	private void setupBlocksYml() {
		final BlocksConfig blocksConfig = lfc.open(BlocksConfig.class);

		blocksConfig.getConfiguration().getConfigurationSection("Blocks").getKeys(false).forEach(blockKey -> {
			final String mainBlocksPath = "Blocks." + blockKey + ".";
			final OriginBlock originBlock = blocksConfig.createOriginBlock(mainBlocksPath, blockKey);
			blocksAPI.getBlockRegistry().createBlock(originBlock);
		});
	}


	/*

	private void setup() {

		YamlConfiguration conf = lfc.open(BlocksConfig.class).getConfiguration();
		for (String blockKey : conf.getConfigurationSection("Blocks").getKeys(false)) {
			final Dropable drop = originBlock.getFactory().newDropable();
			final Effectable effectable = originBlock.getFactory().newEffectable();
			final Hardenable hardenable = originBlock.getFactory().newHardenable();
			final Regenable regenable = originBlock.getFactory().newRegenable();
			final Projectable projectable = originBlock.getFactory().newProjectable();
			String mainPath = "Blocks." + blockKey + ".";
			String blockName = conf.getString(mainPath + "blockName");
			double regenTime = conf.getDouble(mainPath + "regenTime");
			boolean hasParticles = conf.getBoolean(mainPath + "hasParticle");
			boolean hasSound = conf.getBoolean(mainPath + "hasSound");

			final OriginBlock originBlock = new BlockFactoryImpl().newBlock()
					.setName(blockName);


			final Dropable drop = originBlock.getFactory().newDropable();
			final Effectable effectable = originBlock.getFactory().newEffectable();
			final Regenable regenable = originBlock.getFactory().newRegenable();
			final Projectable projectable = originBlock.getFactory().newProjectable();

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

			if (hasSound) {
				String soundType = conf.getString(mainPath + "effects.sounds.type");

				OriginSound originSound = new OriginSound(soundType);
				EffectHolder soundEffect = new EffectFactoryImpl().newEffect();
				soundEffect.setEffectType(originSound);
				effectable.addEffect(soundEffect);
			}

			for (String dropKey : conf.getConfigurationSection("Blocks." + blockKey + ".drops").getKeys(false)) {
				String dropPath = mainPath + "drops." + dropKey + ".";

				String material = conf.getString(dropPath + "material");
				double sellPrice = conf.getDouble(dropPath + "sellPrice");
				String dropName = conf.getString(dropPath + "itemName");
				List<String> lore = conf.getStringList(dropPath + "lore");
				int data = conf.getInt(dropPath + "data");
				boolean glowing = conf.getBoolean(dropPath + "glowing");

				if (material == null || dropName == null) return;

				List<String> loreList = new ArrayList<>();

				Placeholder pl = StringPlaceholder.builder()
						.set("drop_name", StringUtil.convertToUserFriendlyCase(dropName))
						.set("drop_price", StringUtil.formatNumber(sellPrice))
						.build();

				ItemStackBuilder builder = ItemStackBuilder.of(Material.valueOf(material)).name(pl.format(name.format(placeholder))).lore(lore.stream().map(msg -> pl.format(String.format(pl.toString()))).toList()).data(data);

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
	}

	 */

	protected static class FarmingModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final LiteConfig lfc;
		private final BlocksAPI blocksAPI;

		FarmingModule(JavaPlugin plugin, LiteConfig lfc, BlocksAPI blocksAPI) {
			this.plugin = plugin;
			this.lfc = lfc;
			this.blocksAPI = blocksAPI;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(LiteConfig.class).toInstance(lfc);
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
		}
	}
}
