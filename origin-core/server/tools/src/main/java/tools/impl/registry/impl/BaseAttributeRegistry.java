package tools.impl.registry.impl;

import commons.events.api.EventRegistry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.BaseAttribute;
import tools.impl.registry.AttributeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAttributeRegistry<T extends BaseAttribute> implements AttributeRegistry<T> {

	private final Map<AttributeKey, T> attributes = new HashMap<>();
	private final Map<NamespacedKey, AttributeKey> byKey = new HashMap<>();
	private final Map<String, AttributeKey> byName = new HashMap<>();

	private final EventRegistry events;

	public BaseAttributeRegistry(EventRegistry events) {
		this.events = events;
	}

	@Override
	public @NotNull List<T> getAllAttributes() {
		return new ArrayList<>(attributes.values());
	}

	@Override
	public void register(T attribute) {
		attribute.getHandle().bind(events);
		attributes.put(attribute.getKey(), attribute);
		byKey.put(attribute.getKey().getNamespacedKey(), attribute.getKey());
		byName.put(attribute.getKey().getName().toLowerCase(), attribute.getKey());
	}

	@Override
	public void unregister(T attribute) {
		attribute.getHandle().unbind(events);
		attributes.remove(attribute.getKey());
		byKey.remove(attribute.getKey().getNamespacedKey());
		byName.remove(attribute.getKey().getName().toLowerCase());
	}

	@Override
	public @NotNull T getByKey(AttributeKey key) {
		T attribute = attributes.get(key);
		if (attribute == null)
			throw new IllegalArgumentException("invalid key " + key);
		return attribute;
	}

	@Override
	public @Nullable AttributeKey adaptKey(NamespacedKey key) {
		return byKey.get(key);
	}

	@Override
	public @Nullable AttributeKey keyFromName(String name) {
		return byName.get(name.toLowerCase());
	}
}
