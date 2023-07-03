package blocks.impl.illusions.impl;

import blocks.block.illusions.BlockOverlay;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.util.ClickCallback;
import blocks.block.util.PacketReceiver;
import blocks.block.util.PlayerInteraction;
import blocks.impl.BlocksPlugin;
import commons.CommonsPlugin;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.util.BukkitUtil;
import commons.util.PackUtil;
import commons.util.ReflectUtil;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.SneakyThrows;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.util.*;

class BlockIllusionRegistry implements IllusionRegistry {

	private final JavaPlugin plugin;
	private final Map<Location, FakeBlock> byLocation = new HashMap<>();
	private final Map<Integer, FakeBlock> byOverlay = new HashMap<>();
	private final Long2ObjectMap<List<FakeBlock>> byChunk = new Long2ObjectOpenHashMap<>();
	private final PacketReceiver receiver;

	BlockIllusionRegistry(JavaPlugin plugin, PacketReceiver receiver, EventRegistry events) {
		this.plugin = plugin;
		this.receiver = receiver;
		events.subscribeAll(this);
	}

	@Override
	public void register(FakeBlock block) {
		FakeBlock old = getBlockAt(block.getBlockLocation());
		if(old != null)
			unregister(old);

		Location loc = block.getBlockLocation();
		byLocation.put(block.getBlockLocation(), block);
		byChunk.computeIfAbsent(PackUtil.packChunk(loc), __ -> new ArrayList<>()).add(block);
		block.send(receiver);
		if(block.hasOverlay())
			if(block instanceof PacketBasedFakeBlock packet)
				byOverlay.put(packet.getOverlay().getEntityID(), block);
	}

	@Override
	public void unregister(FakeBlock block) {
		Location loc = block.getBlockLocation();
		byLocation.remove(loc);
		byChunk.computeIfAbsent(PackUtil.packChunk(loc), __ -> new ArrayList<>()).remove(block);
		block.remove(receiver);
		if(block.hasOverlay())
			if(block instanceof PacketBasedFakeBlock packet)
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
		if(!receiver.appliesTo(context.getPlayer())) return;
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
		if(!receiver.appliesTo(context.getPlayer())) return;
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
//	private static final FieldAccess<byte[]> ClientboundLevelChunkPacketData_c = Reflection.unreflectFieldAccess(ClientboundLevelChunkPacketData.class, "c");
	private static final MethodHandle ClientboundLevelChunkPacketData_c = ReflectUtil.unreflectMethodHandle(ClientboundLevelChunkPacketData.class, "c");

	List<Long> recentlySent = new ArrayList<>();
	@Subscribe
	@SneakyThrows
	void sendMapChunk(EventContext context, ClientboundLevelChunkWithLightPacket packet) {
		if (!receiver.appliesTo(context.getPlayer())) return;
		Player player = context.getPlayer();

		//:Prayge:

		int cX = packet.a();
		int cZ = packet.c();

		if(recentlySent.contains(PackUtil.packChunk(cX, cZ))) return;

		List<FakeBlock> fakes = getBlocksByChunk(cX, cZ);
		if(fakes.isEmpty()) return;

		World world = player.getWorld();
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

			/*
			* These are useful methods, so I'm leaving them here.
			* However, they did not work for my purposes.
			*/

			public static @NotNull ChunkSection getSection(Chunk chunk, int blockY) {
				int sY = chunk.e(blockY & 15); // since Y can be negative, LevelHeightAccessor apparantly "converts" this to a ChunkSection index
				ChunkSection[] sections = chunk.d(); // chunk.getSections()
				ChunkSection section = sections[sY];
				if(section == null)
					section = sections[sY] = new ChunkSection(sY, chunk.biomeRegistry);
				return section;
			}

			public static void setBlockState(Chunk chunk, int x, int y, int z, IBlockData data) {
				setBlockState(getSection(chunk, y), chunk, x, y, z, data);
			}

			public static void setBlockState(@NotNull ChunkSection section, Chunk chunk, int x, int y, int z, IBlockData data) {
				// update inside chunk section
				section.a(x, y, z, data, false); // section.setType(x, y, z, data, some_kind_of_update)

				// update heightmap
				chunk.g.get(HeightMap.Type.e).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING)).update(j, i, l, blockstate);
				chunk.g.get(HeightMap.Type.f).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, blockstate);
				chunk.g.get(HeightMap.Type.d).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.OCEAN_FLOOR)).update(j, i, l, blockstate);
				chunk.g.get(HeightMap.Type.b).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.WORLD_SURFACE)).update(j, i, l, blockstate);
			}

		};

		// shallow-clone the original nms chunk
		// inside this constructor chain this.k (chunkSections) is passed as null and re-initialized
		// so the data is not actually being copied
		ProtoChunk proto = new ProtoChunk(new ChunkCoordIntPair(cX, cZ),
										  original.r(), // ChunkConverter
										  new accessor()/*original.j*/, // LevelHeightAccessor
										  original.biomeRegistry/*server.u_().d(Registries.an)*/, // BiomeRegistry
										  original.t() // BlendingData
		);

		proto.a(original.j()); // ChunkStatus
		proto.a(engine); // LightEngine
		proto.a(() -> original.a(() -> (BiomeSettingsGeneration)null)); // Biomes (?) -- deprecated but I don't see any replacement
		original.i.forEach((bp, tile) -> { // original.tileEntities
			proto.a(bp, original.a_(bp), false); // proto.setType(bp, original.getType(bp), false);
			proto.a(tile); // proto.setTileEntity(tile)
		});

		// chunk_c <class Chunk.c> appears to be a callback:
		// <init> chunk_c -> this.s -> public void C()
		Chunk shallowClone = new Chunk(server, proto, null);

		// now we can modify shallowClone

		// copy real blocks
		ChunkSection[] sectionsOld = original.d();
		ChunkSection[] sectionsNew = shallowClone.d();
		for (int i = 0; i < sectionsOld.length; i++) {
			if(sectionsOld[i] == null) {
				sectionsNew[i] = null;
				continue;
			}
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < 16; y++) {
						ChunkSection section = sectionsNew[i];
						if(section == null)
							section = sectionsNew[i] = new ChunkSection(sectionsOld[i].g(), shallowClone.biomeRegistry); // sectionsOld[i].getY()
						// for some reason this works here, but not for my projected block data
						// probably because we've done a shallow copy and this is the data that's
						// supposed to be there in the first place
						section.a(x, y, z, sectionsOld[i].a(x, y, z), false); // section.setType(x, y, z, sectionsOld[i].getType(x, y, z), some_kind_of_update)
//						accessor.setBlockState(section, shallowClone, x, y, z, ibd);
					}
				}
			}
		}
		// replace fake blocks
		for (FakeBlock fake : fakes) {
			if(fake.hasOverlay())
				fake.getOverlay().send(receiver); // go ahead and send the overlay
			if(!fake.hasProjection()) continue;
			Location loc = fake.getBlockLocation();
			// this doesn't work, for some reason
			/*
			int rX = loc.getBlockX() & 15;
			int rY = loc.getBlockY() & 15;
			int rZ = loc.getBlockZ() & 15;
			int sY = shallowClone.e(rY); // since Y can be negative, LevelHeightAccessor apparantly "converts" this to a ChunkSection index
			ChunkSection section = sectionsNew[sY];
			if(section == null)
				section = sectionsNew[sY] = new ChunkSection(sY, shallowClone.biomeRegistry);
			accessor.setBlockState(section, shallowClone, rX, rY, rZ, ((CraftBlockData) fake.getProjectedBlockData()).getState());
			*/

			// so we do this instead, however setBlockState want to be run on main thread
			// so we permorm miniscule amounts of devious synchronization on the packet thread
			// :SillyChamp:
			CommonsPlugin.commons().sync(() -> {
				shallowClone.a(CraftLocation.toBlockPosition(loc), ((CraftBlockData) fake.getProjectedBlockData()).getState(), false); // setBlockState
			}).get();
		}

		// instead of writing my own parser (https://wiki.vg/Chunk_Format)
		// we will just use a cheeky amount of reflection to gain access to the buffer
		// (byte[] c) and re-serialize the ChunkPacketData on our new chunk
		ClientboundLevelChunkPacketData data = packet.d();
		ByteBuf byteBuf_c = (ByteBuf) ClientboundLevelChunkPacketData_c.invoke(data);
		ClientboundLevelChunkPacketData.a(new PacketDataSerializer(byteBuf_c), shallowClone);
	}


	@Subscribe
	void quit(PlayerQuitEvent event) {
		recentlySent.clear();
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
