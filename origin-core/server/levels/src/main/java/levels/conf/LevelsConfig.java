package levels.conf;

import commons.conf.BukkitConfig;
import commons.util.StringUtil;
import levels.Level;
import levels.reward.LevelReward;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.Placeholders;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.ItemBuilder;
import me.vadim.util.item.Text;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LevelsConfig extends BukkitConfig {

	private final String BASE = "levels.";

	public LevelsConfig(ResourceProvider resourceProvider) {
		super("levels.yml", resourceProvider);
		setDefaultTemplate();
	}

	private int getCommandCount(int level) {
		return getConfiguration().getInt(BASE + level + ".rewards.count");
	}

	private List<String> getCommandList(int level) {
		return getConfiguration().getStringList(BASE + level + ".rewards.commands");
	}

	@Override
	public YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

	public List<LevelReward> getRewards(int level) {
		final ArrayList<LevelReward> pickedCommands = new ArrayList<>();
		final List<String>           commandList    = getCommandList(level);

		while (pickedCommands.size() < getCommandCount(level)) {
			final Random random = new Random();
			random.setSeed(System.currentTimeMillis());

			final int    num  = random.nextInt(commandList.size());
			final String pick = commandList.get(num);

			final LevelReward reward = new RewardImpl(pick, String.join("\n", getRewardNames(level)));

			if (!pickedCommands.contains(reward)) pickedCommands.add(reward);
		}
		return pickedCommands;
	}

	public double getRequiredExperience(int level) {
		return getConfiguration().getDouble(BASE + level + ".experience");
	}

	public List<String> getRewardNames(int level) {
		return getConfiguration().getStringList(BASE + level + ".rewards.name");
	}

	public ItemStack getLockedLevel(boolean legacy) {
		return legacy ? getItem("legacy.locked_level") : getItem("nonlegacy.locked_level");
	}

	public ItemStack getUnlockedLevel(boolean legacy) {
		return legacy ? getItem("legacy.unlocked_level") : getItem("nonlegacy.unlocked_level");
	}

	public PlaceholderMessage getTitle(boolean legacy) {
		return legacy ? getConfigurationAccessor().getPlaceholder("legacy.menu.title") : getConfigurationAccessor().getPlaceholder("nonlegacy.menu.title");
	}

	public String getSelectedOptionPrefix(boolean legacy) {
		return legacy ? getConfigurationAccessor().getString("legacy.menu.option_prefix.selected") : getConfigurationAccessor().getString("nonlegacy.menu.option_prefix.selected");
	}

	public String getOtherOptionPrefix(boolean legacy) {
		return legacy ? getConfigurationAccessor().getString("legacy.menu.option_prefix.other") : getConfigurationAccessor().getString("nonlegacy.menu.option_prefix.other");
	}

	public ItemStack getNextLevel(boolean legacy) {
		return legacy ? getItem("legacy.next_level") : getItem("nonlegacy.next_level");
	}

	public ArrayDeque<Level> getLevels() {
		final ArrayDeque<Level> levels = new ArrayDeque<>();

		for (String id : getConfiguration().getConfigurationSection("levels").getKeys(false)) {
			final int num = Integer.parseInt(id);

			final double            requiredExperience = getRequiredExperience(num);
			final int               commandCount       = getCommandCount(num);
			final List<LevelReward> rewards            = getRewards(num);

			levels.add(new LevelImpl(requiredExperience, rewards, commandCount, num));
		}

		for (Level level : levels) {
			System.out.println("Number: " + level.levelNumber());
		}
		return levels;
	}

	private static final Pattern MODEL_DATA = Pattern.compile("([^()]+)(\\(\\d+\\))?");

	private ItemBuilder getMaterialModelData(String path, String name) {
		String mat  = getConfigurationAccessor().getString(path);
		name += " MATERIAL(modeldata)";
		if (mat == null)
			logError(path, name);
		assert mat != null;

		Matcher matcher = MODEL_DATA.matcher(mat);
		matcher.matches();

		Material material = Material.matchMaterial(matcher.group(1));
		if (material == null)
			logError(path, name);
		assert material != null;

		ItemBuilder builder = ItemBuilder.create(material);

		try {
			int model = Integer.parseInt(matcher.group(2).replaceAll("\\D", ""));
			builder.customModelData(model);
		} catch (NumberFormatException x) {
			logError(path, name);
		} catch (IllegalStateException | NullPointerException ignored) {

		}

		return builder;
	}

	public ItemBuilder getNextLevelButton(boolean legacy) {
		return getMaterialModelData(legacy ? "legacy.menu.next_level_button" : "nonlegacy.menu.next_level_button", "next level button");
	}

	public ItemBuilder getLockedLevelButton(boolean legacy) {
		return getMaterialModelData(legacy ? "legacy.menu.locked_level_button" : "nonlegacy.menu.locked_level_button", "next level button");
	}

	public ItemBuilder getUnlockedLevelButton(boolean legacy) {
		return getMaterialModelData(legacy ? "legacy.menu.unlocked_level_button" : "nonlegacy.menu.unlocked_level_button", "next level button");
	}

}
