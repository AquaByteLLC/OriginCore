package blocks.block.progress;

import blocks.BlocksAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public interface SpeedAttribute {

	String KEY_NAME = "CUSTOM_BREAK_SPEED";

	static NamespacedKey getKey() {
		final JavaPlugin plugin = BlocksAPI.get().getInstance(JavaPlugin.class);
		return new NamespacedKey(plugin, KEY_NAME);
	}

	void setSpeed(ItemStack stack, float speed);

	float getSpeed(ItemStack stack);

	PersistentDataContainer readContainer(ItemStack stack);

	void writeContainer(ItemStack stack, Consumer<PersistentDataContainer> pdc);

}
