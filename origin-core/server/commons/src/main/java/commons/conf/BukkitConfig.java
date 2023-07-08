package commons.conf;

import commons.conf.wrapper.EffectGroup;
import commons.conf.wrapper.EffectParticle;
import commons.conf.wrapper.EffectSound;
import commons.conf.wrapper.OptionalMessage;
import commons.util.ReflectUtil;
import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

	protected EffectSound getSound(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		float volume = 1.0f;
		if (conf.has("volume"))
			volume = conf.getFloat("volume");

		float pitch = 1.0f;
		if (conf.has("pitch"))
			pitch = conf.getFloat("pitch");

		Sound sound = null;
		if (conf.has("sound")) {
			sound = ReflectUtil.getEnum(Sound.class, conf.getString("sound"));
			if (sound == null)
				logError(resourceProvider.getLogger(), path + ".sound", "sound");
		}
		return new EffectSound(sound, volume, pitch);
	}

	protected EffectParticle getParticle(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		int count = 1;
		if (conf.has("count"))
			count = conf.getInt("count");

		Particle particle = null;
		if (conf.has("sound")) {
			particle = ReflectUtil.getEnum(Particle.class, conf.getString("particle"));
			if (particle == null)
				logError(resourceProvider.getLogger(), path + ".particle", "particle");
		}
		return new EffectParticle(particle, count);
	}

	protected EffectGroup getEffect(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		EffectSound sound = EffectSound.EMPTY;
		if (conf.has("sound"))
			sound = getSound(path);

		EffectParticle particle = EffectParticle.EMPTY;
		if (conf.has("particle"))
			particle = getParticle(path);

		return new EffectGroup(sound, particle);
	}

	protected OptionalMessage getOptional(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		PlaceholderMessage msg = null;
		if(conf.has(path))
			msg = conf.getPlaceholder(path);

		return new OptionalMessage(msg);
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

}
