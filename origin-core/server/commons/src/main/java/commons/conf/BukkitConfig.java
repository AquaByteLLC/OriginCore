package commons.conf;

import commons.util.ReflectUtil;
import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.apache.commons.lang.enums.EnumUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author vadim
 */
public abstract class BukkitConfig extends YamlFile {

	public BukkitConfig(String file, ResourceProvider resourceProvider) {
		super(file, resourceProvider);
	}

	protected ItemStack getItem(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		String   name = conf.getString("name");
		String[] lore = conf.getStringArray("lore");
		Material type = Material.matchMaterial(conf.getString("type"));

		if (name == null || lore == null || type == null)
			logError(resourceProvider.getLogger(), path, "item element");
		assert type != null;

		return ItemBuilder.create(type).displayName(name).lore(lore).build();
	}

	// for custom UnformattedItem impls
	private UnformattedItemFactory ufif = UnformattedItem::new;

	protected void setUnformattedItemFactory(UnformattedItemFactory factory) {
		if (factory != null)
			this.ufif = factory;
	}

	@SuppressWarnings("unchecked")
	protected <U extends UnformattedItem> U getUnformatted(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		Material type = null;
		if (conf.has("type")) {
			type = Material.matchMaterial(conf.getString("type"));
			if (type == null)
				logError(resourceProvider.getLogger(), path + ".type", "item type");
		}
		return (U) ufif.newUnformattedItem(type, conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	protected UnplayedSound getSound(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		float volume = 1.0f;
		if(conf.has("volume"))
			volume = conf.getFloat("volume");

		float pitch = 1.0f;
		if(conf.has("pitch"))
			pitch = conf.getFloat("pitch");

		Sound sound = null;
		if (conf.has("sound")) {
			sound = ReflectUtil.getEnum(Sound.class, conf.getString("sound"));
			if (sound == null)
				logError(resourceProvider.getLogger(), path + ".sound", "sound");
		}
		return new UnplayedSound(volume, pitch, sound);

	}

	protected interface UnformattedItemFactory {

		UnformattedItem newUnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore);

	}

	public static class UnformattedItem {

		protected final Material material;
		protected final PlaceholderMessage name;
		protected final List<PlaceholderMessage> lore;

		protected UnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.material = material;
			this.name     = name;
			this.lore     = lore;
		}

		public ItemBuilder format(Placeholder placeholder) {
			if (material == null)
				throw new UnsupportedOperationException("type unset, call #format(Material, Placeholder)");
			return format(material, placeholder);
		}

		public ItemBuilder format(Material material, Placeholder placeholder) {
			return ItemBuilder.create(material)
							  .displayName(name.format(placeholder))
							  .lore(lore.stream().map(msg -> msg.format(placeholder)).toList());
		}

	}

	public static class UnplayedSound {

		public final float volume;
		public final float pitch;
		public final Sound sound;

		public UnplayedSound(float volume, float pitch, Sound sound) {
			this.volume = volume;
			this.pitch  = pitch;
			this.sound  = sound;
		}

		public void playTo(Player player, Location location) {
			player.playSound(location, sound, volume, pitch);
		}

		public void playAt(World world, Location location) {
			world.playSound(location, sound, volume, pitch);
		}

	}

}
