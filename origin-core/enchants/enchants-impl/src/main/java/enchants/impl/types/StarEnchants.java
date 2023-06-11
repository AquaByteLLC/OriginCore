package enchants.impl.types;

import enchants.annotations.RegisterEnchant;
import enchants.records.StarEnchant;
import lombok.Getter;
import me.lucko.helper.text3.Text;
import org.bukkit.event.block.BlockBreakEvent;

public class StarEnchants {

    @RegisterEnchant
    @Getter private final StarEnchant speedEnchant;

    public StarEnchants() {
        this.speedEnchant =
                new StarEnchant(
                        Text.colorize("&aStarEnchant"),
                        Text.colorize("&bEnchant which makes you the speedy!"),
                        Text.colorize("&aSpeed -->"),
                        1250.0,
                        50.0,
                        0.2,
                        0.01,
                        handleEvent -> {
                            if (handleEvent instanceof BlockBreakEvent event) {
                                event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 1);
                            }
                        });
    }

}
