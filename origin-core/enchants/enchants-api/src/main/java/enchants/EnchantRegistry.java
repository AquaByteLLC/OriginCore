package enchants;

import enchants.item.Enchant;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author vadim
 */
public interface EnchantRegistry {

	@NotNull List<Enchant> getAllEnchants();

	void register(Enchant enchant);

	void unregister(Enchant enchant);

	@NotNull Enchant getByKey(EnchantKey key);

	@Nullable EnchantKey adaptKey(NamespacedKey key);

	@Nullable EnchantKey keyFromName(String name);

}
