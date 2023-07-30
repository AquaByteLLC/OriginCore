package farming.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.FakeBlock;
import blocks.impl.BlocksPlugin;
import blocks.impl.anim.entity.BlockEntity;
import blocks.impl.aspect.effect.type.OriginParticle;
import blocks.impl.aspect.effect.type.OriginSound;
import blocks.impl.builder.OriginBlock;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import blocks.impl.events.RegenEvent;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import farming.impl.settings.FarmingSettings;
import net.minecraft.world.level.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FarmingBreakEvent implements Listener {

	private final EventRegistry eventRegistry;
	private final BlocksPlugin plugin;

	public FarmingBreakEvent(BlocksPlugin plugin, EventRegistry eventRegistry) {
		this.eventRegistry = eventRegistry;
		this.plugin = plugin;
		this.eventRegistry.subscribeAll(this);
	}

	@Subscribe
	void farmingBreakEvent(BreakEvent event) {
		if (!event.getCalling().equals("farming")) return;
		Player player = event.getPlayer();
		Block block = event.getBlock();

		final BlockAccount account = plugin.getAccounts().getAccount(player);
		if (account == null) return;

		final World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
		final OriginBlock originBlock = (OriginBlock) BlocksAPI.getBlock(block.getLocation());

		if (originBlock == null) return;

		final Effectable effectable = (Effectable) originBlock.getAspects().get(AspectType.EFFECTABLE);
		final Dropable dropable = (Dropable) originBlock.getAspects().get(AspectType.DROPABLE);
		final Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

		final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
		if (regenerationRegistry.getRegenerations().containsKey(CraftLocation.toBlockPosition(block.getLocation())))
			return;

		final FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
		final Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);

		if (regenable == null) return;
		if (regenable.getRegenTime() <= 0) return;

		regenable.setFakeBlock(fake);

		final long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);

		regenerationRegistry.createRegen(regenable, block);
		new RegenEvent("farming", regenable, player, block, endTime).callEvent();

		final List<ItemStack> clonedDrops = new ArrayList<>(block.getDrops());
		if (clonedDrops.isEmpty()) {
			return;
		}

		block.getDrops().clear();

		if (!event.isCalledFromEnchant()) {
			if (effectable != null) {
				effectable.getEffects().forEach(effect -> {
					if (effect.getEffectType() instanceof OriginParticle) {
						if (FarmingSettings.CUSTOM_BLOCK_PARTICLES.isEnabled(player)) {
							System.out.println("ENABLED");
							effect.getEffectType().handleEffect(player, block.getLocation());
						}
					}
					if (effect.getEffectType() instanceof OriginSound) {
						if (FarmingSettings.CUSTOM_BLOCK_SOUNDS.isEnabled(player)) {
							System.out.println("ENABLED");
							effect.getEffectType().handleEffect(player, block.getLocation());
						}
					}
				});
			}
		}

		if (dropable != null) {
			dropable.getDrops().forEach(drop -> {
				// TODO: ITEM BACKPACKS?????
				new BlockEntity(player, nmsWorld, block, drop, true);
			});
		}
	}
}