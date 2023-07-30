package farming.impl.action;

import commons.versioning.VersionSender;
import commons.versioning.impl.ConfigurableMessage;
import farming.impl.FarmingPlugin;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static commons.util.ReflectUtil.sneaky;

public class Messages {

	public static class MessagesYml {
		private final File messagesYml;
		private final File actionsFolder;
		private final YamlConfiguration fileConfiguration;
		private final FarmingPlugin plugin;

		@SneakyThrows
		public MessagesYml() {
			this.plugin = FarmingPlugin.getInjector().getInstance(FarmingPlugin.class);

			this.actionsFolder = new File(plugin.getDataFolder(), "actions");
			this.messagesYml = new File(actionsFolder, "message.yml");

			this.fileConfiguration = YamlConfiguration.loadConfiguration(messagesYml);

			create();
		}

		public void create() {
			try {
				if (!this.messagesYml.exists()) {
					File parent = this.messagesYml.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}

					this.messagesYml.createNewFile();
				}
			} catch (IOException var2) {
				sneaky(var2);
			}

		}
	}

	private static MessagesYml messageCfg;
	public static VersionSender versionSender;

	public static ConfigurableMessage REGION_REGISTER;
	public static ConfigurableMessage REGION_REMOVE;
	public static ConfigurableMessage BLOCK_NOT_FOUND;
	public static ConfigurableMessage REGION_NOT_REGISTERED;
	public static ConfigurableMessage WG_REGION_NOT_FOUND;
	public static ConfigurableMessage ENCHNAT_PROC;


	@SneakyThrows
	public static void init() {
		messageCfg = new MessagesYml();
		versionSender = new VersionSender(messageCfg.messagesYml, messageCfg.fileConfiguration);

		REGION_REGISTER = new ConfigurableMessage(versionSender, "region_register",
				List.of("&aThe region {region_name} has been registered with the block {block_name}."),
				List.of("&aThe region {region_name} has been registered with the block {block_name}."));

		REGION_REMOVE = new ConfigurableMessage(versionSender, "region_remove",
				List.of("&aThe region {region_name} was removed."),
				List.of("&aThe region {region_name} was removed."));

		BLOCK_NOT_FOUND = new ConfigurableMessage(versionSender, "block_not_found",
				List.of("&cThe block {block_name} was not found!"),
				List.of("&cThe block {block_name} was not found!"));

		REGION_NOT_REGISTERED = new ConfigurableMessage(versionSender, "region_not_registered",
				List.of("&cThe region {region_name} is not registered at the moment."),
				List.of("&cThe region {region_name} is not registered at the moment."));

		ENCHNAT_PROC = new ConfigurableMessage(versionSender, "enchant_proc",
				List.of("&c{enchant_name} has been activated."),
				List.of("&c{enchant_name} has been activated."));

		WG_REGION_NOT_FOUND = new ConfigurableMessage(versionSender, "wg_region_not_found",
				List.of("&cThere is no region with the name {region_name} registered in World Guard."),
				List.of("&cThere is no region with the name {region_name} registered in World Guard."));

	}
}
