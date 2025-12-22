package at.koopro.spells_n_squares.core.api.addon.events;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Event fired when a player's class changes.
 * Addons can subscribe to this event to react to player class changes.
 */
public class PlayerClassChangeEvent extends Event {
    private final Player player;
    private final PlayerClass oldClass;
    private final PlayerClass newClass;
    
    public PlayerClassChangeEvent(Player player, PlayerClass oldClass, PlayerClass newClass) {
        this.player = player;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }
    
    /**
     * Gets the player whose class changed.
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the player's previous class.
     * @return The old class, or NONE if the player had no class
     */
    public PlayerClass getOldClass() {
        return oldClass;
    }
    
    /**
     * Gets the player's new class.
     * @return The new class, or NONE if the class is being cleared
     */
    public PlayerClass getNewClass() {
        return newClass;
    }
}












