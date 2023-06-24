package blocks.factory.interfaces;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface OriginBlock {

	String getName();

	OriginBlock setName(String name);

	List<OriginEffects> getEffects();

	OriginBlock addEffect(OriginEffects effectFactory);

	OriginBlock removeEffect(OriginEffects effectFactory);

	int getModelData();

	OriginBlock setModelData(int modelData);

	double getRegenTime();

	OriginBlock setRegenTime(double regenTime);

	double getHardnessMultiplier();

	OriginBlock setHardnessMultiplier(double hardnessMultiplier);

	List<ItemStack> getDrops();

	OriginBlock addDrop(ItemStack itemStack);

	OriginBlock removeDrop(ItemStack itemStack);
}
