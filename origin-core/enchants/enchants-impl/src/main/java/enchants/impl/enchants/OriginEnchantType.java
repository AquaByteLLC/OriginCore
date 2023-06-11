package enchants.impl.enchants;

import commons.entity.bukkit.BukkitEntityEvent;
import enchants.config.EnchantConfig;
import enchants.records.EnchantChance;
import enchants.records.OriginEnchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import originmc.packets.event.PacketEntityEvent;
import originmc.packets.type.PacketPlayInBlockDigImpl;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum OriginEnchantType {
	BLOCK_BREAK(new OriginEnchant(
			"Block_Break",
			"This enchant activates on block break",
			"&fBlockBreak: &c",
			ItemStackBuilder.of(Material.WOODEN_AXE).build(),
			new BukkitEntityEvent<BlockBreakEvent>(event -> {
				System.out.print(event.getBlock().getLocation());
			}))),
	BLOCK_DIG_IN(new OriginEnchant(
			"Block_Dig_In",
			"This enchant activates on the packet 'BlockDigIn'",
			"&fBlockDigIn: &c",
			ItemStackBuilder.of(Material.DIAMOND_AXE).build(),
			new PacketEntityEvent<PacketPlayInBlockDigImpl>(event -> {
				PacketPlayInBlockDig packet = event.getPacket();
				System.out.println(packet.a());
			})
	));

	@Getter private final OriginEnchant enchant;
	@Getter private final EnchantChance enchantChance;
	@Getter private final EnchantConfig enchantConfig;

	OriginEnchantType(OriginEnchant enchant) {
		this.enchant = enchant;
		this.enchantConfig = enchant.createConfig();
		this.enchantChance = enchant.createChance();
	}

	public OriginEnchant getByName(String name) {
		for (OriginEnchantType types : values()) {
			if (types.name().equals(name)) return types.getEnchant();
		}
		return null;
	}
}
