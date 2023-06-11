package enchants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import enchants.records.OriginEnchant;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnchantAPI {
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The EnchantAPI hasn't been initialized anywhere. Create a new instance of the EnchantAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	public EnchantAPI(final JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new EnchantModule(javaPlugin));
	}

	public interface EnchantType {
		HashMap<String, OriginEnchant> enchants = new HashMap<>();
		static OriginEnchant byName(String name) {
			if (enchants.isEmpty()) try {
				throw new Exception("The Enchant registry is empty.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return enchants.getOrDefault(name, null);
		}

		static void register(OriginEnchant enchant) {
			if (enchants.containsKey(enchant.name())) try {
				throw new Exception("This enchant seems to already be registered.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			enchants.put(enchant.name(), enchant);
		}
	}

	static class EnchantModule extends AbstractModule {
		private final JavaPlugin plugin;

		public EnchantModule(final JavaPlugin plugin) {
			this.plugin = plugin;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
		}
	}
}
