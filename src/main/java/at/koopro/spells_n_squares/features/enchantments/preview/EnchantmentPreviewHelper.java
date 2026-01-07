package at.koopro.spells_n_squares.features.enchantments.preview;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;

/**
 * Helper class for enchantment preview functionality.
 * Provides methods to preview enchantment effects before applying them.
 */
public final class EnchantmentPreviewHelper {
    
    private EnchantmentPreviewHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents enchantment preview information.
     */
    public record EnchantmentPreview(
        Holder.Reference<Enchantment> enchantment,
        int level,
        Component description,
        float powerIncrease,
        List<Component> effects
    ) {}
    
    /**
     * Generates a preview for applying an enchantment to an item.
     * 
     * @param item The item stack
     * @param enchantment The enchantment to preview
     * @param level The enchantment level
     * @return Preview information
     */
    public static EnchantmentPreview generatePreview(ItemStack item, Holder.Reference<Enchantment> enchantment, int level) {
        if (item == null || item.isEmpty() || enchantment == null || level <= 0) {
            return null;
        }
        
        Enchantment ench = enchantment.value();
        Component description = Enchantment.getFullname(enchantment, level);
        
        // Calculate estimated power increase (simplified)
        float powerIncrease = calculatePowerIncrease(ench, level);
        
        // Get enchantment effects
        List<Component> effects = getEnchantmentEffects(enchantment, level);
        
        return new EnchantmentPreview(enchantment, level, description, powerIncrease, effects);
    }
    
    /**
     * Calculates estimated power increase from enchantment.
     */
    private static float calculatePowerIncrease(Enchantment enchantment, int level) {
        // Simplified calculation - in full implementation would use actual enchantment values
        return level * 0.1f;
    }
    
    /**
     * Gets enchantment effects as components.
     */
    private static List<Component> getEnchantmentEffects(Holder.Reference<Enchantment> enchantment, int level) {
        List<Component> effects = new ArrayList<>();
        
        // Add base description - use the enchantment holder
        effects.add(Enchantment.getFullname(enchantment, level));
        
        // Add level-specific effects if applicable
        if (level > 1) {
            effects.add(Component.translatable("enchantment.level." + level));
        }
        
        return effects;
    }
    
    /**
     * Compares two enchantment configurations.
     * 
     * @param item The item
     * @param currentEnchantments Current enchantments
     * @param previewEnchantments Preview enchantments
     * @return Comparison result
     */
    public static EnchantmentComparison compare(ItemStack item, 
                                                Map<Holder.Reference<Enchantment>, Integer> currentEnchantments,
                                                Map<Holder.Reference<Enchantment>, Integer> previewEnchantments) {
        if (item == null || item.isEmpty()) {
            return new EnchantmentComparison(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }
        
        List<Component> added = new ArrayList<>();
        List<Component> removed = new ArrayList<>();
        List<Component> changed = new ArrayList<>();
        
        // Find added enchantments
        for (Map.Entry<Holder.Reference<Enchantment>, Integer> entry : previewEnchantments.entrySet()) {
            if (!currentEnchantments.containsKey(entry.getKey())) {
                added.add(Enchantment.getFullname(entry.getKey(), entry.getValue()));
            } else {
                int currentLevel = currentEnchantments.get(entry.getKey());
                if (entry.getValue() != currentLevel) {
                    changed.add(Component.translatable("enchantment.changed", 
                        Enchantment.getFullname(entry.getKey(), currentLevel),
                        Enchantment.getFullname(entry.getKey(), entry.getValue())));
                }
            }
        }
        
        // Find removed enchantments
        for (Map.Entry<Holder.Reference<Enchantment>, Integer> entry : currentEnchantments.entrySet()) {
            if (!previewEnchantments.containsKey(entry.getKey())) {
                removed.add(Enchantment.getFullname(entry.getKey(), entry.getValue()));
            }
        }
        
        return new EnchantmentComparison(added, removed, changed);
    }
    
    /**
     * Represents enchantment comparison result.
     */
    public record EnchantmentComparison(
        List<Component> added,
        List<Component> removed,
        List<Component> changed
    ) {}
}

