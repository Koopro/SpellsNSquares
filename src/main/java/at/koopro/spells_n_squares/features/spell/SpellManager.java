package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.SpellCastEvent;
import at.koopro.spells_n_squares.core.api.addon.events.SpellSlotChangeEvent;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.Set;

/**
 * Manages spell slots and cooldowns for all players.
 * Handles storage, retrieval, casting, and syncing of spell data.
 */
public class SpellManager {
    // Slot constants
    public static final int SLOT_TOP = 0;
    public static final int SLOT_BOTTOM = 1;
    public static final int SLOT_LEFT = 2;
    public static final int SLOT_RIGHT = 3;
    public static final int MAX_SLOTS = 4;
    public static final int[] SLOTS = {SLOT_TOP, SLOT_BOTTOM, SLOT_LEFT, SLOT_RIGHT};
    
    // Per-player spell slot assignments (slot index -> spell ID)
    private static final Map<UUID, Identifier[]> playerSpellSlots = new HashMap<>();
    
    // Per-player spell cooldowns (spell ID -> remaining ticks)
    private static final Map<UUID, Map<Identifier, Integer>> playerCooldowns = new HashMap<>();
    
    // Per-player learned spells (set of spell IDs)
    private static final Map<UUID, Set<Identifier>> playerLearnedSpells = new HashMap<>();
    
    /**
     * Validates if a slot index is valid.
     * @param slot The slot index
     * @return true if valid (0-3)
     */
    public static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < MAX_SLOTS;
    }
    
    /**
     * Sets a spell in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @param spellId The spell ID to assign, or null to clear
     */
    public static void setSpellInSlot(Player player, int slot, Identifier spellId) {
        if (!isValidSlot(slot)) {
            return;
        }
        
        UUID uuid = player.getUUID();
        Identifier[] slots = playerSpellSlots.computeIfAbsent(uuid, k -> new Identifier[MAX_SLOTS]);
        
        Identifier oldSpellId = slots[slot];
        slots[slot] = spellId;
        
        // Fire event if spell changed
        if (!Objects.equals(oldSpellId, spellId)) {
            SpellSlotChangeEvent event = new SpellSlotChangeEvent(player, slot, oldSpellId, spellId);
            AddonEventBus.getInstance().post(event);
        }
        
        // Sync to client if this is a server player
        if (player instanceof ServerPlayer serverPlayer) {
            syncSpellSlotsToClient(serverPlayer);
        }
    }
    
    /**
     * Gets the spell in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @return The spell ID, or null if no spell assigned
     */
    public static Identifier getSpellInSlot(Player player, int slot) {
        if (!isValidSlot(slot)) {
            return null;
        }
        
        UUID uuid = player.getUUID();
        Identifier[] slots = playerSpellSlots.get(uuid);
        if (slots == null) {
            return null;
        }
        
        return slots[slot];
    }
    
    /**
     * Casts the spell in the specified slot for a player.
     * @param player The player casting
     * @param level The level/world
     * @param slot The slot index (0-3)
     * @return true if the spell was successfully cast
     */
    public static boolean castSpellInSlot(Player player, Level level, int slot) {
        if (!isValidSlot(slot)) {
            return false;
        }
        
        Identifier spellId = getSpellInSlot(player, slot);
        if (spellId == null) {
            return false;
        }
        
        // Check cooldown
        if (isOnCooldown(player, spellId)) {
            return false;
        }
        
        // Get spell from registry
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            return false;
        }
        
        // Cast the spell
        boolean success = spell.cast(player, level);
        
        if (success) {
            // Set cooldown
            setCooldown(player, spellId, spell.getCooldown());
            
            // Fire event
            SpellCastEvent event = new SpellCastEvent(player, spell, level, slot);
            AddonEventBus.getInstance().post(event);
            
            // Sync cooldowns to client
            if (player instanceof ServerPlayer serverPlayer) {
                syncCooldownsToClient(serverPlayer);
            }
        }
        
        return success;
    }
    
    /**
     * Sets a cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @param ticks The cooldown duration in ticks
     */
    public static void setCooldown(Player player, Identifier spellId, int ticks) {
        if (spellId == null || ticks <= 0) {
            return;
        }
        
        UUID uuid = player.getUUID();
        Map<Identifier, Integer> cooldowns = playerCooldowns.computeIfAbsent(uuid, k -> new HashMap<>());
        cooldowns.put(spellId, ticks);
    }
    
    /**
     * Checks if a spell is on cooldown for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return true if on cooldown
     */
    public static boolean isOnCooldown(Player player, Identifier spellId) {
        if (spellId == null) {
            return false;
        }
        
        UUID uuid = player.getUUID();
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(uuid);
        if (cooldowns == null) {
            return false;
        }
        
        Integer remaining = cooldowns.get(spellId);
        return remaining != null && remaining > 0;
    }
    
    /**
     * Gets the remaining cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return Remaining cooldown in ticks, or 0 if not on cooldown
     */
    public static int getRemainingCooldown(Player player, Identifier spellId) {
        if (spellId == null) {
            return 0;
        }
        
        UUID uuid = player.getUUID();
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(uuid);
        if (cooldowns == null) {
            return 0;
        }
        
        Integer remaining = cooldowns.get(spellId);
        return remaining != null ? Math.max(0, remaining) : 0;
    }
    
    /**
     * Ticks all cooldowns for a player. Call this every tick.
     * @param player The player
     */
    public static void tickCooldowns(Player player) {
        UUID uuid = player.getUUID();
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(uuid);
        if (cooldowns == null || cooldowns.isEmpty()) {
            return;
        }
        
        // Decrement all cooldowns and remove expired ones
        Iterator<Map.Entry<Identifier, Integer>> iterator = cooldowns.entrySet().iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            Map.Entry<Identifier, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                changed = true;
            } else {
                entry.setValue(remaining);
                changed = true;
            }
        }
        
        // Sync to client if cooldowns changed
        if (changed && player instanceof ServerPlayer serverPlayer) {
            syncCooldownsToClient(serverPlayer);
        }
    }
    
    /**
     * Checks if a player has learned a specific spell.
     * @param player The player
     * @param spellId The spell ID to check
     * @return true if the player has learned the spell
     */
    public static boolean hasLearnedSpell(Player player, Identifier spellId) {
        if (spellId == null) {
            return false;
        }
        
        UUID uuid = player.getUUID();
        Set<Identifier> learnedSpells = playerLearnedSpells.get(uuid);
        return learnedSpells != null && learnedSpells.contains(spellId);
    }
    
    /**
     * Learns a spell for a player.
     * @param player The player
     * @param spellId The spell ID to learn
     * @return true if the spell was newly learned, false if already known
     */
    public static boolean learnSpell(Player player, Identifier spellId) {
        if (spellId == null) {
            return false;
        }
        
        // Verify spell exists in registry
        if (!SpellRegistry.isRegistered(spellId)) {
            return false;
        }
        
        UUID uuid = player.getUUID();
        Set<Identifier> learnedSpells = playerLearnedSpells.computeIfAbsent(uuid, k -> new HashSet<>());
        
        if (learnedSpells.contains(spellId)) {
            return false; // Already learned
        }
        
        learnedSpells.add(spellId);
        return true; // Newly learned
    }
    
    /**
     * Clears all spell data for a player.
     * Called when a player disconnects.
     * @param player The player
     */
    public static void clearPlayerData(Player player) {
        UUID uuid = player.getUUID();
        playerSpellSlots.remove(uuid);
        playerCooldowns.remove(uuid);
        playerLearnedSpells.remove(uuid);
    }
    
    /**
     * Syncs spell slots to the client for a server player.
     * TODO: Implement network sync when network payloads are enabled
     * @param serverPlayer The server player
     */
    public static void syncSpellSlotsToClient(ServerPlayer serverPlayer) {
        // TODO: Send SpellSlotsSyncPayload to client
        // Currently commented out in ModNetwork.java until network payloads are implemented
    }
    
    /**
     * Syncs cooldowns to the client for a server player.
     * TODO: Implement network sync when network payloads are enabled
     * @param serverPlayer The server player
     */
    public static void syncCooldownsToClient(ServerPlayer serverPlayer) {
        // TODO: Send SpellCooldownSyncPayload to client
        // Currently commented out in ModNetwork.java until network payloads are implemented
    }
}
