package blocks.impl;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.IllusionsAPI;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.data.account.BlockAccountStorage;
import blocks.impl.anim.item.BreakSpeed;
import blocks.impl.illusions.impl.Illusions;
import blocks.impl.registry.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import lombok.Getter;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BlocksPlugin extends JavaPlugin implements ResourceProvider {
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private RegenerationRegistry regenerationRegistry;
	private IllusionsAPI illusions;
	private BlockRegistry blockRegistry;
	private OverlayLocationRegistry overlayLocationRegistry;
	private BlockLocationRegistry blockLocationRegistry;
	private ProgressRegistry progressRegistry;
	private SpeedAttribute speedAttribute;
	private RegionRegistry regionRegistry;
	private BlockAccountStorage accountStorage;

	@Override
	public void onEnable() {
		final CommonsPlugin commonsPlugin = CommonsPlugin.commons();
		final EventRegistry events = commonsPlugin.getEventRegistry();

		this.blockRegistry = new BlockRegistryImpl();
		this.overlayLocationRegistry = new OverlayRegistryImpl();
		this.illusions = new Illusions(this, CommonsPlugin.commons().getEventRegistry());
		this.regenerationRegistry = new RegenerationRegistryImpl();
		this.blockLocationRegistry = new LocationRegistryImpl();
		this.progressRegistry = new ProgressRegistryImpl();
		this.speedAttribute = new BreakSpeed();
		this.regionRegistry = new RegionRegistryImpl();

		injector = Guice.createInjector(new BlockModule(new BlocksAPI(this, blockLocationRegistry, illusions, regenerationRegistry, blockRegistry, overlayLocationRegistry, progressRegistry, speedAttribute, regionRegistry), this));

		accountStorage = new BlockAccountStorage(commonsPlugin.getDatabase());
		commonsPlugin.registerAccountLoader(accountStorage);
	}

	public AccountStorage<BlockAccount> getAccounts() {
		return accountStorage;
	}

	static class BlockModule extends AbstractModule {

		private final BlocksAPI blocksAPI;
		private final BlocksPlugin blocksPlugin;

		BlockModule(BlocksAPI blocks, BlocksPlugin blocksPlugin) {
			this.blocksAPI = blocks;
			this.blocksPlugin = blocksPlugin;
		}

		@Override
		protected void configure() {
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
			this.bind(BlocksPlugin.class).toInstance(blocksPlugin);
		}
	}
}
