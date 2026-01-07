package at.koopro.spells_n_squares.services.spell.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Service interface for spell management operations.
 * Provides a clean API for other modules to interact with the spell system.
 */
public interface ISpellManager {
    /**
     * Checks if a slot index is valid.
     * @param slot The slot index
     * @return true if valid (0-3)
     */
    boolean isValidSlot(int slot);
    
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
     * Ticks all cooldowns for a player. Call this every tick.
     * @param player The player
     */
    void tickCooldowns(Player player);
    
    /**
     * Starts a hold-to-cast spell for a player.
     * @param player The player
     * @param spellId The spell ID
     */
    void startHoldSpell(Player player, Identifier spellId);
    
    /**
     * Stops a hold-to-cast spell for a player.
     * @param player The player
     */
    void stopHoldSpell(Player player);
    
    /**
     * Gets the active hold-to-cast spell for a player.
     * @param player The player
     * @return The spell ID, or null if no hold spell is active
     */
    Identifier getActiveHoldSpell(Player player);
    
    /**
     * Ticks all active hold-to-cast spells for all players in a level.
     * @param level The level to tick spells in
     */
    void tickHoldSpells(Level level);
    
    /**
     * Learns a spell for a player.
     * @param player The player
     * @param spellId The spell ID to learn
     */
    void learnSpell(Player player, Identifier spellId);
    
    /**
     * Forgets a spell for a player.
     * @param player The player
     * @param spellId The spell ID to forget
     */
    void forgetSpell(Player player, Identifier spellId);
    
    /**
     * Checks if a player has learned a specific spell.
     * @param player The player
     * @param spellId The spell ID to check
     * @return true if the player has learned the spell
     */
    boolean hasLearnedSpell(Player player, Identifier spellId);
    
    /**
     * Syncs spell slots to the client for a server player.
     * @param player The server player
     */
    void syncSpellSlotsToClient(Player player);
    
    /**
     * Syncs cooldowns to the client for a server player.
     * @param player The server player
     */
    void syncCooldownsToClient(Player player);
    
    /**
     * Clears all spell data for a player.
     * Called when a player disconnects.
     * @param player The player
     */
    void clearPlayerData(Player player);
}






