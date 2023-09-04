package enderchests.impl.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import commons.data.sql.DatabaseSession;
import commons.data.sql.SessionProvider;
import commons.util.num.PackUtil;
import enderchests.ChestNetwork;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.EnderChestRegistry;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author vadim
 */
public class LinkedStorage {

	private final SessionProvider provider;
	private final EnderChestRegistry registry;

	public LinkedStorage(SessionProvider provider, EnderChestRegistry registry) {
		this.provider = provider;
		this.registry = registry;
	}

	@SneakyThrows
	public void load() {
		try (DatabaseSession session = provider.session()) {
			Dao<NetworkData, Void> dao      = session.getDAO(NetworkData.class, Void.class);
			Iterator<NetworkData>  iterator = dao.queryForAll().iterator();

			NetworkData data;
			while (iterator.hasNext()) {
				data = iterator.next();

				registry.defineNetwork(data.color, data.owner, data.getInventory());
			}
		}
		try (DatabaseSession session = provider.session()) {
			Dao<ChestData, Void> dao      = session.getDAO(ChestData.class, Void.class);
			Iterator<ChestData>  iterator = dao.queryForAll().iterator();

			ChestData data;
			while (iterator.hasNext()) {
				data = iterator.next();

				ChestNetwork net = registry.getNetwork(data.network, Bukkit.getOfflinePlayer(data.owner));
				LinkedChest  ch  = registry.createChest(net, data.getLocation(), data.face);
			}
		}
	}

	@SneakyThrows
	public void save() {
		try (DatabaseSession session = provider.session()) {
			Dao<NetworkData, Void> dao = session.getDAO(NetworkData.class, Void.class);
			TableUtils.clearTable(session.getConnectionSource(), dao.getDataClass());

			NetworkData data = new NetworkData();
			for (Map<NetworkColor, ChestNetwork> map : registry.getNetworks().values()) {
				for (ChestNetwork net : map.values()) {
					data.owner     = net.getOwnerUUID();
					data.color     = net.getColor();
					data.inventory = PackUtil.inventoryToBase64(net.getInventory());

					try {
						dao.create(data);
					} catch (SQLException e) {
						System.err.println("Problem saving " + data.color + " chest network belonging to " + data.owner);
						System.err.println("Dumping inventory contents to console: ");
						System.err.println("--------------------------------------------------");
						System.err.println(new String(data.inventory, StandardCharsets.UTF_8));
						System.err.println("--------------------------------------------------");
						e.printStackTrace();
					}
				}
			}
		}
		try (DatabaseSession session = provider.session()) {
			Dao<ChestData, Void> dao = session.getDAO(ChestData.class, Void.class);
			TableUtils.clearTable(session.getConnectionSource(), dao.getDataClass());

			ChestData data = new ChestData();
			for (Map<NetworkColor, ChestNetwork> map : registry.getNetworks().values()) {
				for (ChestNetwork net : map.values()) {
					for (LinkedChest ch : net.getChests()) {
						data.owner   = ch.getOwnerUUID();
						data.face    = ch.getProjectedBlockData().getFacing();
						data.network = net.getColor();
						data.setLocation(ch.getBlockLocation());

						try {
							dao.create(data);
						} catch (SQLException e) {
							System.err.println("Problem saving " + data.network + " chest belonging to " + data.owner + " at " + ch.getBlockLocation());
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
