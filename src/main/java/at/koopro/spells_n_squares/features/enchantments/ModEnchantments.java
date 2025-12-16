package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.core.registry.EnchantmentRegistry;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;

/**
 * Registry and initialization of all mod enchantments.
 */
public final class ModEnchantments {
    private ModEnchantments() {
    }
    
    /**
     * Registers all enchantments in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Wand enchantments
        EnchantmentRegistry.register("wand_power", new Enchantment(
            ModIdentifierHelper.modId("wand_power"),
            "Wand Power",
            "Increases spell power and damage",
            Enchantment.EnchantmentType.WAND,
            5
        ));
        
        EnchantmentRegistry.register("wand_range", new Enchantment(
            ModIdentifierHelper.modId("wand_range"),
            "Wand Range",
            "Increases spell casting range",
            Enchantment.EnchantmentType.WAND,
            5
        ));
        
        EnchantmentRegistry.register("wand_efficiency", new Enchantment(
            ModIdentifierHelper.modId("wand_efficiency"),
            "Wand Efficiency",
            "Reduces spell cooldown time",
            Enchantment.EnchantmentType.WAND,
            5
        ));
        
        EnchantmentRegistry.register("wand_accuracy", new Enchantment(
            ModIdentifierHelper.modId("wand_accuracy"),
            "Wand Accuracy",
            "Increases spell accuracy and reduces miscast chance",
            Enchantment.EnchantmentType.WAND,
            5
        ));
        
        // Item enchantments
        EnchantmentRegistry.register("unbreaking", new Enchantment(
            ModIdentifierHelper.modId("unbreaking"),
            "Unbreaking",
            "Reduces item durability loss",
            Enchantment.EnchantmentType.UNIVERSAL,
            3
        ));
        
        EnchantmentRegistry.register("mending", new Enchantment(
            ModIdentifierHelper.modId("mending"),
            "Mending",
            "Repairs item using experience",
            Enchantment.EnchantmentType.UNIVERSAL,
            1,
            false,
            true
        ));
        
        // Charm enchantments (temporary effects)
        EnchantmentRegistry.register("feather_falling", new Enchantment(
            ModIdentifierHelper.modId("feather_falling"),
            "Feather Falling",
            "Reduces fall damage",
            Enchantment.EnchantmentType.ARMOR,
            4
        ));
        
        EnchantmentRegistry.register("protection", new Enchantment(
            ModIdentifierHelper.modId("protection"),
            "Protection",
            "Reduces all types of damage",
            Enchantment.EnchantmentType.ARMOR,
            4
        ));
    }
}

