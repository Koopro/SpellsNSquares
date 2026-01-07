package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.spell.client.RecentSpellsManager;
import at.koopro.spells_n_squares.features.spell.client.SpellUsageStatistics;
import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Client-side spell data storage.
 * Stores spell slots and cooldowns synced from the server.
 */
public class ClientSpellData {
    // Client-side spell slot assignments (slot index -> spell ID)
    private static final Identifier[] clientSpellSlots = new Identifier[SpellManager.MAX_SLOTS];
    
    // Client-side spell cooldowns (spell ID -> remaining ticks)
    private static final Map<Identifier, Integer> clientCooldowns = CollectionFactory.createMap();
    
    // Currently selected spell slot (for casting)
    private static int selectedSlot = SpellManager.SLOT_TOP;
    
    // Whether player is currently holding a spell
    private static boolean holdingSpell = false;
    
    // Favorite spells (spell IDs that are favorited)
    private static final java.util.Set<Identifier> favoriteSpells = CollectionFactory.createSet();
    private static boolean favoritesLoaded = false;
    
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
        DevLogger.logMethodEntry(ClientSpellData.class, "updateSpellSlots");
        if (slots == null || slots.length != SpellManager.MAX_SLOTS) {
            DevLogger.logWarn(ClientSpellData.class, "updateSpellSlots", 
                "Invalid slots array: " + (slots == null ? "null" : "length=" + slots.length));
            DevLogger.logMethodExit(ClientSpellData.class, "updateSpellSlots");
            return;
        }
        System.arraycopy(slots, 0, clientSpellSlots, 0, SpellManager.MAX_SLOTS);
        DevLogger.logStateChange(ClientSpellData.class, "updateSpellSlots", 
            "Updated spell slots from server");
        DevLogger.logMethodExit(ClientSpellData.class, "updateSpellSlots");
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
        
        // Track newly added cooldowns as recently used spells
        for (Map.Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
            Identifier spellId = entry.getKey();
            // Skip entries with null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            Integer newCooldown = entry.getValue();
            Integer oldCooldown = clientCooldowns.get(spellId);
            
            // If this is a new cooldown (wasn't on cooldown before), record as recently used
            if (oldCooldown == null || oldCooldown <= 0) {
                if (newCooldown != null && newCooldown > 0) {
                    RecentSpellsManager.recordSpellUsed(spellId);
                    SpellUsageStatistics.recordSpellUsed(spellId);
                }
            }
        }
        
        clientCooldowns.clear();
        // Filter out null keys before putAll to prevent NullPointerException
        for (Map.Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
            Identifier spellId = entry.getKey();
            if (spellId != null) {
                clientCooldowns.put(spellId, entry.getValue());
            }
        }
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
     * Gets the currently selected spell slot.
     * @return The selected slot index
     */
    public static int getSelectedSlot() {
        DevLogger.logMethodEntry(ClientSpellData.class, "getSelectedSlot");
        int result = selectedSlot;
        DevLogger.logReturnValue(ClientSpellData.class, "getSelectedSlot", result);
        return result;
    }
    
    /**
     * Sets the currently selected spell slot.
     * @param slot The slot index to select
     */
    public static void setSelectedSlot(int slot) {
        DevLogger.logMethodEntry(ClientSpellData.class, "setSelectedSlot", "slot=" + slot);
        if (SpellManager.isValidSlot(slot)) {
            selectedSlot = slot;
            DevLogger.logStateChange(ClientSpellData.class, "setSelectedSlot", 
                "Selected slot changed to " + slot);
        } else {
            DevLogger.logWarn(ClientSpellData.class, "setSelectedSlot", "Invalid slot: " + slot);
        }
        DevLogger.logMethodExit(ClientSpellData.class, "setSelectedSlot");
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
     * Ensures favorites are loaded from disk.
     */
    private static void ensureFavoritesLoaded() {
        if (!favoritesLoaded) {
            java.util.Set<Identifier> loaded = FavoritesPersistence.loadFavorites();
            favoriteSpells.clear();
            favoriteSpells.addAll(loaded);
            favoritesLoaded = true;
        }
    }
    
    /**
     * Checks if a spell is favorited.
     * @param spellId The spell ID
     * @return true if the spell is favorited
     */
    public static boolean isFavorite(Identifier spellId) {
        ensureFavoritesLoaded();
        return spellId != null && favoriteSpells.contains(spellId);
    }
    
    /**
     * Toggles the favorite status of a spell.
     * @param spellId The spell ID
     * @return true if the spell is now favorited, false if it was unfavorited
     */
    public static boolean toggleFavorite(Identifier spellId) {
        ensureFavoritesLoaded();
        if (spellId == null) {
            return false;
        }
        boolean wasFavorite = favoriteSpells.contains(spellId);
        if (wasFavorite) {
            favoriteSpells.remove(spellId);
        } else {
            favoriteSpells.add(spellId);
        }
        // Save to disk
        FavoritesPersistence.saveFavorites(favoriteSpells);
        return !wasFavorite;
    }
    
    /**
     * Gets all favorite spell IDs.
     * @return A set of favorite spell IDs
     */
    public static java.util.Set<Identifier> getFavorites() {
        ensureFavoritesLoaded();
        return CollectionFactory.createSetFrom(favoriteSpells);
    }
    
    /**
     * Clears all client spell data.
     */
    public static void clear() {
        for (int i = 0; i < clientSpellSlots.length; i++) {
            clientSpellSlots[i] = null;
        }
        clientCooldowns.clear();
        favoriteSpells.clear();
        favoritesLoaded = false; // Reset so favorites reload on next access
        selectedSlot = SpellManager.SLOT_TOP;
    }
    
    /**
     * Initializes favorites by loading from disk.
     * Should be called when the client starts or when the player logs in.
     */
    public static void initializeFavorites() {
        ensureFavoritesLoaded();
    }
}
