package enchants.registry;

import enchants.records.StarEnchant;
import java.util.HashMap;
import java.util.Map;

public class EnchantRegistry {
    private final Map<String, StarEnchant> enchantMap;
    private static EnchantRegistry registry;

    public static EnchantRegistry get() {
        if (EnchantRegistry.registry == null) {
            EnchantRegistry.registry = new EnchantRegistry();
        }
        return EnchantRegistry.registry;
    }
    public EnchantRegistry() {
        this.enchantMap = new HashMap<>();
    }

    /**
     *
     * @param ID This is how the enchant is identified.
     * @param enchant {@link StarEnchant} record.
     */
    public void register(String ID, StarEnchant enchant) {
        if (isRegistered(enchant)) return;
        enchantMap.put(ID, enchant);
    }

    /**
     *
     * @param enchant {@link  StarEnchant} record.
     *
     */
    public void unregister(StarEnchant enchant) {
        if (!isRegistered(enchant)) return;
        enchantMap.remove(enchant.name());
    }

    /**
     *
     * @param enchant {@link StarEnchant} record.
     *
     * @return Returns a boolean value based on weather or not the enchant is in the enchant map.
     */
    public boolean isRegistered(StarEnchant enchant) {
        return enchantMap.containsValue(enchant);
    }
}