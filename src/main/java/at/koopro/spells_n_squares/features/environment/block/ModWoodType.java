package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.material.MapColor;

/**
 * Enum defining all custom wood types for the mod.
 * These are wood types that don't exist in vanilla Minecraft.
 */
public enum ModWoodType {
    ASH("ash", MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.TERRACOTTA_BROWN),
    BEECH("beech", MapColor.SAND, MapColor.TERRACOTTA_ORANGE),
    BLACKTHORN("blackthorn", MapColor.COLOR_BLACK, MapColor.TERRACOTTA_GRAY),
    CEDAR("cedar", MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_BROWN),
    CHESTNUT("chestnut", MapColor.TERRACOTTA_BROWN, MapColor.COLOR_BROWN),
    CYPRESS("cypress", MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY),
    DOGWOOD("dogwood", MapColor.TERRACOTTA_PINK, MapColor.COLOR_GRAY),
    ELDER("elder", MapColor.TERRACOTTA_WHITE, MapColor.QUARTZ),
    ELM("elm", MapColor.TERRACOTTA_ORANGE, MapColor.COLOR_BROWN),
    FIR("fir", MapColor.TERRACOTTA_CYAN, MapColor.TERRACOTTA_BROWN),
    HAWTHORN("hawthorn", MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_BROWN),
    HOLLY("holly", MapColor.COLOR_RED, MapColor.COLOR_GREEN),
    HORNBEAM("hornbeam", MapColor.SAND, MapColor.COLOR_GRAY),
    LAUREL("laurel", MapColor.COLOR_GREEN, MapColor.TERRACOTTA_BROWN),
    MAHOGANY("mahogany", MapColor.TERRACOTTA_RED, MapColor.COLOR_BROWN),
    MAPLE("maple", MapColor.COLOR_ORANGE, MapColor.TERRACOTTA_ORANGE),
    PEAR("pear", MapColor.TERRACOTTA_PINK, MapColor.TERRACOTTA_BROWN),
    PINE("pine", MapColor.TERRACOTTA_YELLOW, MapColor.TERRACOTTA_BROWN),
    POPLAR("poplar", MapColor.TERRACOTTA_YELLOW, MapColor.COLOR_GRAY),
    REDWOOD("redwood", MapColor.TERRACOTTA_RED, MapColor.COLOR_RED),
    ROWAN("rowan", MapColor.TERRACOTTA_ORANGE, MapColor.COLOR_BROWN),
    SILVER_LIME("silver_lime", MapColor.QUARTZ, MapColor.COLOR_LIGHT_GRAY),
    SYCAMORE("sycamore", MapColor.SAND, MapColor.TERRACOTTA_WHITE),
    WALNUT("walnut", MapColor.COLOR_BROWN, MapColor.TERRACOTTA_BROWN),
    WILLOW("willow", MapColor.COLOR_LIGHT_GREEN, MapColor.TERRACOTTA_BROWN),
    YEW("yew", MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_BROWN);
    
    private final String id;
    private final MapColor planksColor;
    private final MapColor barkColor;
    
    ModWoodType(String id, MapColor planksColor, MapColor barkColor) {
        this.id = id;
        this.planksColor = planksColor;
        this.barkColor = barkColor;
    }
    
    public String getId() {
        return id;
    }
    
    /**
     * Gets the serialized name for use in registry and data.
     */
    public String getSerializedName() {
        return id;
    }
    
    /**
     * Gets the map color for planks and stripped wood.
     */
    public MapColor getPlanksColor() {
        return planksColor;
    }
    
    /**
     * Gets the map color for bark/logs.
     */
    public MapColor getBarkColor() {
        return barkColor;
    }
    
    /**
     * Gets the translation key prefix for this wood type.
     */
    public String getTranslationKeyPrefix() {
        return "block.spells_n_squares." + id;
    }
    
    /**
     * Finds a wood type by its ID.
     * @return The wood type, or null if not found
     */
    public static ModWoodType fromId(String id) {
        for (ModWoodType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}








