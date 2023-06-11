package enchants.records;

public record EnchantCost(StarEnchant enchant, int level) {

    /**
     *
     * @return Returns the new enchant cost.
     *
     */
    public double getEnchantCost() {
        return (enchant.price() * level) * enchant.priceMulti();
    }
}
