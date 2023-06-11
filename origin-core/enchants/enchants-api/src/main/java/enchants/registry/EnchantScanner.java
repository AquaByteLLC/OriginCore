package enchants.registry;

import enchants.annotations.RegisterEnchant;
import enchants.records.StarEnchant;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class EnchantScanner {
    private final Reflections reflections;

    /**
     *
     * @param plugin Used for gathering information on where to scan.
     *
     */
    public EnchantScanner(JavaPlugin plugin) {
        this.reflections = new Reflections(
                new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(plugin.getName())));
    }

    /**
     * This method scans for all fields annotated with the {@link RegisterEnchant} annotation.
     */
    public void scan() {
        reflections.getFieldsAnnotatedWith(RegisterEnchant.class).forEach(aClass -> {
            try {
                StarEnchant enchant = (StarEnchant) aClass.getType().newInstance();
                EnchantRegistry.get().register(enchant.name(), enchant);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}