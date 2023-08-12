package tools.impl.attribute.skins;

import commons.events.impl.EventSubscriber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tools.impl.target.ToolTarget;

import java.util.List;
import java.util.function.Consumer;

public interface SkinBuilder {
	SkinBuilder setAppliedLore(String appliedLore);

	SkinBuilder setInfo(List<String> information);

	SkinBuilder setSkinStack(ItemStack skinItem);

	SkinBuilder setModelData(int modelData);

	Skin build(EventSubscriber handleEnchant, Consumer<FileConfiguration> writer, ToolTarget... targets);
}
