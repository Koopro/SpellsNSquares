package at.koopro.spells_n_squares.core.api.addon.events;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Event fired when a spell slot assignment changes.
 * Addons can subscribe to this event to react to spell slot changes.
 */
public class SpellSlotChangeEvent extends Event {
    private final Player player;
    private final int slot;
    private final Identifier oldSpellId;
    private final Identifier newSpellId;
    
    public SpellSlotChangeEvent(Player player, int slot, Identifier oldSpellId, Identifier newSpellId) {
        this.player = player;
        this.slot = slot;
        this.oldSpellId = oldSpellId;
        this.newSpellId = newSpellId;
    }
    
    /**
     * Gets the player whose spell slot changed.
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the slot index that changed.
     * @return The slot index (0-3)
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Gets the spell ID that was previously in the slot.
     * @return The old spell ID, or null if the slot was empty
     */
    public Identifier getOldSpellId() {
        return oldSpellId;
    }
    
    /**
     * Gets the spell ID that is now in the slot.
     * @return The new spell ID, or null if the slot is being cleared
     */
    public Identifier getNewSpellId() {
        return newSpellId;
    }
}












