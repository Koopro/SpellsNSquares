package at.koopro.spells_n_squares.features.wand.core;

import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Helper class for accessing and modifying wand data.
 */
public final class WandDataHelper {
    private WandDataHelper() {
    }
    
    /**
     * Gets the wand data from an item stack, or null if not present.
     */
    public static WandData.WandDataComponent getWandData(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.get(WandData.WAND_DATA.get());
    }
    
    /**
     * Sets the wand core and wood on an item stack.
     * @param stack The item stack
     * @param core The wand core
     * @param wood The wand wood
     * @param attuned Whether the wand is attuned
     */
    public static void setWandData(ItemStack stack, WandCore core, WandWood wood, boolean attuned) {
        if (stack.isEmpty()) {
            return;
        }
        WandData.WandDataComponent data = new WandData.WandDataComponent(
            core.getId(),
            wood.getId(),
            attuned
        );
        stack.set(WandData.WAND_DATA.get(), data);
    }
    
    /**
     * Sets the wand core and wood on an item stack (not attuned).
     */
    public static void setWandData(ItemStack stack, WandCore core, WandWood wood) {
        setWandData(stack, core, wood, false);
    }
    
    /**
     * Sets the attunement status of a wand.
     */
    public static void setAttuned(ItemStack stack, boolean attuned) {
        WandData.WandDataComponent data = getWandData(stack);
        if (data != null) {
            setWandData(stack, data.getCore(), data.getWood(), attuned);
        }
    }
    
    /**
     * Checks if a wand is attuned.
     */
    public static boolean isAttuned(ItemStack stack) {
        WandData.WandDataComponent data = getWandData(stack);
        return data != null && data.attuned();
    }
    
    /**
     * Gets the wand core from an item stack.
     */
    public static WandCore getCore(ItemStack stack) {
        WandData.WandDataComponent data = getWandData(stack);
        return data != null ? data.getCore() : null;
    }
    
    /**
     * Gets the wand wood from an item stack.
     */
    public static WandWood getWood(ItemStack stack) {
        WandData.WandDataComponent data = getWandData(stack);
        return data != null ? data.getWood() : null;
    }
    
    /**
     * Checks if an item stack has wand data.
     */
    public static boolean hasWandData(ItemStack stack) {
        return getWandData(stack) != null;
    }
    
    /**
     * Gets the owner UUID of a wand, or null if the wand has no owner.
     */
    public static UUID getOwner(ItemStack stack) {
        WandData.WandDataComponent data = getWandData(stack);
        if (data == null || !data.hasOwner()) {
            return null;
        }
        try {
            return UUID.fromString(data.ownerId());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

