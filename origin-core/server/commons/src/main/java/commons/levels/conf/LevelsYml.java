package commons.levels.conf;

import commons.conf.BukkitConfig;
import commons.levels.Level;
import commons.levels.reward.LevelReward;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelsYml extends BukkitConfig {

	private final String BASE = "levels.";

	public LevelsYml(ResourceProvider resourceProvider) {
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
		final List<String> commandList = getCommandList(level);

		while(pickedCommands.size() < getCommandCount(level)){
			final Random random = new Random();
			random.setSeed(System.currentTimeMillis());

			final int num = random.nextInt(commandList.size());
			final String pick = commandList.get(num);

			final LevelReward reward = new LevelReward() {
				@Override
				public String getCommand() {
					return pick;
				}

				@Override
				public String getName() {
					StringBuilder builder = new StringBuilder();

					for (String s : getRewardNames(level))
						builder.append("""
						%s""".formatted(s));

					return builder.toString();
				}
			};

			if(!pickedCommands.contains(reward)) pickedCommands.add(reward);
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
		return legacy ? getConfigurationAccessor().getPlaceholder("legacy.menu.title") : getConfigurationAccessor().getPlaceholder("non_legacy.menu.title");
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

			final double requiredExperience = getRequiredExperience(num);
			final int commandCount = getCommandCount(num);
			final List<LevelReward> rewards = getRewards(num);

			levels.add(new Level() {
				@Override
				public double getRequiredExperience() {
					return requiredExperience;
				}

				@Override
				public List<LevelReward> getRewards() {
					return rewards;
				}

				@Override
				public int getRewardCount() {
					return commandCount;
				}

				@Override
				public int levelNumber() {
					return num;
				}
			});

		}

		for (Level level : levels) {
			System.out.println("Number: " + level.levelNumber());
		}
		return levels;
	}
}
