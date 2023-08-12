package tools.impl.registry;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.impl.attribute.AttributeKey;

import java.util.List;

public interface AttributeRegistry<T> {
	@NotNull List<T> getAllAttributes();

	void register(T attribute);

	void unregister(T attribute);

	@NotNull T getByKey(AttributeKey key);

	@Nullable AttributeKey adaptKey(NamespacedKey key);

	@Nullable AttributeKey keyFromName(String name);

}
