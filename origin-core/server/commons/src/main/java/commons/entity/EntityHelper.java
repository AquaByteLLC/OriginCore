package commons.entity;

import com.google.common.collect.ConcurrentHashMultiset;
import commons.CommonsPlugin;
import commons.entity.interfaces.Tickable;
import commons.util.BukkitUtil;
import me.lucko.helper.Schedulers;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;

public class EntityHelper {
	private static final ConcurrentHashMultiset<EntityWrapper<?>> entityWrappers = CommonsPlugin.commons().getEntityRegistry().getEntities();

	public static void addEntity(EntityWrapper<?> entity) {
		entityWrappers.add(entity);
	}

	public static void removeEntity(EntityWrapper<?> entityWrapper) {
		entityWrappers.remove(entityWrapper);
	}

	public static abstract class EntityWrapper<T extends Entity> implements Tickable {
		private final T entity;
		private final String name;

		private boolean cancelled;
		private long ticksRan;

		public EntityWrapper(T entity, String name, boolean tickable, long delay, long period) {
			this.entity = entity;
			this.initEntity();
			this.name = name;

			this.cancelled = false;

			if (!tickable) {
				return;
			}

			Schedulers.async().runRepeating((task) -> {
				if (this.cancelled) {
					task.stop();
					this.removeEntity();
					return;
				}

				this.tick();
				this.ticksRan++;
			}, delay, period);
		}

		public String getName() {
			return this.name;
		}

		public T getEntity() {
			return this.entity;
		}

		public long getTicksRan() {
			return this.ticksRan;
		}

		public void cancel() {
			this.cancelled = true;
		}

		public void removeEntity() {
			EntityHelper.removeEntity(this);
		}

		public void spawnEntity(Player... players) {
			final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(this.entity);
			for (final Player player : players) {
				BukkitUtil.sendPacket(player, packetPlayOutSpawnEntity);
			}
		}

		protected abstract void initEntity();
	}
}
