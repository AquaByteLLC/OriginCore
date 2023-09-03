package tools.impl.tool.type;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.IBaseTool;

import java.util.stream.Collectors;

public interface ISkinnedTool extends IBaseTool {

	NamespacedKey reqKey = new NamespacedKey("skins", "_skinnable");
	NamespacedKey hasSkin = new NamespacedKey("skins", "has_skin");
	NamespacedKey applierKey = new NamespacedKey("skins", "applicable");

	String reqValue = "isSkinnable";

	void addSkin(AttributeKey enchantKey);

	void removeSkin();

	void makeSkinnable();

	boolean isSkinnable();

	AttributeKey getSkin();

	boolean hasSkin(AttributeKey enchantKey);

	static ItemStack makeApplier(String type) {
		final AttributeRegistry<Skin> registry = ToolsPlugin.getPlugin().getSkinRegistry();
		final AttributeKey            key      = registry.keyFromName(type);
		final Skin skin = registry.getByKey(key);

		final Placeholder pl = StringPlaceholder.builder()
				.set("name", key.getName())
				.set("information", StringUtil.colorize(String.join("\n", skin.getInformation())))
				.set("appliedLore", StringUtil.colorize(skin.getAppliedLore()))
				.set("targets", StringUtil.colorize(skin.getAttributeTargets().stream().map(Enum::name).collect(Collectors.joining("\n")))).build();

		final ItemStack item = skin.getSkinStack();
		BukkitUtil.writeContainer(item, pdc -> pdc.set(applierKey, PersistentDataType.STRING, type));
		BukkitUtil.formatItem(pl, item);

		return item;
	}
}
