package enchants.impl;

import enchants.records.OriginEnchant;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import originmc.PacketAPI;
import originmc.packets.type.PacketPlayOutBlockBreakImpl;

public class Test extends JavaPlugin {

	void test() {
		new OriginEnchant("TestingName", "Info", "Lore", ItemStackBuilder.of(Material.WOODEN_AXE).build(),
				new PacketAPI(this).mapEvent(PacketPlayOutBlockBreakImpl.class, $ -> {

		}));
	}
}
