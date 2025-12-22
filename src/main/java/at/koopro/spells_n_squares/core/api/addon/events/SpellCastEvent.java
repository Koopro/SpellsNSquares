package at.koopro.spells_n_squares.core.api.addon.events;

import at.koopro.spells_n_squares.features.spell.Spell;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Event fired when a spell is cast.
 * Addons can subscribe to this event to react to spell casts or cancel them.
 */
public class SpellCastEvent extends Event implements ICancellableEvent {
    private final Player player;
    private final Spell spell;
    private final Level level;
    private final int slot;
    private boolean canceled = false;
    
    public SpellCastEvent(Player player, Spell spell, Level level, int slot) {
        this.player = player;
        this.spell = spell;
        this.level = level;
        this.slot = slot;
    }
    
    /**
     * Gets the player casting the spell.
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the spell being cast.
     * @return The spell
     */
    public Spell getSpell() {
        return spell;
    }
    
    /**
     * Gets the level/world where the spell is being cast.
     * @return The level
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Gets the slot index from which the spell is being cast.
     * @return The slot index (0-3)
     */
    public int getSlot() {
        return slot;
    }
    
    public boolean isCanceled() {
        return canceled;
    }
    
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}












