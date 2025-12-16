package at.koopro.spells_n_squares.features.enchantments;

import net.minecraft.resources.Identifier;

/**
 * Represents an enchantment or charm that can be applied to items or wands.
 */
public class Enchantment {
    private final Identifier id;
    private final String name;
    private final String description;
    private final EnchantmentType type;
    private final int maxLevel;
    private final boolean isCurse;
    private final boolean isTreasure;
    
    public Enchantment(Identifier id, String name, String description, EnchantmentType type, int maxLevel) {
        this(id, name, description, type, maxLevel, false, false);
    }
    
    public Enchantment(Identifier id, String name, String description, EnchantmentType type, int maxLevel, boolean isCurse, boolean isTreasure) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.maxLevel = maxLevel;
        this.isCurse = isCurse;
        this.isTreasure = isTreasure;
    }
    
    public Identifier getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public EnchantmentType getType() {
        return type;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public boolean isCurse() {
        return isCurse;
    }
    
    public boolean isTreasure() {
        return isTreasure;
    }
    
    /**
     * Types of enchantments that can be applied.
     */
    public enum EnchantmentType {
        WAND,      // Can only be applied to wands
        ITEM,      // Can be applied to any item
        ARMOR,     // Can only be applied to armor
        WEAPON,    // Can only be applied to weapons
        TOOL,      // Can only be applied to tools
        UNIVERSAL  // Can be applied to any item
    }
}

