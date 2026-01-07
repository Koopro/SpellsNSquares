package at.koopro.spells_n_squares.features.wand.system;

import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.player.PlayerItemUtils;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Manages wand loyalty bonuses.
 * Wands work better for their owners, providing cooldown reduction and power bonuses.
 */
public final class WandLoyaltySystem {
    private WandLoyaltySystem() {
    }
    
    /**
     * Base loyalty bonus multiplier for owner (10% bonus).
     */
    private static final float OWNER_BONUS_MULTIPLIER = 1.1f;
    
    /**
     * Base loyalty cooldown reduction for owner (10% reduction).
     */
    private static final float OWNER_COOLDOWN_REDUCTION = 0.9f;
    
    /**
     * Penalty multiplier for non-owner (20% penalty).
     */
    private static final float NON_OWNER_PENALTY_MULTIPLIER = 0.8f;
    
    /**
     * Penalty cooldown multiplier for non-owner (20% longer cooldown).
     */
    private static final float NON_OWNER_COOLDOWN_PENALTY = 1.2f;
    
    /**
     * Gets the power multiplier for a spell cast with a wand.
     * Returns bonus if player owns the wand, penalty if someone else owns it.
     * 
     * @param player The player casting
     * @param wand The wand being used
     * @return Power multiplier (1.0 = no change, 1.1 = 10% bonus, 0.8 = 20% penalty)
     */
    public static float getPowerMultiplier(Player player, ItemStack wand) {
        if (player == null || wand == null || wand.isEmpty()) {
            return 1.0f;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null) {
            return 1.0f; // Invalid wand
        }
        
        if (!wandData.hasOwner()) {
            return 1.0f; // No owner, no bonus or penalty
        }
        
        if (wandData.isOwner(player.getUUID())) {
            return OWNER_BONUS_MULTIPLIER; // Owner gets bonus
        } else {
            return NON_OWNER_PENALTY_MULTIPLIER; // Non-owner gets penalty
        }
    }
    
    /**
     * Gets the cooldown multiplier for a spell cast with a wand.
     * Returns reduction if player owns the wand, penalty if someone else owns it.
     * 
     * @param player The player casting
     * @param wand The wand being used
     * @return Cooldown multiplier (1.0 = no change, 0.9 = 10% reduction, 1.2 = 20% longer)
     */
    public static float getCooldownMultiplier(Player player, ItemStack wand) {
        if (player == null || wand == null || wand.isEmpty()) {
            return 1.0f;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null) {
            return 1.0f; // Invalid wand
        }
        
        if (!wandData.hasOwner()) {
            return 1.0f; // No owner, no bonus or penalty
        }
        
        if (wandData.isOwner(player.getUUID())) {
            return OWNER_COOLDOWN_REDUCTION; // Owner gets reduction
        } else {
            return NON_OWNER_COOLDOWN_PENALTY; // Non-owner gets penalty
        }
    }
    
    /**
     * Gets the currently held wand for a player.
     * 
     * @param player The player
     * @return The wand item stack, or empty if no wand held
     */
    public static ItemStack getHeldWand(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        return PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
    }
    
    /**
     * Checks if a player is using their own wand.
     * 
     * @param player The player
     * @return true if the player is holding their own wand
     */
    public static boolean isUsingOwnWand(Player player) {
        if (player == null) {
            return false;
        }
        
        ItemStack wand = getHeldWand(player);
        if (wand.isEmpty()) {
            return false;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null || !wandData.hasOwner()) {
            return false;
        }
        
        return wandData.isOwner(player.getUUID());
    }
}








