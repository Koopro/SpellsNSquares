package at.koopro.spells_n_squares.core.events;

import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Events related to spell casting.
 * Modules can subscribe to these events to react to spell-related actions.
 */
public class SpellEvents {
    /**
     * Fired when a spell is about to be cast.
     * Can be cancelled to prevent casting.
     */
    public static class SpellCastEvent extends Event implements ICancellableEvent {
        private final Player player;
        private final Spell spell;
        private final Identifier spellId;
        private final Level level;
        private final int slot;
        private boolean cancelled = false;
        
        public SpellCastEvent(Player player, Spell spell, Level level, int slot) {
            this.player = player;
            this.spell = spell;
            this.spellId = spell != null ? spell.getId() : null;
            this.level = level;
            this.slot = slot;
        }
        
        public Player getPlayer() { 
            return player; 
        }
        
        public Spell getSpell() {
            return spell;
        }
        
        public Identifier getSpellId() { 
            return spellId; 
        }
        
        public Level getLevel() {
            return level;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public boolean isCancelled() { 
            return cancelled; 
        }
        
        public void setCancelled(boolean cancelled) { 
            this.cancelled = cancelled; 
        }
    }
    
    /**
     * Fired after a spell is successfully cast.
     * This event cannot be cancelled.
     */
    public static class SpellCastPostEvent extends Event {
        private final Player player;
        private final Spell spell;
        private final Identifier spellId;
        private final Level level;
        private final int slot;
        
        public SpellCastPostEvent(Player player, Spell spell, Level level, int slot) {
            this.player = player;
            this.spell = spell;
            this.spellId = spell != null ? spell.getId() : null;
            this.level = level;
            this.slot = slot;
        }
        
        public Player getPlayer() { 
            return player; 
        }
        
        public Spell getSpell() {
            return spell;
        }
        
        public Identifier getSpellId() { 
            return spellId; 
        }
        
        public Level getLevel() {
            return level;
        }
        
        public int getSlot() {
            return slot;
        }
    }
}

