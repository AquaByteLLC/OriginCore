package blocks.registry;

import blocks.factory.interfaces.OriginBlock;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.concurrent.ConcurrentHashMap;

public class BlockRegistry {
	@Getter
	private final ConcurrentHashMap<String, OriginBlock> blocks;
	@Getter
	private final ConcurrentHashMap<Location, Block> regeneratingBlocks;

	public BlockRegistry() {
		this.blocks = new ConcurrentHashMap<>();
		this.regeneratingBlocks = new ConcurrentHashMap<>();
	}
}
