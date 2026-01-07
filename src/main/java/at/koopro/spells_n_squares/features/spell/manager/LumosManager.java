package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages Lumos spell state for players.
 * Lumos uses item data components to store active state.
 */
public class LumosManager {
    // Per-player Lumos active state (backup storage if item component not available)
    private static final Map<UUID, Boolean> playerLumosState = new HashMap<>();
    
    /**
     * Checks if Lumos is active for a player.
     * Checks the player's held items for Lumos active data component.
     * @param player The player
     * @return true if Lumos is active
     */
    public static boolean isLumosActive(Player player) {
        // Check held items for Lumos active component
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) {
            Boolean active = mainHand.get(ModDataComponents.LUMOS_ACTIVE.get());
            if (active != null && active) {
                return true;
            }
        }
        
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty()) {
            Boolean active = offHand.get(ModDataComponents.LUMOS_ACTIVE.get());
            if (active != null && active) {
                return true;
            }
        }
        
        // Fallback to in-memory storage
        return playerLumosState.getOrDefault(player.getUUID(), false);
    }
    
    /**
     * Sets Lumos active state for a player.
     * Updates the player's held items with Lumos active data component.
     * @param player The player
     * @param active Whether Lumos should be active
     */
    public static void setLumosActive(Player player, boolean active) {
        UUID uuid = player.getUUID();
        
        // Update held items
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) {
            if (active) {
                mainHand.set(ModDataComponents.LUMOS_ACTIVE.get(), true);
            } else {
                mainHand.remove(ModDataComponents.LUMOS_ACTIVE.get());
            }
        }
        
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty()) {
            if (active) {
                offHand.set(ModDataComponents.LUMOS_ACTIVE.get(), true);
            } else {
                offHand.remove(ModDataComponents.LUMOS_ACTIVE.get());
            }
        }
        
        // Update in-memory storage
        if (active) {
            playerLumosState.put(uuid, true);
        } else {
            playerLumosState.remove(uuid);
        }
    }
    
    /**
     * Toggles Lumos on/off for a player.
     * @param player The player
     * @param stack The item stack (wand) to toggle Lumos on
     */
    public static void toggleLumos(net.minecraft.server.level.ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        
        boolean currentlyActive = isLumosActive(player);
        boolean newState = !currentlyActive;
        
        setLumosActive(player, newState);
        
        // Update the wand item stack
        if (newState) {
            stack.set(ModDataComponents.LUMOS_ACTIVE.get(), true);
        } else {
            stack.remove(ModDataComponents.LUMOS_ACTIVE.get());
        }
    }
    
    /**
     * Clears all Lumos data for a player.
     * Called when a player disconnects.
     * @param player The player
     */
    public static void clearPlayerData(Player player) {
        UUID uuid = player.getUUID();
        playerLumosState.remove(uuid);
        
        // Clear from held items
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) {
            mainHand.remove(ModDataComponents.LUMOS_ACTIVE.get());
        }
        
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty()) {
            offHand.remove(ModDataComponents.LUMOS_ACTIVE.get());
        }
    }
}
