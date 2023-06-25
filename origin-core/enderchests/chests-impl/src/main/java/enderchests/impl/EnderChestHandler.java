package enderchests.impl;

import commons.events.api.PlayerEventContext;
import commons.events.api.Subscribe;
import enderchests.ChestRegistry;
import enderchests.block.LinkedChest;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import java.lang.reflect.Field;

/**
 * @author vadim
 */
public class EnderChestHandler implements Listener {

	private final ChestRegistry registry;

	public EnderChestHandler(ChestRegistry registry) {
		this.registry = registry;
	}

	@Subscribe
	void onMove(InventoryMoveItemEvent event) {
		InventoryHolder holder = event.getDestination().getHolder();
		if(!(holder instanceof Container container)) return;

		LinkedChest chest = registry.getChestAt(container.getLocation());
		if(chest == null) return;

	}

}
