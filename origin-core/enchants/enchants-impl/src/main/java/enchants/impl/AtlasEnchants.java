package enchants.impl;

import enchants.impl.types.StarEnchants;
import me.lucko.helper.Events;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AtlasEnchants extends JavaPlugin {

    @Override
    public void onEnable() {
        registerEvents();
    }

    public void registerEvents() {
        Events.subscribe(BlockBreakEvent.class).handler(event -> {
            StarEnchants enchants = new StarEnchants();
            enchants.getSpeedEnchant().handleEnchant().accept(event);
        });
    }

    @Override
    public void onDisable() {

    }

}
