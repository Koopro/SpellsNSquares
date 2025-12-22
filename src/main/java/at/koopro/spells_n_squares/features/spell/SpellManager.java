package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.SpellCastEvent;
import at.koopro.spells_n_squares.core.api.addon.events.SpellSlotChangeEvent;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.artifacts.ElderWandItem;
import at.koopro.spells_n_squares.features.spell.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotsSyncPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

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
    
    // Per-player active hold-to-cast spells (player UUID -> spell ID)
    private static final Map<UUID, Identifier> activeHoldSpells = new HashMap<>();
    
    // Cache for spell lookups (spell ID -> Spell)
    private static final Map<Identifier, Spell> spellCache = new HashMap<>();
    
    // Per-player sync timers for batched cooldown synchronization (UUID -> tick count)
    private static final Map<UUID, Integer> syncTimers = new HashMap<>();
    private static final int SYNC_INTERVAL = 20; // Sync every 20 ticks (1 second)
    
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
        
        // Get spell from registry (with caching)
        Spell spell = spellCache.get(spellId);
        if (spell == null) {
            spell = SpellRegistry.get(spellId);
            if (spell != null) {
                spellCache.put(spellId, spell);
            } else {
                return false;
            }
        }
        
        // Cast the spell
        boolean success = spell.cast(player, level);
        
        // Suppress ALL visual effects for hold-to-cast spells to avoid annoying animations
        // Hold spells should only have subtle effects during onHoldTick, not on cast
        boolean isHoldSpell = spell.isHoldToCast();
        
        if (success) {
            // Only spawn visual effects for non-hold spells
            if (!isHoldSpell) {
                // Spawn visual effects (screen flash, particles, etc.)
                spell.spawnCastEffects(player, level, true);
                
                // Fire event (this also triggers SoundVisualSync for additional particles)
                SpellCastEvent event = new SpellCastEvent(player, spell, level, slot);
                AddonEventBus.getInstance().post(event);
            }
            
            // Set cooldown (apply Elder Wand reduction if applicable)
            int baseCooldown = spell.getCooldown();
            float cooldownReduction = ElderWandItem.getCooldownReduction(player);
            int adjustedCooldown = (int) (baseCooldown * cooldownReduction);
            setCooldown(player, spellId, adjustedCooldown);
            
            // Sync cooldowns to client (immediate sync for spell cast)
            if (player instanceof ServerPlayer serverPlayer) {
                syncCooldownsToClient(serverPlayer);
                syncTimers.put(serverPlayer.getUUID(), 0); // Reset sync timer
            }
        } else {
            // Spawn failure effects (subtle feedback) - but not for hold spells
            if (!isHoldSpell) {
                spell.spawnCastEffects(player, level, false);
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
        
        // Batch sync to client (only sync every SYNC_INTERVAL ticks)
        if (changed && player instanceof ServerPlayer serverPlayer) {
            int timer = syncTimers.getOrDefault(uuid, SYNC_INTERVAL);
            timer++;
            if (timer >= SYNC_INTERVAL) {
                syncCooldownsToClient(serverPlayer);
                syncTimers.put(uuid, 0);
            } else {
                syncTimers.put(uuid, timer);
            }
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
        activeHoldSpells.remove(uuid);
    }
    
    /**
     * Starts a hold-to-cast spell for a player.
     * @param player The player
     * @param spellId The spell ID
     */
    public static void startHoldSpell(Player player, Identifier spellId) {
        UUID uuid = player.getUUID();
        activeHoldSpells.put(uuid, spellId);
    }
    
    /**
     * Stops a hold-to-cast spell for a player.
     * @param player The player
     */
    public static void stopHoldSpell(Player player) {
        UUID uuid = player.getUUID();
        Identifier spellId = activeHoldSpells.remove(uuid);
        
        // Clean up spell-specific tracking (e.g., Wingardium Leviosa tracked entities)
        if (spellId != null) {
            Spell spell = SpellRegistry.get(spellId);
            if (spell instanceof at.koopro.spells_n_squares.features.spell.WingardiumLeviosaSpell) {
                at.koopro.spells_n_squares.features.spell.WingardiumLeviosaSpell.cleanupForPlayer(uuid);
            }
        }
    }
    
    /**
     * Gets the active hold-to-cast spell for a player.
     * @param player The player
     * @return The spell ID, or null if no hold spell is active
     */
    public static Identifier getActiveHoldSpell(Player player) {
        return activeHoldSpells.get(player.getUUID());
    }
    
    /**
     * Ticks all active hold-to-cast spells.
     * Called every server tick.
     */
    public static void tickHoldSpells(Level level) {
        if (level.isClientSide()) {
            return;
        }
        
        Iterator<Map.Entry<UUID, Identifier>> iterator = activeHoldSpells.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Identifier> entry = iterator.next();
            UUID playerUUID = entry.getKey();
            Identifier spellId = entry.getValue();
            
            Player player = level.getPlayerByUUID(playerUUID);
            if (player == null || !player.isAlive()) {
                iterator.remove();
                continue;
            }
            
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null || !spell.isHoldToCast()) {
                iterator.remove();
                continue;
            }
            
            // Call the spell's hold tick method
            boolean shouldContinue = spell.onHoldTick(player, level);
            if (!shouldContinue) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Cleans up tracked entities when a hold spell stops.
     * Called when stopping a hold spell.
     */
    public static void cleanupHoldSpell(Player player) {
        // This is handled by individual spells that track entities
        // Spells like WingardiumLeviosaSpell manage their own tracked entities
    }
    
    /**
     * Syncs spell slots to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncSpellSlotsToClient(ServerPlayer serverPlayer) {
        UUID uuid = serverPlayer.getUUID();
        Identifier[] slots = playerSpellSlots.get(uuid);
        if (slots == null) {
            slots = new Identifier[MAX_SLOTS];
        }
        
        // Convert array to list for payload
        List<Identifier> slotList = new ArrayList<>(MAX_SLOTS);
        for (int i = 0; i < MAX_SLOTS; i++) {
            slotList.add(slots[i]);
        }
        
        SpellSlotsSyncPayload payload = new SpellSlotsSyncPayload(slotList);
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }
    
    /**
     * Syncs cooldowns to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncCooldownsToClient(ServerPlayer serverPlayer) {
        UUID uuid = serverPlayer.getUUID();
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(uuid);
        if (cooldowns == null) {
            cooldowns = new HashMap<>();
        }
        
        // Create a copy to avoid concurrent modification issues
        Map<Identifier, Integer> cooldownsCopy = new HashMap<>(cooldowns);
        SpellCooldownSyncPayload payload = new SpellCooldownSyncPayload(cooldownsCopy);
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }
}
