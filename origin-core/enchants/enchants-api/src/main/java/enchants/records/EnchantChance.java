package enchants.records;

public record EnchantChance(StarEnchant enchant, int level) {

    public double getChance() {
        return (enchant.chance() * level) * enchant.chanceMulti();
    }
}
