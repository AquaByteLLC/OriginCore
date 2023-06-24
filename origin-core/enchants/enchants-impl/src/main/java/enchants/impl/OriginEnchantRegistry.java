package enchants.impl;

import commons.events.api.EventRegistry;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.item.Enchant;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vadim
 */
public class OriginEnchantRegistry implements EnchantRegistry {

	private final Map<EnchantKey, Enchant> enchants = new HashMap<>();
	private final Map<NamespacedKey, EnchantKey> byKey = new HashMap<>();
	private final Map<String, EnchantKey> byName = new HashMap<>();

	private final EventRegistry events;

	public OriginEnchantRegistry(EventRegistry events) {
		this.events = events;
	}

	@Override
	public @NotNull List<Enchant> getAllEnchants() {
		return new ArrayList<>(enchants.values());
	}

	@Override
	public void register(Enchant enchant) {
		enchant.getHandleEnchant().bind(events);
		enchants.put(enchant.getKey(), enchant);
		byKey.put(enchant.getKey().getNamespacedKey(), enchant.getKey());
		byName.put(enchant.getKey().getName().toLowerCase(), enchant.getKey());
	}

	@Override
	public void unregister(Enchant enchant) {
		enchant.getHandleEnchant().unbind(events);
		enchants.remove(enchant.getKey());
		byKey.remove(enchant.getKey().getNamespacedKey());
		byName.remove(enchant.getKey().getName().toLowerCase());
	}

	@Override
	public @NotNull Enchant getByKey(EnchantKey key) {
		Enchant enchant = enchants.get(key);
		if(enchant == null)
			throw new IllegalArgumentException("invalid enchant key "+key);
		return enchant;
	}

	@Override
	public @Nullable EnchantKey adaptKey(NamespacedKey key) {
		return byKey.get(key);
	}

	@Override
	public @Nullable EnchantKey keyFromName(String name) {
		return byName.get(name.toLowerCase());
	}

}
