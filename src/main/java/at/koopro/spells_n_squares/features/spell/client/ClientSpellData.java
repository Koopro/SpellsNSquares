package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side spell data storage.
 * Stores spell slots and cooldowns synced from the server.
 */
public class ClientSpellData {
    // Client-side spell slot assignments (slot index -> spell ID)
    private static final Identifier[] clientSpellSlots = new Identifier[SpellManager.MAX_SLOTS];
    
    // Client-side spell cooldowns (spell ID -> remaining ticks)
    private static final Map<Identifier, Integer> clientCooldowns = new HashMap<>();
    
    // Client-side player class
    private static PlayerClass clientPlayerClass = PlayerClass.NONE;
    
    // Currently selected spell slot (for casting)
    private static int selectedSlot = SpellManager.SLOT_TOP;
    
    // Whether player is currently holding a spell
    private static boolean holdingSpell = false;
    
    /**
     * Gets the spell ID in the specified slot.
     * @param slot The slot index (0-3)
     * @return The spell ID, or null if no spell is assigned
     */
    public static Identifier getSpellInSlot(int slot) {
        if (!SpellManager.isValidSlot(slot)) {
            return null;
        }
        return clientSpellSlots[slot];
    }
    
    /**
     * Sets the spell ID in the specified slot.
     * @param slot The slot index (0-3)
     * @param spellId The spell ID, or null to clear
     */
    public static void setSpellInSlot(int slot, Identifier spellId) {
        if (!SpellManager.isValidSlot(slot)) {
            return;
        }
        clientSpellSlots[slot] = spellId;
    }
    
    /**
     * Updates all spell slots from server data.
     * @param slots Array of spell IDs (length must be MAX_SLOTS)
     */
    public static void updateSpellSlots(Identifier[] slots) {
        if (slots == null || slots.length != SpellManager.MAX_SLOTS) {
            return;
        }
        System.arraycopy(slots, 0, clientSpellSlots, 0, SpellManager.MAX_SLOTS);
    }
    
    /**
     * Checks if a spell is on cooldown.
     * @param spellId The spell ID
     * @return true if the spell is on cooldown
     */
    public static boolean isOnCooldown(Identifier spellId) {
        if (spellId == null) {
            return false;
        }
        Integer cooldown = clientCooldowns.get(spellId);
        return cooldown != null && cooldown > 0;
    }
    
    /**
     * Gets the remaining cooldown for a spell.
     * @param spellId The spell ID
     * @return The remaining cooldown in ticks, or 0 if not on cooldown
     */
    public static int getCooldown(Identifier spellId) {
        if (spellId == null) {
            return 0;
        }
        return clientCooldowns.getOrDefault(spellId, 0);
    }
    
    /**
     * Sets the cooldown for a spell.
     * @param spellId The spell ID
     * @param ticks The cooldown duration in ticks
     */
    public static void setCooldown(Identifier spellId, int ticks) {
        if (spellId == null) {
            return;
        }
        if (ticks > 0) {
            clientCooldowns.put(spellId, ticks);
        } else {
            clientCooldowns.remove(spellId);
        }
    }
    
    /**
     * Updates all cooldowns from server data.
     * @param cooldowns Map of spell IDs to cooldown ticks
     */
    public static void updateCooldowns(Map<Identifier, Integer> cooldowns) {
        if (cooldowns == null) {
            return;
        }
        clientCooldowns.clear();
        clientCooldowns.putAll(cooldowns);
    }
    
    /**
     * Ticks all cooldowns, decrementing them by 1.
     * Called every client tick.
     */
    public static void tickCooldowns() {
        clientCooldowns.entrySet().removeIf(entry -> {
            int newValue = entry.getValue() - 1;
            if (newValue <= 0) {
                return true; // Remove expired cooldowns
            }
            entry.setValue(newValue);
            return false;
        });
    }
    
    /**
     * Sets the player class for the client.
     * @param playerClass The player class
     */
    public static void setPlayerClass(PlayerClass playerClass) {
        clientPlayerClass = playerClass != null ? playerClass : PlayerClass.NONE;
    }
    
    /**
     * Gets the player class for the client.
     * @return The player class
     */
    public static PlayerClass getPlayerClass() {
        return clientPlayerClass;
    }
    
    /**
     * Gets the currently selected spell slot.
     * @return The selected slot index
     */
    public static int getSelectedSlot() {
        return selectedSlot;
    }
    
    /**
     * Sets the currently selected spell slot.
     * @param slot The slot index to select
     */
    public static void setSelectedSlot(int slot) {
        if (SpellManager.isValidSlot(slot)) {
            selectedSlot = slot;
        }
    }
    
    /**
     * Checks if the player is currently holding a spell.
     * @return true if holding a spell
     */
    public static boolean isHoldingSpell() {
        return holdingSpell;
    }
    
    /**
     * Sets whether the player is holding a spell.
     * @param holding true if holding, false otherwise
     */
    public static void setHoldingSpell(boolean holding) {
        holdingSpell = holding;
    }
    
    /**
     * Clears all client spell data.
     */
    public static void clear() {
        for (int i = 0; i < clientSpellSlots.length; i++) {
            clientSpellSlots[i] = null;
        }
        clientCooldowns.clear();
        clientPlayerClass = PlayerClass.NONE;
        selectedSlot = SpellManager.SLOT_TOP;
    }
}
