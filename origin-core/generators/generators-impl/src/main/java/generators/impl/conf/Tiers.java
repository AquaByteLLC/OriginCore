package generators.impl.conf;

import commons.util.StringUtil;
import generators.impl.wrapper.GenDrop;
import generators.impl.wrapper.GenInfo;
import generators.impl.wrapper.TierUp;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import me.vadim.util.conf.*;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Material;

import java.util.*;

/**
 * @author vadim
 */
public class Tiers extends YamlFile {

	private final ConfigurationProvider prov;

	public Tiers(ResourceProvider resourceProvider, ConfigurationProvider prov) {
		super("tiers.yml", resourceProvider);
		this.prov = prov;
	}

	private Tier first;
	private final Map<Material, Tier> byMaterial = new HashMap<>();
	private final List<Tier> byIndex = new ArrayList<>();

	public Tier findTier(Material material) {
		return byMaterial.get(material);
	}

	public Tier findTier(int index) {
		return index < byIndex.size() ? byIndex.get(index) : null;
	}

	public List<Tier> allTiers() {
		return new ArrayList<>(byIndex);
	}

	public Tier getFirstTier() {
		return first;
	}

	@Override
	public void load() {
		super.load();

		ConfigurationAccessor conf = getConfigurationAccessor().getObject("tiers");
		ConfigurationAccessor[] keys = conf.getChildren();

		Arrays.sort(keys, (k1, k2) -> {
			int i1;
			try {
				i1 = Integer.parseInt(k1.currentPath().split("\\.")[1]);
			} catch (NumberFormatException x) {
				logError(resourceProvider.getLogger(), k1.currentPath());
				return 0;
			}
			int i2;
			try {
				i2 = Integer.parseInt(k2.currentPath().split("\\.")[1]);
			} catch (NumberFormatException x) {
				logError(resourceProvider.getLogger(), k2.currentPath());
				return 0;
			}
			return Integer.compare(i2, i1);
		}); // sort keys desc

		byMaterial.clear();
		byIndex.clear();

		int i = keys.length;
		Tier last = null;
		for (ConfigurationAccessor child : keys) {
			ConfigurationAccessor price = child.getObject("price");
			ConfigurationAccessor material = child.getObject("material");

			Material block = Material.matchMaterial(material.getString("gen"));
			Material drop = Material.matchMaterial(material.getString("drop"));
			double sell = price.getDouble("sell");
			double buy = price.getDouble("buy");
			Upgrade upgrade = null;

			if (last != null && !price.has("upgrade")) // non-leaf tier missing upgrade block
				logError(resourceProvider.getLogger(), price.currentPath() + ".upgrade", "tier upgrade cost");
			else
				upgrade = new TierUp(last, price.getDouble("upgrade"));

			if (block == null || !block.isBlock())
				logError(resourceProvider.getLogger(), material.currentPath() + ".gen", "gen block material");
			assert block != null;

			if (drop == null || !drop.isItem())
				logError(resourceProvider.getLogger(), material.currentPath() + ".drop", "gen drop material");
			assert drop != null;

			Placeholder pl = StringPlaceholder.builder()
					.set("drop_name", StringUtil.convertToUserFriendlyCase(drop.name()))
					.set("drop_price", StringUtil.formatNumber(sell))
					.set("buy_price", StringUtil.formatNumber(buy))
					.set("gen_tier", StringUtil.formatNumber(i + 1))
					.build();

			last = new GenInfo(--i, buy, block, upgrade, new GenDrop(sell, prov.open(Config.class).getGeneratorDrop().format(drop, pl).build()), prov);

			if (byMaterial.containsKey(block))
				logError(resourceProvider.getLogger(), material.currentPath() + ".gen", "gen block material [DUPLICATE]");

			byMaterial.put(block, last);
			byIndex.add(last);
		}

		Collections.reverse(byIndex);
		first = last;
	}

}
