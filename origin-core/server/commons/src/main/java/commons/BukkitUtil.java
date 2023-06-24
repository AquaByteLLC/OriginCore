package commons;

import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public class BukkitUtil {

	@SuppressWarnings("DataFlowIssue")
	public static void formatItem(Placeholder pl, ItemStack item) {
		item.editMeta(meta -> {
			if (meta.hasDisplayName())
				meta.setDisplayName(Text.colorize(pl.format(meta.getDisplayName())));
			if (meta.hasLore())
				meta.setLore(meta.getLore().stream().map(pl::format).map(Text::colorize).toList());
		});
	}

}
