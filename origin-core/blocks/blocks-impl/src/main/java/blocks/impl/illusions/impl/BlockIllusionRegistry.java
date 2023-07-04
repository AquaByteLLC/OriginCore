package blocks.impl.illusions.impl;

import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.util.ClickCallback;
import blocks.block.util.PacketReceiver;
import blocks.block.util.PlayerInteraction;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.util.PackUtil;
import commons.util.ReflectUtil;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.SneakyThrows;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class BlockIllusionRegistry implements IllusionRegistry {

	private final JavaPlugin plugin;
	private final Map<Location, FakeBlock> byLocation = new HashMap<>();
	private final Map<Integer, FakeBlock> byOverlay = new HashMap<>();
	private final Long2ObjectMap<List<FakeBlock>> byChunk = new Long2ObjectOpenHashMap<>();
	private final PacketReceiver receiver;

	BlockIllusionRegistry(JavaPlugin plugin, PacketReceiver receiver, EventRegistry events) {
		this.plugin   = plugin;
		this.receiver = receiver;
		events.subscribeAll(this);
	}

	@Override
	public void register(FakeBlock block) {
		FakeBlock old = getBlockAt(block.getBlockLocation());
		if (old != null)
			unregister(old);

		Location loc = block.getBlockLocation();
		byLocation.put(block.getBlockLocation(), block);
		byChunk.computeIfAbsent(PackUtil.packChunk(loc), __ -> new ArrayList<>()).add(block);
		block.send(receiver);
		if (block.hasOverlay())
			if (block instanceof PacketBasedFakeBlock packet)
				byOverlay.put(packet.getOverlay().getEntityID(), block);
	}

	@Override
	public void unregister(FakeBlock block) {
		Location loc = block.getBlockLocation();
		byLocation.remove(loc);
		byChunk.computeIfAbsent(PackUtil.packChunk(loc), __ -> new ArrayList<>()).remove(block);
		block.remove(receiver);
		if (block.hasOverlay())
			if (block instanceof PacketBasedFakeBlock packet)
				byOverlay.remove(packet.getOverlay().getEntityID());
	}

	@Override
	public @Nullable FakeBlock getBlockAt(Location location) {
		return byLocation.get(location.getBlock().getLocation());
	}

	public @Nullable FakeBlock getBlockByEntityID(Integer id) {
		return byOverlay.get(id);
	}

	public @NotNull List<FakeBlock> getBlocksByChunk(int chunkX, int chunkZ) {
		return byChunk.computeIfAbsent(PackUtil.packChunk(chunkX, chunkZ), __ -> new ArrayList<>());
	}

	private static final FieldAccess<IBlockData> PacketPlayOutBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutBlockChange.class, "b");

	@Subscribe
	void sendBlockChange(EventContext context, PacketPlayOutBlockChange packet) {
		if (!receiver.appliesTo(context.getPlayer())) return;
//		Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
		Vector    vector   = CraftVector.toBukkit(packet.c().b()); // whatever
		Location  location = vector.toLocation(context.getPlayer().getWorld());
		FakeBlock block    = getBlockAt(location);
		if (block == null) return;
		if (!block.hasProjection()) return;

		PacketPlayOutBlockChange_b.set(packet, ((CraftBlockData) block.getProjectedBlockData()).getState());
	}

	private static final FieldAccess<SectionPosition> PacketPlayOutMultiBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "b");
	private static final FieldAccess<short[]> PacketPlayOutMultiBlockChange_c = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "c");
	private static final FieldAccess<IBlockData[]> PacketPlayOutMultiBlockChange_d = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "d");

	@Subscribe
	@SuppressWarnings("PointlessBitwiseExpression")
	void sendMultiBlockChange(EventContext context, PacketPlayOutMultiBlockChange packet) {
		if (!receiver.appliesTo(context.getPlayer())) return;
		SectionPosition pos  = PacketPlayOutMultiBlockChange_b.get(packet);
		short[]         rel  = PacketPlayOutMultiBlockChange_c.get(packet);
		IBlockData[]    data = PacketPlayOutMultiBlockChange_d.get(packet);

		int cx = pos.u() << 4;
		int cz = pos.w() << 4;
		int cy = pos.v() << 4;

		for (int i = 0; i < rel.length; i++) {
			short pack = rel[i];

			byte rx = (byte) ((pack >>> 8) & 0xf);
			byte rz = (byte) ((pack >>> 4) & 0xf);
			byte ry = (byte) ((pack >>> 0) & 0xf);

			int x = rx + cx;
			int z = rz + cz;
			int y = ry + cy;

			FakeBlock block = getBlockAt(new Location(context.getPlayer().getWorld(), x, y, z));
			if (block == null) continue;
			if (!block.hasProjection()) continue;

			data[i] = ((CraftBlockData) block.getProjectedBlockData()).getState();
		}
	}

	private static final FieldAccess<ClientboundLevelChunkPacketData> ClientboundLevelChunkWithLightPacket_c = Reflection.unreflectFieldAccess(ClientboundLevelChunkWithLightPacket.class, "c");
	private static final FieldAccess<byte[]> ClientboundLevelChunkPacketData_c = Reflection.unreflectFieldAccess(ClientboundLevelChunkPacketData.class, "c");

	@Subscribe
	@SneakyThrows
	void sendMapChunk(EventContext context, ClientboundLevelChunkWithLightPacket packet) {
		if (!receiver.appliesTo(context.getPlayer())) return;
		Player player = context.getPlayer();

		int cX = packet.a();
		int cZ = packet.c();

		List<FakeBlock> fakes = getBlocksByChunk(cX, cZ);
		if (fakes.isEmpty()) return;

		World            world  = player.getWorld();
		org.bukkit.Chunk bukkit = world.getChunkAtAsync(cX, cZ).get();

		Chunk original = (Chunk) ((CraftChunk) bukkit).getHandle(ChunkStatus.o);

		WorldServer server = ((CraftWorld) world).getHandle();
		LightEngine engine = server.k().a();

		class accessor implements LevelHeightAccessor {

			@Override
			public int w_() {
				return original.w_();
			}

			@Override
			public int v_() {
				return original.v_();
			}

		}
		;

		/**
		 * ProtoChunk extension with helper methods and methods from Chunk.
		 */
		class AsyncChunk extends ProtoChunk {

			AsyncChunk(ChunkCoordIntPair var0, ChunkConverter var1, LevelHeightAccessor var2, IRegistry<BiomeBase> var3, @Nullable BlendingData var4) {
				super(var0, var1, var2, var3, var4);
			}

			public @NotNull ChunkSection getSection(int blockY) {
				int            sY       = e(blockY & 15); // since Y can be negative, LevelHeightAccessor apparantly "converts" this to a ChunkSection index
				ChunkSection[] sections = this.k; // this.sections
				ChunkSection   section  = sections[sY];
				if (section == null)
					section = sections[sY] = new ChunkSection(sY, biomeRegistry);
				return section;
			}

			public void setBlockState(int x, int y, int z, IBlockData data) {
				setBlockState(getSection(y), x, y, z, data);
			}

//			public void a(TileEntity tileentity) {
//				BlockPosition blockposition = tileentity.p();
//				if (this.a_(blockposition).q()) {
//					tileentity.a(server); // setLevel
//					tileentity.s();
//					TileEntity tileentity1 = (TileEntity)this.i.put(blockposition.i(), tileentity);
//					if (tileentity1 != null && tileentity1 != tileentity) {
//						tileentity1.ar_();
//					}
//				} else {
//					System.out.println("Attempted to place a tile entity (" + tileentity + ") at " + tileentity.p().u() + "," + tileentity.p().v() + "," + tileentity.p().w() + " (" + this.a_(blockposition) + ") where there was no entity tile!");
//					System.out.println("Chunk coordinates: " + this.c.e * 16 + "," + this.c.f * 16);
//					(new Exception()).printStackTrace();
//				}
//
//			}

			private @javax.annotation.Nullable TileEntity a(BlockPosition blockposition, NBTTagCompound nbttagcompound) {
				IBlockData iblockdata = this.a_(blockposition);
				TileEntity tileentity;
				if ("DUMMY".equals(nbttagcompound.l("id"))) {
					if (iblockdata.q()) {
						tileentity = ((ITileEntity) iblockdata.b()).a(blockposition, iblockdata);
					} else {
						tileentity = null;
//						l.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockposition, iblockdata);
					}
				} else {
					tileentity = TileEntity.a(blockposition, iblockdata, nbttagcompound);
				}

				if (tileentity != null) {
					tileentity.a(server); // setLevel
					this.a(tileentity);
				} else {
//					l.warn("Tried to load a block entity for block {} but failed at location {}", iblockdata, blockposition);
				}

				return tileentity;
			}

			private @Nullable TileEntity _j(BlockPosition blockposition) {
				IBlockData iblockdata = this.a_(blockposition);
				return !iblockdata.q() ? null : ((ITileEntity) iblockdata.b()).a(blockposition, iblockdata);
			}

			public @Nullable TileEntity getBlockEntity(BlockPosition blockposition) {
				TileEntity tileentity = tileentity = (TileEntity) this.i.get(blockposition);

				if (tileentity == null) {
					NBTTagCompound nbttagcompound = (NBTTagCompound) this.h.remove(blockposition);
					if (nbttagcompound != null) {
						TileEntity tileentity1 = this.a(blockposition, nbttagcompound);
						if (tileentity1 != null) {
							return tileentity1;
						}
					}
				}

				if (tileentity == null) {
					tileentity = this._j(blockposition);
					if (tileentity != null) {
						this.a(tileentity);
					}
				} else if (tileentity.r()) {
					this.i.remove(blockposition);
					return null;
				}

				return tileentity;
			}

			public void setBlockState(@NotNull ChunkSection section, int x, int y, int z, IBlockData data) {
				// update inside chunk section
				section.a(x, y, z, data, false); // section.setType(x, y, z, data, some_kind_of_update)

				// update heightmap
				g.get(HeightMap.Type.e).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING)).update(j, i, l, blockstate);
				g.get(HeightMap.Type.f).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, blockstate);
				g.get(HeightMap.Type.d).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.OCEAN_FLOOR)).update(j, i, l, blockstate);
				g.get(HeightMap.Type.b).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.WORLD_SURFACE)).update(j, i, l, blockstate);
			}

			public void replaceWithPacketData(PacketDataSerializer packetdataserializer, NBTTagCompound heightmaps, Consumer<ClientboundLevelChunkPacketData.b> consumer) {
				ChunkSection[] achunksection = this.k;
				int            i             = achunksection.length;

				int j;
				for (j = 0; j < i; ++j) {
					ChunkSection chunksection = achunksection[j];
					chunksection.a(packetdataserializer);
				}

				HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
				i = aheightmap_type.length;

				for (j = 0; j < i; ++j) {
					HeightMap.Type heightmap_type = aheightmap_type[j];
					String         s              = heightmap_type.a();
					if (heightmaps.b(s, 12)) {
						this.a((HeightMap.Type) heightmap_type, (long[]) heightmaps.o(s));
					}
				}

				consumer.accept((blockposition, tileentitytypes, nbttagcompound1) -> {
					TileEntity tileentity = this.getBlockEntity(blockposition);
					if (tileentity != null && nbttagcompound1 != null && tileentity.u() == tileentitytypes) {
						tileentity.a(nbttagcompound1);
					}
				});
			}

			public int getSerializedSize() {
				int len = 0;
				for (ChunkSection section : this.k) {
					len += section.k();
				}
				return len;
			}

		}

		// create empty chunk
		// proto chunks appears to be async-friendly
		// and are used during chunk loading, however
		// they lack many of the methods that Chunk
		// has, so I extended ProtoChunk and added
		// the necessary methods back
		AsyncChunk proto = new AsyncChunk(new ChunkCoordIntPair(cX, cZ),
										  original.r(), // ChunkConverter
										  new accessor()/*original.j*/, // LevelHeightAccessor
										  original.biomeRegistry/*server.u_().d(Registries.an)*/, // BiomeRegistry
										  original.t() // BlendingData
		);

		// read packet data
		ClientboundLevelChunkPacketData packet_data = packet.d();
		byte[]                          c           = ClientboundLevelChunkPacketData_c.get(packet_data);
		PacketDataSerializer            serializer  = new PacketDataSerializer(Unpooled.wrappedBuffer(c));

		// reset read head
		serializer.writerIndex(serializer.capacity());
		serializer.readerIndex(0);

		// populate empty chunk from packet data
		// this means we won't overwrite global/local fake blocks!
		proto.replaceWithPacketData(serializer, packet_data.b(), packet_data.a(cX, cZ));
		// proto.replaceWithPacketData(new FriendlyByteBuf(packet_data.getReadBuffer()), packet_data.getHeightmaps(), packet_data.getBlockEntitiesTagsConsumer(x,z));

		// replace fake blocks in the new chunk
		for (FakeBlock fake : fakes) {
			if (fake.hasOverlay())
				fake.getOverlay().send(receiver); // go ahead and send the overlay
			if (!fake.hasProjection()) continue;
			Location      loc = fake.getBlockLocation();
			BlockPosition bp  = CraftLocation.toBlockPosition(loc);

			BlockData  proj = fake.getProjectedBlockData();
			IBlockData ibd  = ((CraftBlockData) proj).getState();
			TileEntity tile = proto.getBlockEntity(bp); // create

			// creating a tile entity where there wasn't a tile entity is just not working
			if (ibd.q() && tile != null) { // ibd.hasBlockEntity()
				proto.a(bp, ibd, true);
				// put tile entity
				tile = proto.getBlockEntity(bp);
				if (tile == null)
					tile = ((ITileEntity) ibd.b()).a(bp, ibd); // create
				if (tile != null)
					proto.a(tile); // place
			} else {
				// so, send a delayed block change if there is not already a tile entity on this block
				Bukkit.getScheduler().runTaskLater(plugin, () -> fake.send(receiver), 15L);
				// if this is sent too early, the light is not updated
				// sometimes the light is not updated at all
			}
		}

		// re-serialize modified chunk

		// reset write head
		byte[] buffer = new byte[proto.getSerializedSize()];
		serializer = new PacketDataSerializer(Unpooled.wrappedBuffer(buffer));
		serializer.readerIndex(0);
		serializer.writerIndex(0);

		// this method operates on Chunk, but only uses the sections, so I will just replicate what it does here
//		ClientboundLevelChunkPacketData.a(serializer, chunk); // ClientboundLevelChunkPacketData.extractChunkData(friendlyByteBuf, chunk);
		for (ChunkSection section : proto.d()) {
			section.c(serializer); // section.write(serializer);
		}

		// set re-serialized buffer
		// the reason we can't just write to it directly is because the size may have changed when populating fake blocks
		ClientboundLevelChunkPacketData_c.set(packet_data, buffer);
	}

	private static final FieldAccess<Integer> PacketPlayInUseEntity_a = Reflection.unreflectFieldAccess(PacketPlayInUseEntity.class, "a");

	@Subscribe
	void click(EventContext context, PacketPlayInUseEntity packet) throws Throwable {
		if (!receiver.appliesTo(context.getPlayer())) return;
		Player player = context.getPlayer();

		class adapter implements PacketPlayInUseEntity.c {

			ClickCallback callback;

			adapter(int entity) {
				FakeBlock fake = getBlockByEntityID(entity);

				if (fake == null) return;
				if (!fake.hasOverlay()) return;

				callback = fake.getOverlay().getCallback();
				context.setCancelled(true);
			}

			/**
			 * INTERACT
			 */
			@Override
			public void a(EnumHand enumHand) {
				if (enumHand != EnumHand.a) return; // MAIN_HAND
				if (callback != null)
					callback.onClick(player, PlayerInteraction.RIGHT_CLICK);
			}

			/**
			 * INTERACT_AT
			 */
			@Override
			public void a(EnumHand enumHand, Vec3D vec3D) {
				// this appears to fire alongside INTERACT
			}

			/**
			 * ATTACK
			 */
			@Override
			public void a() {
				if (callback != null)
					callback.onClick(player, PlayerInteraction.LEFT_CLICK);
			}

		}

		try {
			packet.a(new adapter(PacketPlayInUseEntity_a.get(packet)));
		} catch (Exception e) {
			ReflectUtil.serr("Caught exception in ClickCallback!");
			ReflectUtil.serr(e);
		}
	}

}
