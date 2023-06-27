package blocks.impl.anim.item;

import blocks.block.progress.SpeedAttribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class BreakSpeed implements SpeedAttribute {
	@Override
	public void setSpeed(ItemStack stack, float speed) {
		writeContainer(stack, persistentDataContainer -> {
			persistentDataContainer.set(SpeedAttribute.getKey(), PersistentDataType.FLOAT, speed);
		});
	}

	@Override
	public float getSpeed(ItemStack stack) {
		return readContainer(stack).get(SpeedAttribute.getKey(), PersistentDataType.FLOAT);
	}

	@Override
	public PersistentDataContainer readContainer(ItemStack stack) {
		return stack.getItemMeta().getPersistentDataContainer();
	}

	@Override
	public void writeContainer(ItemStack stack, Consumer<PersistentDataContainer> pdc) {
		stack.editMeta(meta -> pdc.accept(meta.getPersistentDataContainer()));
	}
}
