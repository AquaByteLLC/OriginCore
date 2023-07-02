package blocks.impl.registry;

import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.builder.AspectHolder;
import blocks.block.builder.FixedAspectHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LocationRegistryImpl implements BlockLocationRegistry {

	private final HashMap<Location, FixedAspectHolder> blocks = new HashMap<>();

	@Override
	public @Nullable FixedAspectHolder getBlockAt(Location location) {
		return location == null || location.getWorld() == null ? null : blocks.get(location.getBlock().getLocation());
	}

	@Override
	public @NotNull FixedAspectHolder createBlock(AspectHolder editor, Location block) {
		if (editor == null || block == null) throw new IllegalArgumentException("null param in createBlock");
		FixedAspectHolder fah = editor.asLocationBased(block);
		blocks.put(fah.getBlockLocation(), fah);
		return fah;
	}

	@Override
	public void deleteBlock(FixedAspectHolder editor) {
		if (editor == null) return;
		blocks.remove(editor.getBlockLocation());
	}

	@Override
	public void deleteBlock(Location location) {
		if (location == null) return;
		blocks.remove(location.getBlock().getLocation());
	}

}
