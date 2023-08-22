package tools.impl.tool.impl;

import commons.conf.BukkitConfig;
import kotlin.Pair;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.inventory.ItemStack;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.skins.Skin;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.IOriginTool;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class OriginTool extends BukkitConfig implements IOriginTool {

	private List<Pair<Enchant, Integer>> enchants;
	private List<Pair<Augment, Long>> augments;
	private Skin skin;
	private int maxAugmentSlots;
	private int startingAugmentSlots;
	private boolean canSkinsBeApplied;
	private boolean canEnchantsBeApplied;
	private boolean canAugmentsBeApplied;
	private UniqueItemBuilder builder;
	private final Consumer<UniqueItemBuilder> builderConsumer;
	private ItemStack stack;

	public OriginTool(String toolName, ResourceProvider resourceProvider, Consumer<UniqueItemBuilder> builderConsumer) {
		super(toolName, resourceProvider);
		this.builderConsumer = builderConsumer;
	}

	@Override
	public List<Pair<Enchant, Integer>> getStartingEnchants() {
		final String path = "tool.enchants.starting";
		final List<String> list = getConfiguration().getStringList(path);
		final List<Pair<Enchant, Integer>> enchants = new LinkedList<>();
		final BaseAttributeRegistry<Enchant> registry = ToolsPlugin.getPlugin().getEnchantRegistry();

		for (String str : list) {
			final String[] strs = str.split(":");
			final Enchant enchant = registry.getByKey(registry.keyFromName(strs[0]));
			final int level = Integer.parseInt(strs[1]);
			enchants.add(new Pair<>(enchant, level));
		}

		this.enchants = enchants;
		return this.enchants;
	}

	@Override
	public Skin getStartingSkin() {
		final String path = "tool.skins.starting";
		final BaseAttributeRegistry<Skin> registry = ToolsPlugin.getPlugin().getSkinRegistry();
		this.skin = registry.getByKey(registry.keyFromName(getContent()));
		return this.skin;
	}

	@Override
	public List<Pair<Augment, Long>> getStartingAugments() {
		final String path = "tool.augments.starting";
		final List<String> list = getConfiguration().getStringList(path);
		final List<Pair<Augment, Long>> augments = new LinkedList<>();
		final BaseAttributeRegistry<Augment> registry = ToolsPlugin.getPlugin().getAugmentRegistry();

		for (String str : list) {
			final String[] strs = str.split(":");
			final Augment augment = registry.getByKey(registry.keyFromName(strs[0]));
			final long level = Long.parseLong(strs[1]);
			augments.add(new Pair<>(augment, level));
		}

		this.augments = augments;
		return this.augments;
	}

	@Override
	public int getMaxAugmentSlots() {
		final String path = "tool.augments.maxSlots";
		this.maxAugmentSlots = getConfiguration().getInt(path);
		return this.maxAugmentSlots;
	}

	@Override
	public int getStartingAugmentSlots() {
		final String path = "tool.augments.startingSlots";
		this.startingAugmentSlots = getConfiguration().getInt(path);
		return this.startingAugmentSlots;
	}

	@Override
	public boolean canSkinsBeApplied() {
		final String path = "tool.skins.canBeSkinned";
		this.canSkinsBeApplied = getConfiguration().getBoolean(path);
		return this.canSkinsBeApplied;
	}

	@Override
	public boolean canAugmentsBeApplied() {
		final String path = "tool.augments.canBeAugmented";
		this.canAugmentsBeApplied = getConfiguration().getBoolean(path);
		return this.canAugmentsBeApplied;
	}

	@Override
	public boolean canEnchantsBeApplied() {
		final String path = "tool.enchants.canBeEnchanted";
		this.canEnchantsBeApplied = getConfiguration().getBoolean(path);
		return this.canEnchantsBeApplied;
	}

	@Override
	public ItemStack getItem() {
		this.stack = getItem("tool");
		this.builder = new UniqueItemBuilder(stack);

		builder.asSpecialTool(SkinnedTool.class, (tool) -> {
			if (canSkinsBeApplied) {
				tool.makeSkinnable();
				if (this.skin != null) tool.addSkin(this.skin.getKey());
			}
		});

		builder.asSpecialTool(EnchantedTool.class, (tool) -> {
			if (canEnchantsBeApplied) {
				tool.makeEnchantable();
				enchants.forEach(pair -> {
					if (pair.getFirst() == null) throw new RuntimeException("One of the starting enchants is null");
					tool.addEnchant(pair.getFirst().getKey(), pair.getSecond());
				});
			}
		});

		builder.asSpecialTool(AugmentedTool.class, (tool) -> {
			if (canAugmentsBeApplied) {
				if (startingAugmentSlots > maxAugmentSlots)
					throw new RuntimeException("The starting augment slot is greater than max!");
				if (augments.size() > startingAugmentSlots)
					throw new RuntimeException("The number of augments is greater than the starting slots!");

				tool.makeAugmentable(startingAugmentSlots);
				augments.forEach(pair -> {
					if (pair.getFirst() == null) throw new RuntimeException("One of the starting augments is null");
					tool.addAugment(pair.getFirst().getKey(), pair.getSecond());
				});
			}
		});

		builderConsumer.accept(builder);
		this.stack = builder.build();

		return this.stack;
	}
}
