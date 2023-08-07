package levels.menu;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuSorter {

	private static final int X = 9;
	private static final int Y = 5;
	private static final int[][] slots = new int[Y][X];
	// [
	//   [0..9],
	//   [9..17],
	//   <etc>
	// ]

	@SuppressWarnings("ConstantValue")
	public static <T> List<List<T>> chunked(List<T> list, int size) {
		if (list == null)
			throw new NullPointerException("list");
		if (size <= 0)
			throw new IllegalArgumentException("size: " + size);

		List<List<T>> chunked = new ArrayList<>(list.size() / size);
		List<T>       current = new ArrayList<>(size);

		int i = 0;
		for (T t : list) {
			current.add(t);
			if (++i >= size) {
				i = 0;
				chunked.add(current);
				current = new ArrayList<>(size);
			}
		}
		if (i < size && !current.isEmpty())
			chunked.add(current);

		return chunked;
	}

	public static final class ProgressionOptions {

		final int min, max;
		final int gap;
		final int s, e;

		public ProgressionOptions(int min, int max, int gap, int s, int e) {
			if (min >= max)
				throw new IllegalArgumentException("invalid range (min,max) " + min + " to " + max);
			if (gap < 0)
				throw new IllegalArgumentException("gap " + gap);
			if (s >= e)
				throw new IllegalArgumentException("invalid range (s,e) " + s + " to " + e);
			if (s < 0)
				throw new IllegalArgumentException("s " + s);

			this.min = min;
			this.max = max;
			this.gap = gap;
			this.s   = s;
			this.e   = e;
		}

	}


	@FunctionalInterface
	private interface ForeachConsumer {

		void process(int x, int y, int i);

	}

	private static void foreachYX(ProgressionOptions opts, ForeachConsumer consumer) {
		if (opts.e >= X)
			throw new IllegalArgumentException("opts.e " + opts.e + " >= " + X);

		int x = opts.s;
		int y = opts.min;
		int g = 0;

		boolean incY = true;
		boolean incX = false;
		boolean nxtY = false;

		int i = 0;
		while (y < Y || x <= opts.e) {
			consumer.process(x, y, i++);

			if (incX)
				x++;
			else {
				if (incY)
					y++;
				else
					y--;
			}
			if (y >= opts.max) {
				incY = false;
				incX = true;
				nxtY = false;
			}
			if (y <= opts.min) {
				incY = false;
				incX = true;
				nxtY = true;
			}
			if (incX && g++ >= opts.gap + 1) {
				incX = false;
				incY = nxtY;

				g = 0;
			}
		}
	}

	public static <T> BiMap<Integer, T> sortYX(List<T> sort, ProgressionOptions opts) {
		BiMap<Integer, T> ofSlots = HashBiMap.create();
		foreachYX(opts, (x, y, i) -> ofSlots.put(slots[y][x], sort.get(i)));
		return ofSlots;
	}

	public static int ctYX(ProgressionOptions opts) {
		AtomicInteger ct = new AtomicInteger(0);
		foreachYX(opts, (x, y, i) -> ct.getAndIncrement());
		return ct.get();
	}

	public static int[] slotsYX(ProgressionOptions opts) {
		List<Integer> slot = new ArrayList<>();
		foreachYX(opts, (x, y, i) -> slot.add(slots[y][x]));
		return slot.stream().mapToInt(Integer::intValue).toArray();
	}

}
