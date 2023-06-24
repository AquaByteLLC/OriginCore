package blocks.factory;

import blocks.factory.interfaces.OriginBlock;
import blocks.factory.interfaces.OriginEffects;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OriginBlockImpl implements OriginBlock {
	private String name = "";
	private final List<OriginEffects> effectsList = new ArrayList<>();
	private final List<ItemStack> itemsList = new ArrayList<>();
	private int modelData = 0;
	private double regenTime = 0.0;
	private double hardnessMultiplier = 0.0;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public OriginBlock setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public List<OriginEffects> getEffects() {
		return this.effectsList;
	}

	@Override
	public OriginBlock addEffect(OriginEffects effects) {
		this.effectsList.add(effects);
		return this;
	}

	@Override
	public OriginBlock removeEffect(OriginEffects effects) {
		this.effectsList.remove(effects);
		return this;
	}

	@Override
	public int getModelData() {
		return this.modelData;
	}

	@Override
	public OriginBlock setModelData(int modelData) {
		this.modelData = modelData;
		return this;
	}

	@Override
	public double getRegenTime() {
		return this.regenTime;
	}

	@Override
	public OriginBlock setRegenTime(double regenTime) {
		this.regenTime = regenTime;
		return this;
	}

	@Override
	public double getHardnessMultiplier() {
		return this.hardnessMultiplier;
	}

	@Override
	public OriginBlock setHardnessMultiplier(double hardnessMultiplier) {
		this.hardnessMultiplier = hardnessMultiplier;
		return this;
	}

	@Override
	public List<ItemStack> getDrops() {
		return this.itemsList;
	}

	@Override
	public OriginBlock addDrop(ItemStack itemStack) {
		this.itemsList.add(itemStack);
		return this;

	}

	@Override
	public OriginBlock removeDrop(ItemStack itemStack) {
		this.itemsList.remove(itemStack);
		return this;
	}
}

