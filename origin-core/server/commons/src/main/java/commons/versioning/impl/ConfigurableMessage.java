package commons.versioning.impl;

import commons.versioning.VersionSender;
import commons.versioning.api.MessageVersioned;
import me.vadim.util.conf.bukkit.wrapper.OptionalMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.List;

import static commons.versioning.impl.ConfigurableMessage.EffectGroupPaths.getOrCreate;

public class ConfigurableMessage implements MessageVersioned {
	private final VersionSender sender;
	private final String messageKey;
	private final YamlConfiguration cfg;
	private final OptionalMessage legacy;
	private final OptionalMessage nonLegacy;

	public ConfigurableMessage(VersionSender sender, String messageKey, List<String> baseMessageLegacy, List<String> baseMessageNonLegacy) {
		this.sender = sender;
		this.messageKey = messageKey;
		this.cfg = sender.cfg();

		EffectGroupPaths.createSectionsAndCreateKey(sender, messageKey, cfg, baseMessageLegacy, baseMessageNonLegacy);

		this.legacy = createMessage(true);
		this.nonLegacy = createMessage(false);

		if (legacy == null || nonLegacy == null)
			throw new RuntimeException("The message with the key " + messageKey + " was null, please check the configuration!");
	}

	@Override
	public OptionalMessage legacy() {
		return this.legacy;
	}

	@Override
	public OptionalMessage nonLegacy() {
		return this.nonLegacy;
	}

	public interface EffectGroupPaths {

		// BASES
		String messages = "Messages";
		String base = messages + ".%key%";
		String legacy = base + ".legacy";
		String nonLegacy = base + ".nonLegacy";

		// SECTIONS

		String messageSectionLegacy = legacy + ".messages";
		String messageSectionNonLegacy = nonLegacy + ".messages";

		// DATA
		String messageLegacy = messageSectionLegacy + ".message";
		String messageNonLegacy = messageSectionNonLegacy + ".message";


		static String getAndReplace(String current, String enchantKey) {
			return current.replaceAll("%key%", enchantKey);
		}

		static String getAsRelative(String global) {
			return global.substring(global.lastIndexOf('.') + 1);
		}

		static ConfigurationSection getOrCreate(ConfigurationSection section, String path) {
			return section.isConfigurationSection(path) ? section.getConfigurationSection(path) : section.createSection(path);
		}

		static void createSectionsAndCreateKey(VersionSender sender, String key, YamlConfiguration configuration, List<String> baseMessageLegacy, List<String> baseMessageNonLegacy) {
			final String messageSectionLegacyReplaced = getAndReplace(messageSectionLegacy, key);
			final String messageSectionNonLegacyReplaced = getAndReplace(messageSectionNonLegacy, key);
			final String legacyBaseReplaced = getAndReplace(legacy, key);
			final String nonLegacyBaseReplaced = getAndReplace(nonLegacy, key);
			final String baseSectionReplaced = getAndReplace(base, key);

			if (configuration.isConfigurationSection(baseSectionReplaced)) return;

			final ConfigurationSection effectsSectionCfg = getOrCreate(configuration, messages);

			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection legacySection = getOrCreate(configuration, legacyBaseReplaced);
			final ConfigurationSection nonLegacySection = getOrCreate(configuration, nonLegacyBaseReplaced);

			final ConfigurationSection messageSectionLegacyCfg = getOrCreate(configuration, messageSectionLegacyReplaced);
			final ConfigurationSection messageSectionNonLegacyCfg = getOrCreate(configuration, messageSectionNonLegacyReplaced);

			messageSectionLegacyCfg.set(getAsRelative(messageLegacy), baseMessageLegacy);
			messageSectionNonLegacyCfg.set(getAsRelative(messageNonLegacy), baseMessageNonLegacy);

			System.out.println("MessageLegacy: " + messageSectionLegacyCfg.get(getAsRelative(messageLegacy)));
			System.out.println("Creating: " + key);

			try {
				configuration.save(sender.file());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public List<String> getMessage(boolean legacy) {
		final String path = legacy ? EffectGroupPaths.messageLegacy : EffectGroupPaths.messageNonLegacy;
		final String type = EffectGroupPaths.getAndReplace(path, messageKey);
		return cfg.getStringList(type);
	}

	public OptionalMessage createMessage(boolean legacy) {
		final StringBuilder listAsStr = new StringBuilder();

		for(String s : getMessage(legacy))
			listAsStr.append("""
			%s""".formatted(s));

		return new OptionalMessage(listAsStr.toString());
	}
}
