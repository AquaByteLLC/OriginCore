package tools.impl.attribute.skins;

import commons.events.impl.EventSubscriber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tools.impl.ability.builder.impl.AbilityCreator;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeBuilder;
import tools.impl.attribute.AttributeKey;
import tools.impl.target.ToolTarget;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SkinBuilder extends AttributeBuilder {
	SkinBuilder setAppliedLore(String appliedLore);

	SkinBuilder setInfo(List<String> information);

	SkinBuilder setSkinStack(ItemStack skinItem);

	SkinBuilder setModelData(int modelData);

	Skin build(EventSubscriber handleEnchant, BiConsumer<AbilityCreator<Skin, PlayerCachedAttribute<Skin>>, AttributeKey> creator, Consumer<FileConfiguration> writer, ToolTarget... targets);
}
