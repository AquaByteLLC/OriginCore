package blocks.registry;

import blocks.factory.interfaces.OriginBlock;
import blocks.region.OriginRegion;
import com.google.common.collect.ConcurrentHashMultiset;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class BlockRegistry {

	public interface RandomBlockFactory {
		static RandomBlock create() {
			return new RandomBlock();
		}
	}

	public static class RandomBlock {

		protected class Entry {

			public final Block object;
			private final Double insertionWeight;
			public final Double weight;

			public Entry(Block object, Double insertionWeight, Double weight) {
				this.object = object;
				this.insertionWeight = insertionWeight;
				this.weight = weight;
			}
		}

		private final TreeMap<Double, Entry> entries = new TreeMap<>();
		private final Random random = new Random();
		private double accumulatedWeight = 0;

		public synchronized void add(Block object, double weight) {
			entries.put(accumulatedWeight += weight, new Entry(object, accumulatedWeight, weight));
		}

		public synchronized boolean contain(Block object) {
			for (Entry entry : entries.values()) {
				if(entry.object == object) {
					return true;
				}
			}
			return false;
		}

		public synchronized void remove(Block object) {
			for (Map.Entry<Double, Entry> entry : entries.entrySet()) {
				if(entry.getValue().object == object) {
					accumulatedWeight -= entry.getValue().weight;
					entries.remove(entry.getValue().insertionWeight);
				}
			}
		}

		public synchronized Block get() {
			return entries.ceilingEntry(random.nextDouble() * accumulatedWeight).getValue().object;
		}

		public double getAccumulatedWeight() {
			return accumulatedWeight;
		}

		public int size() {
			return entries.size();
		}

		public boolean isEmpty() {
			return entries.isEmpty();
		}

		public TreeMap<Double, Entry> getEntries() {
			return entries;
		}
	}

	@Getter
	private final ConcurrentHashMap<String, OriginBlock> blocks;
	@Getter
	private final ConcurrentHashMultiset<Location> regeneratingBlocks;
	@Getter
	private final ConcurrentHashMap<String, OriginRegion.RegionInstance> regions;

	public BlockRegistry() {
		this.blocks = new ConcurrentHashMap<>();
		this.regeneratingBlocks = ConcurrentHashMultiset.create();
		regions = new ConcurrentHashMap<>();
	}
}
