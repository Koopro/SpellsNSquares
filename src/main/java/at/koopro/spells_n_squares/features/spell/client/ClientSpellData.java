package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side storage for spell slot data and cooldowns.
 * This is synced from the server via network packets.
 */
public class ClientSpellData {
    // Client-side spell slot assignments
    private static final Identifier[] clientSpellSlots = new Identifier[SpellManager.MAX_SLOTS];
    
    // Client-side cooldown tracking
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<Identifier, Integer> clientCooldowns = new HashMap<>();
    
    // Client-side player class
    private static PlayerClass clientPlayerClass = PlayerClass.NONE;
    
    /**
     * Sets a spell in a specific slot on the client.
     * @param slot The slot index (0-3)
     * @param spellId The spell ID, or null to clear
     */
    public static void setSpellSlot(int slot, Identifier spellId) {
        if (SpellManager.isValidSlot(slot)) {
            clientSpellSlots[slot] = spellId;
        }
    }
    
    /**
     * Gets the spell in a specific slot on the client.
     * @param slot The slot index (0-3)
     * @return The spell ID, or null if no spell assigned
     */
    public static Identifier getSpellInSlot(int slot) {
        if (!SpellManager.isValidSlot(slot)) {
            return null;
        }
        return clientSpellSlots[slot];
    }
    
    /**
     * Updates cooldowns on the client.
     * @param cooldowns Map of spell ID -> remaining cooldown ticks
     */
    public static void updateCooldowns(Map<Identifier, Integer> cooldowns) {
        clientCooldowns.clear();
        clientCooldowns.putAll(cooldowns);
    }
    
    /**
     * Checks if a spell is on cooldown on the client.
     * @param spellId The spell ID
     * @return true if on cooldown
     */
    public static boolean isOnCooldown(Identifier spellId) {
        Integer remaining = clientCooldowns.get(spellId);
        return remaining != null && remaining > 0;
    }
    
    /**
     * Gets the remaining cooldown for a spell on the client.
     * @param spellId The spell ID
     * @return Remaining cooldown in ticks, or 0 if not on cooldown
     */
    public static int getRemainingCooldown(Identifier spellId) {
        Integer remaining = clientCooldowns.get(spellId);
        return remaining != null ? remaining : 0;
    }
    
    /**
     * Ticks all cooldowns on the client. Call this every client tick.
     */
    public static void tickCooldowns() {
        clientCooldowns.entrySet().removeIf(entry -> {
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                return true; // Remove expired cooldowns
            }
            entry.setValue(remaining);
            return false;
        });
    }
    
    /**
     * Sets the player class on the client.
     * @param playerClass The player class
     */
    public static void setPlayerClass(PlayerClass playerClass) {
        clientPlayerClass = playerClass != null ? playerClass : PlayerClass.NONE;
    }
    
    /**
     * Gets the player class on the client.
     * @return The player class, or NONE if not set
     */
    public static PlayerClass getPlayerClass() {
        return clientPlayerClass;
    }
}
