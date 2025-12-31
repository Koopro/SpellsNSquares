package at.koopro.spells_n_squares.core.events;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Events related to spell slot changes.
 * Modules can subscribe to these events to react to spell slot assignments.
 */
public class SpellSlotEvents {
    /**
     * Fired when a player's spell slot changes.
     */
    public static class SpellSlotChangeEvent extends Event {
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
        
        public Player getPlayer() {
            return player;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public Identifier getOldSpellId() {
            return oldSpellId;
        }
        
        public Identifier getNewSpellId() {
            return newSpellId;
        }
    }
}


