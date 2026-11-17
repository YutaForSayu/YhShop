package id.my.yuuhaa.yhshop.model;

public enum ShopType {
    SPAWNER,
    ENCHANT;

    public static ShopType fromString(String s) {
        if (s == null) return null;
        switch (s.toLowerCase()) {
            case "spawner":
            case "spawners":
                return SPAWNER;
            case "enchant":
            case "enchants":
            case "enchantment":
            case "enchantments":
                return ENCHANT;
            default:
                return null;
        }
    }

    public String label() {
        return this == SPAWNER ? "Spawner" : "Enchant";
    }

    public String fileName() {
        return this == SPAWNER ? "spawners.yml" : "enchants.yml";
    }
}
