package generators.impl.conf;

import commons.ReflectUtil;
import commons.StringUtil;
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
import org.bukkit.configuration.InvalidConfigurationException;

import javax.swing.plaf.basic.BasicButtonUI;
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

		ConfigurationAccessor   conf = getConfigurationAccessor().getObject("tiers");
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
			if(last != null && !child.has("upgrade")) // non-leaf tier missing upgrade block
				logError(resourceProvider.getLogger(), child.currentPath() + ".upgrade", "tier upgrade cost");

			String name = child.getString("name");
			Material block = Material.matchMaterial(child.getString("block"));
			Material drop = Material.matchMaterial(child.getObject("drop").getString("item"));
			double  price   = child.getObject("drop").getDouble("price");
			Upgrade upgrade = null;

			if(last != null)
				upgrade = new TierUp(last, child.getObject("upgrade").getDouble("price"));

			if(block == null || !block.isBlock())
				logError(resourceProvider.getLogger(), child.currentPath() + ".block", "gen block material");
			assert block != null;

			if(drop == null || !drop.isItem())
				logError(resourceProvider.getLogger(), child.currentPath() + ".block", "gen drop material");
			assert drop != null;

			Placeholder pl = StringPlaceholder.builder()
											  .set("drop_name", StringUtil.convertToUserFriendlyCase(drop.name()))
											  .set("drop_price", String.valueOf(price))
											  .build();

			last = new GenInfo(--i, name, block, upgrade, new GenDrop(price, prov.open(Config.class).getGeneratorDrop().format(drop, pl).build()), prov);
			if(byMaterial.containsKey(block))
				logError(resourceProvider.getLogger(), child.currentPath() + ".block", "DUPLICATE gen block material");
			byMaterial.put(block, last);
			byIndex.add(last);
		}

		Collections.reverse(byIndex);
		first = last;
	}

}
