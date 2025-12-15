package at.koopro.spells_n_squares.features.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import at.koopro.spells_n_squares.core.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.core.network.SpellSlotsSyncPayload;

/**
 * Manages spell slots and casting for players.
 * Handles the 4 spell slots (top, bottom, left, right) and their assignments.
 */
public class SpellManager {
    // Spell slot indices matching SpellSelectorHUD
    public static final int SLOT_TOP = 0;
    public static final int SLOT_BOTTOM = 1;
    public static final int SLOT_LEFT = 2;
    public static final int SLOT_RIGHT = 3;
    
    // Per-player spell slot assignments
    private static final Map<Player, Identifier[]> playerSpellSlots = new HashMap<>();
    
    // Per-player cooldown tracking (spell ID -> ticks remaining)
    private static final Map<Player, Map<Identifier, Integer>> playerCooldowns = new HashMap<>();
    
    /**
     * Sets a spell in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @param spellId The spell ID to assign, or null to clear
     */
    public static void setSpellInSlot(Player player, int slot, Identifier spellId) {
        if (slot < 0 || slot > 3) {
            throw new IllegalArgumentException("Slot must be between 0 and 3");
        }
        
        playerSpellSlots.computeIfAbsent(player, p -> new Identifier[4])[slot] = spellId;
        
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
        if (slot < 0 || slot > 3) {
            return null;
        }
        
        Identifier[] slots = playerSpellSlots.get(player);
        if (slots == null) {
            return null;
        }
        
        return slots[slot];
    }
    
    /**
     * Gets the Spell object in a specific slot for a player.
     * @param player The player
     * @param slot The slot index (0-3)
     * @return The Spell, or null if no spell assigned
     */
    public static Spell getSpellObjectInSlot(Player player, int slot) {
        Identifier spellId = getSpellInSlot(player, slot);
        if (spellId == null) {
            return null;
        }
        return SpellRegistry.get(spellId);
    }
    
    /**
     * Casts the spell in the specified slot for a player.
     * @param player The player casting
     * @param level The level/world
     * @param slot The slot index (0-3)
     * @return true if the spell was successfully cast
     */
    public static boolean castSpellInSlot(Player player, Level level, int slot) {
        // Only cast spells on the server side
        if (level.isClientSide()) {
            return false;
        }
        
        Spell spell = getSpellObjectInSlot(player, slot);
        if (spell == null) {
            return false;
        }
        
        // Check cooldown
        if (isOnCooldown(player, spell.getId())) {
            return false;
        }
        
        // Check if spell can be cast
        if (!spell.canCast(player, level)) {
            return false;
        }
        
        // Cast the spell
        boolean success = spell.cast(player, level);
        
        // Set cooldown if successful
        if (success && spell.getCooldown() > 0) {
            setCooldown(player, spell.getId(), spell.getCooldown());
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
        playerCooldowns.computeIfAbsent(player, p -> new HashMap<>()).put(spellId, ticks);
        
        // Sync cooldowns to client if this is a server player
        if (player instanceof ServerPlayer serverPlayer) {
            syncCooldownsToClient(serverPlayer);
        }
    }
    
    /**
     * Checks if a spell is on cooldown for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return true if on cooldown
     */
    public static boolean isOnCooldown(Player player, Identifier spellId) {
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(player);
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
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(player);
        if (cooldowns == null) {
            return 0;
        }
        
        Integer remaining = cooldowns.get(spellId);
        return remaining != null ? remaining : 0;
    }
    
    /**
     * Ticks all cooldowns for a player. Call this every tick.
     * @param player The player
     */
    public static void tickCooldowns(Player player) {
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(player);
        if (cooldowns == null) {
            return;
        }
        
        cooldowns.entrySet().removeIf(entry -> {
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                return true; // Remove expired cooldowns
            }
            entry.setValue(remaining);
            return false;
        });
    }
    
    /**
     * Clears all spell data for a player (used when player disconnects).
     * @param player The player
     */
    public static void clearPlayerData(Player player) {
        playerSpellSlots.remove(player);
        playerCooldowns.remove(player);
    }
    
    /**
     * Syncs spell slots to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncSpellSlotsToClient(ServerPlayer serverPlayer) {
        Identifier[] slots = playerSpellSlots.get(serverPlayer);
        if (slots == null) {
            slots = new Identifier[4];
        }
        
        SpellSlotsSyncPayload payload = new SpellSlotsSyncPayload(
            Optional.ofNullable(slots[SLOT_TOP]),
            Optional.ofNullable(slots[SLOT_BOTTOM]),
            Optional.ofNullable(slots[SLOT_LEFT]),
            Optional.ofNullable(slots[SLOT_RIGHT])
        );
        
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }
    
    /**
     * Syncs cooldowns to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncCooldownsToClient(ServerPlayer serverPlayer) {
        Map<Identifier, Integer> cooldowns = playerCooldowns.get(serverPlayer);
        if (cooldowns == null || cooldowns.isEmpty()) {
            return;
        }
        
        List<SpellCooldownSyncPayload.CooldownEntry> entries = new ArrayList<>();
        for (Map.Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new SpellCooldownSyncPayload.CooldownEntry(entry.getKey(), entry.getValue()));
            }
        }
        
        if (!entries.isEmpty()) {
            SpellCooldownSyncPayload payload = new SpellCooldownSyncPayload(entries);
            PacketDistributor.sendToPlayer(serverPlayer, payload);
        }
    }
}
