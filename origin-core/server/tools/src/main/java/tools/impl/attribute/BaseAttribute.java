package tools.impl.attribute;

import commons.events.impl.EventSubscriber;
import org.bukkit.Material;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.target.ToolTarget;

import java.util.List;

public interface BaseAttribute {
	AttributeKey getKey();
	EventSubscriber getHandle();
	AttributeConfiguration getConfig();

	List<ToolTarget> getAttributeTargets();

	default boolean targetsItem(Material type) {
		for (ToolTarget target : getAttributeTargets())
			if (target.appliesToType(type)) return true;
		return false;
	}

}
