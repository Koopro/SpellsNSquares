package at.koopro.spells_n_squares.core.api;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Interface for spell slot and cooldown management.
 * Defines the API contract for spell management functionality.
 */
public interface ISpellManager {
    /**
     * Sets a spell in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @param spellId The spell ID to assign, or null to clear
     */
    void setSpellInSlot(Player player, int slot, Identifier spellId);
    
    /**
     * Gets the spell in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @return The spell ID, or null if no spell assigned
     */
    Identifier getSpellInSlot(Player player, int slot);
    
    /**
     * Casts the spell in the specified slot for a player.
     * @param player The player casting
     * @param level The level/world
     * @param slot The slot index (0-3)
     * @return true if the spell was successfully cast
     */
    boolean castSpellInSlot(Player player, Level level, int slot);
    
    /**
     * Sets a cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @param ticks The cooldown duration in ticks
     */
    void setCooldown(Player player, Identifier spellId, int ticks);
    
    /**
     * Checks if a spell is on cooldown for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return true if on cooldown
     */
    boolean isOnCooldown(Player player, Identifier spellId);
    
    /**
     * Gets the remaining cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return Remaining cooldown in ticks, or 0 if not on cooldown
     */
    int getRemainingCooldown(Player player, Identifier spellId);
    
    /**
     * Ticks all cooldowns for a player. Call this every tick.
     * @param player The player
     */
    void tickCooldowns(Player player);
    
    /**
     * Clears all spell data for a player (used when player disconnects).
     * @param player The player
     */
    void clearPlayerData(Player player);
    
    /**
     * Syncs spell slots to the client for a server player.
     * @param serverPlayer The server player
     */
    void syncSpellSlotsToClient(ServerPlayer serverPlayer);
    
    /**
     * Syncs cooldowns to the client for a server player.
     * @param serverPlayer The server player
     */
    void syncCooldownsToClient(ServerPlayer serverPlayer);
}

















