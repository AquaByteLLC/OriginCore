package farming.impl.conf;

import lombok.Getter;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import org.bukkit.configuration.file.YamlConfiguration;

public class BlocksConfig extends YamlFile {

	@Getter private final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

	public BlocksConfig(ResourceProvider resourceProvider) {
		super("blocks.yml", resourceProvider);
		setDefaultTemplate();
	}
}
