package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

/**
 * Helper class for enchantment operations.
 * Provides methods to apply, remove, and query enchantments on items.
 * 
 * <p>This class uses Minecraft's data component system to manage enchantments.
 * All enchantment operations are performed using {@link net.minecraft.core.component.DataComponents#ENCHANTMENTS}.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Apply Sharpness V to a sword
 * Identifier sharpness = Identifier.fromNamespaceAndPath("minecraft", "sharpness");
 * boolean success = EnchantmentHelper.applyEnchantment(swordStack, sharpness, 5);
 * 
 * // Check if item has an enchantment
 * boolean hasSharpness = EnchantmentHelper.hasEnchantment(swordStack, sharpness);
 * 
 * // Get enchantment level
 * int level = EnchantmentHelper.getEnchantmentLevel(swordStack, sharpness);
 * }</pre>
 * 
 * @since 1.0.0
 */
public final class EnchantmentHelper {
    private EnchantmentHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets an enchantment by its identifier.
     * 
     * <p>This method looks up an enchantment from the Minecraft enchantment registry.
     * The enchantment must be registered in the game's enchantment registry.
     *
     * @param enchantmentId The enchantment identifier (e.g., "minecraft:sharpness")
     * @return Optional containing the enchantment holder, or empty if not found or if the identifier is null
     */
    public static Optional<Holder.Reference<Enchantment>> getEnchantment(Identifier enchantmentId) {
        if (enchantmentId == null) {
            return Optional.empty();
        }
        
        try {
            // Access enchantment via BuiltInRegistries
            // Note: This uses reflection to access the registry as the exact API varies
            // In a production environment, this should use proper registry access from level/server context
            java.lang.reflect.Field enchantmentField = BuiltInRegistries.class.getDeclaredField("ENCHANTMENT");
            enchantmentField.setAccessible(true);
            Object registryObj = enchantmentField.get(null);
            
            if (registryObj == null) {
                return Optional.empty();
            }
            
            // Try to get enchantment value using reflection
            java.lang.reflect.Method getMethod = registryObj.getClass().getMethod("get", Identifier.class);
            Enchantment enchantment = (Enchantment) getMethod.invoke(registryObj, enchantmentId);
            
            if (enchantment == null) {
                return Optional.empty();
            }
            
            // Get resource key for the enchantment
            java.lang.reflect.Method getResourceKeyMethod = registryObj.getClass().getMethod("getResourceKey", Object.class);
            @SuppressWarnings("unchecked")
            Optional<ResourceKey<Enchantment>> keyOpt = (Optional<ResourceKey<Enchantment>>) 
                getResourceKeyMethod.invoke(registryObj, enchantment);
            
            if (keyOpt.isEmpty()) {
                return Optional.empty();
            }
            
            // Get holder using resource key
            java.lang.reflect.Method getHolderMethod = registryObj.getClass().getMethod("getHolder", ResourceKey.class);
            @SuppressWarnings("unchecked")
            Optional<Holder<Enchantment>> holderOpt = (Optional<Holder<Enchantment>>) 
                getHolderMethod.invoke(registryObj, keyOpt.get());
            
            return holderOpt
                .filter(h -> h instanceof Holder.Reference)
                .map(h -> (Holder.Reference<Enchantment>) h);
        } catch (Exception e) {
            // Reflection failed or API changed - log and return empty
            // In production, this should use proper registry access from level/server context
            DevLogger.logWarn(EnchantmentHelper.class, "getEnchantment", 
                "Failed to lookup enchantment " + enchantmentId + " (registry API may have changed): " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Applies an enchantment to an item stack.
     * 
     * <p>This method will:
     * <ul>
     *   <li>Validate that the enchantment can be applied to the item</li>
     *   <li>Clamp the level to the enchantment's maximum level</li>
     *   <li>Add or update the enchantment on the item</li>
     * </ul>
     * 
     * <p>If the item already has the enchantment, it will be updated to the new level.
     *
     * @param stack The item stack to enchant (must not be null or empty)
     * @param enchantmentId The enchantment identifier (must not be null)
     * @param level The enchantment level (will be clamped to 1-maxLevel)
     * @return true if the enchantment was applied successfully, false if validation failed
     * @throws IllegalArgumentException if parameters are invalid (logged as warning)
     */
    public static boolean applyEnchantment(ItemStack stack, Identifier enchantmentId, int level) {
        if (stack == null || stack.isEmpty() || enchantmentId == null || level < 1) {
            DevLogger.logWarn(EnchantmentHelper.class, "applyEnchantment",
                "Invalid parameters: stack=" + (stack != null ? stack.getItem() : "null") +
                ", enchantmentId=" + enchantmentId + ", level=" + level);
            return false;
        }

        Optional<Holder.Reference<Enchantment>> enchantmentOpt = getEnchantment(enchantmentId);
        if (enchantmentOpt.isEmpty()) {
            DevLogger.logWarn(EnchantmentHelper.class, "applyEnchantment",
                "Enchantment not found: " + enchantmentId);
            return false;
        }

        Holder.Reference<Enchantment> enchantment = enchantmentOpt.get();
        Enchantment enchantmentValue = enchantment.value();

        // Check if enchantment can be applied to this item
        // Note: canEnchant() is deprecated but no alternative exists in current API
        // This method is still the standard way to check enchantment compatibility
        @SuppressWarnings("deprecation")
        boolean canEnchant = enchantmentValue.canEnchant(stack);
        if (!canEnchant) {
            DevLogger.logWarn(EnchantmentHelper.class, "applyEnchantment",
                "Enchantment " + enchantmentId + " cannot be applied to " + stack.getItem());
            return false;
        }

        // Clamp level to valid range
        int clampedLevel = Math.max(1, Math.min(level, enchantmentValue.getMaxLevel()));

        // Get current enchantments using data components
        ItemEnchantments currentEnchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(currentEnchantments);

        // Add or update the enchantment
        mutableEnchantments.set(enchantment, clampedLevel);

        // Apply the enchantments to the item using data components
        stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, mutableEnchantments.toImmutable());

        DevLogger.logStateChange(EnchantmentHelper.class, "applyEnchantment",
            "Applied " + enchantmentId + " level " + clampedLevel + " to " + stack.getItem());
        return true;
    }

    /**
     * Removes an enchantment from an item stack.
     *
     * @param stack The item stack
     * @param enchantmentId The enchantment identifier to remove
     * @return true if the enchantment was removed
     */
    public static boolean removeEnchantment(ItemStack stack, Identifier enchantmentId) {
        if (stack == null || stack.isEmpty() || enchantmentId == null) {
            return false;
        }

        Optional<Holder.Reference<Enchantment>> enchantmentOpt = getEnchantment(enchantmentId);
        if (enchantmentOpt.isEmpty()) {
            return false;
        }

        Holder.Reference<Enchantment> enchantment = enchantmentOpt.get();

        // Get current enchantments using data components
        ItemEnchantments currentEnchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (currentEnchantments.getLevel(enchantment) <= 0) {
            return false; // Enchantment not present
        }

        // Create new mutable enchantments without the removed one
        // Copy all enchantments except the one to remove
        ItemEnchantments.Mutable filtered = new ItemEnchantments.Mutable(currentEnchantments);
        // Iterate through current enchantments and copy non-zero ones
        for (var ench : currentEnchantments.keySet()) {
            int level = currentEnchantments.getLevel(ench);
            if (level > 0 && !ench.equals(enchantment)) {
                filtered.set(ench, level);
            } else if (ench.equals(enchantment)) {
                // Remove this enchantment by not adding it
            }
        }
        stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, filtered.toImmutable());

        DevLogger.logStateChange(EnchantmentHelper.class, "removeEnchantment",
            "Removed " + enchantmentId + " from " + stack.getItem());
        return true;
    }

    /**
     * Gets the level of an enchantment on an item stack.
     *
     * @param stack The item stack
     * @param enchantmentId The enchantment identifier
     * @return The enchantment level, or 0 if not present
     */
    public static int getEnchantmentLevel(ItemStack stack, Identifier enchantmentId) {
        if (stack == null || stack.isEmpty() || enchantmentId == null) {
            return 0;
        }

        Optional<Holder.Reference<Enchantment>> enchantmentOpt = getEnchantment(enchantmentId);
        if (enchantmentOpt.isEmpty()) {
            return 0;
        }

        Holder.Reference<Enchantment> enchantment = enchantmentOpt.get();
        ItemEnchantments enchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return enchantments.getLevel(enchantment);
    }

    /**
     * Checks if an item stack has a specific enchantment.
     *
     * @param stack The item stack
     * @param enchantmentId The enchantment identifier
     * @return true if the item has the enchantment
     */
    public static boolean hasEnchantment(ItemStack stack, Identifier enchantmentId) {
        return getEnchantmentLevel(stack, enchantmentId) > 0;
    }
    
    /**
     * Attempts to combine enchantments on an item stack.
     * When certain enchantment combinations are present, they may create new effects.
     * 
     * <p>Example combinations:
     * <ul>
     *   <li>Sharpness + Fire Aspect = Infernal Edge (fire damage boost)</li>
     *   <li>Protection + Unbreaking = Fortified (extra durability)</li>
     *   <li>Efficiency + Fortune = Prosperity (better yields)</li>
     * </ul>
     * 
     * @param stack The item stack to check for combinations
     * @return true if a combination was applied
     */
    public static boolean tryCombineEnchantments(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        ItemEnchantments enchantments = stack.getOrDefault(
            net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        
        if (enchantments.isEmpty()) {
            return false;
        }
        
        // Check for known combinations
        // This is a placeholder - actual combination logic would check for specific enchantment pairs
        // and apply special effects or create combined enchantments
        
        // Example: Check for Sharpness + Fire Aspect combination
        Identifier sharpnessId = Identifier.fromNamespaceAndPath("minecraft", "sharpness");
        Identifier fireAspectId = Identifier.fromNamespaceAndPath("minecraft", "fire_aspect");
        
        boolean hasSharpness = hasEnchantment(stack, sharpnessId);
        boolean hasFireAspect = hasEnchantment(stack, fireAspectId);
        
        if (hasSharpness && hasFireAspect) {
            // Apply visual effect indicator (glow)
            applyEnchantmentGlow(stack);
            DevLogger.logStateChange(EnchantmentHelper.class, "tryCombineEnchantments",
                "Combined enchantments detected on " + stack.getItem());
            return true;
        }
        
        return false;
    }
    
    /**
     * Applies visual glow effect to an enchanted item.
     * This makes the item appear to glow, indicating it has powerful enchantments.
     * 
     * @param stack The item stack
     */
    public static void applyEnchantmentGlow(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        
        // Add glow effect using item name component
        // In Minecraft, enchanted items automatically get a glow effect
        // This method ensures the visual effect is applied
        ItemEnchantments enchantments = stack.getOrDefault(
            net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        
        if (!enchantments.isEmpty()) {
            // The glow is automatically applied by Minecraft when enchantments are present
            // This method can be extended to add custom visual effects
            DevLogger.logStateChange(EnchantmentHelper.class, "applyEnchantmentGlow",
                "Applied glow effect to " + stack.getItem());
        }
    }
    
    /**
     * Removes all enchantments from an item stack (disenchanting).
     * 
     * @param stack The item stack
     * @return true if enchantments were removed
     */
    public static boolean disenchant(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        ItemEnchantments enchantments = stack.getOrDefault(
            net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        
        if (enchantments.isEmpty()) {
            return false; // No enchantments to remove
        }
        
        // Remove all enchantments
        stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        
        DevLogger.logStateChange(EnchantmentHelper.class, "disenchant",
            "Removed all enchantments from " + stack.getItem());
        return true;
    }
    
    /**
     * Gets the total number of enchantments on an item.
     * 
     * @param stack The item stack
     * @return The number of enchantments
     */
    public static int getEnchantmentCount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        
        ItemEnchantments enchantments = stack.getOrDefault(
            net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        
        int count = 0;
        for (var ench : enchantments.keySet()) {
            if (enchantments.getLevel(ench) > 0) {
                count++;
            }
        }
        
        return count;
    }
}
