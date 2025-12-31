package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.SpellSlotChangeEvent;
import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.events.ModuleEventBus;
import at.koopro.spells_n_squares.modules.spell.internal.SpellData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * Manages spell slot assignments for players.
 * Handles setting, getting, and validating spell slots.
 */
public final class SpellSlotManager {
    private SpellSlotManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates if a slot index is valid.
     * @param slot The slot index
     * @return true if valid (0-3)
     */
    public static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < SpellManager.MAX_SLOTS;
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
        
        // Get current spell data
        SpellData current = PlayerDataHelper.getSpellData(player);
        Identifier oldSpellId = current.slots().getSpellInSlot(slot).orElse(null);
        
        // Update spell slot
        SpellSlotData.SpellSlotComponent newSlots = current.slots().withSpellInSlot(slot, spellId);
        SpellData updated = current.withSlots(newSlots);
        PlayerDataHelper.setSpellData(player, updated);
        
        // Fire event if spell changed
        if (!Objects.equals(oldSpellId, spellId)) {
            // Fire to AddonEventBus (for addon compatibility)
            SpellSlotChangeEvent addonEvent = new SpellSlotChangeEvent(player, slot, oldSpellId, spellId);
            AddonEventBus.getInstance().post(addonEvent);
            
            // Fire to ModuleEventBus (for inter-module communication)
            at.koopro.spells_n_squares.core.events.SpellSlotEvents.SpellSlotChangeEvent moduleEvent = 
                new at.koopro.spells_n_squares.core.events.SpellSlotEvents.SpellSlotChangeEvent(player, slot, oldSpellId, spellId);
            ModuleEventBus.getInstance().post(moduleEvent);
        }
        
        // Sync to client if this is a server player
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            SpellSyncManager.syncSpellSlotsToClient(serverPlayer);
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
        
        // Get from PlayerDataComponent
        SpellData spellData = PlayerDataHelper.getSpellData(player);
        return spellData.slots().getSpellInSlot(slot).orElse(null);
    }
}


