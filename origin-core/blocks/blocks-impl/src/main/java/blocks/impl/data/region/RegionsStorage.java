package blocks.impl.data.region;

import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import commons.data.sql.DatabaseSession;
import commons.data.sql.SessionProvider;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author vadim
 */
public class RegionsStorage {

	private final SessionProvider provider;
	private final RegionRegistry registry;

	public RegionsStorage(SessionProvider provider, RegionRegistry registry) {
		this.provider = provider;
		this.registry = registry;
	}

	@SneakyThrows
	public void load() {
		try (DatabaseSession session = provider.session()) {
			Dao<CachedRegion, Void> dao = session.getDAO(CachedRegion.class, Void.class);

			Iterator<CachedRegion> iterator = dao.queryForAll().iterator();

			CachedRegion cached;
			while (iterator.hasNext()) {
				cached = iterator.next();
				new OriginRegion(cached.wgRegion, cached.block, Bukkit.getWorld(cached.world)).newInstance(registry);
			}
		}
	}

	@SneakyThrows
	public void save() {
		try (DatabaseSession session = provider.session()) {
			Dao<CachedRegion, Void> dao = session.getDAO(CachedRegion.class, Void.class);
			TableUtils.clearTable(session.getConnectionSource(), dao.getDataClass()); // this makes me nervous

			Iterator<OriginRegion.RegionInstance> iterator = registry.getRegions().values().iterator();

			CachedRegion cached = new CachedRegion();
			while (iterator.hasNext()) {
				OriginRegion.RegionInstance ri = iterator.next();
				try {
					cached.wgRegion = ri.getWgRegion().getId();
					cached.block    = ri.getBlock();
					cached.world    = ri.getRegion().world().getUID();

					dao.create(cached);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}