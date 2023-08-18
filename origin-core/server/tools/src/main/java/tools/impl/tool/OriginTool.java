package tools.impl.tool;

import kotlin.Pair;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.skins.Skin;

import java.util.List;

public interface OriginTool {
	List<Pair<Enchant, Integer>> getStartingEnchants();

	Skin getStartingSkin();

	List<Pair<Augment, Long>> getStartingAugments();

	int getMaxAugmentSlots();

	int getStartingAugmentSlots();

	boolean canSkinsBeApplied();

	boolean canAugmentsBeApplied();

	boolean canEnchantsBeApplied();
}
