package at.koopro.spells_n_squares.services.wand.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Service interface for wand-related operations.
 * Provides a clean API for other modules to interact with the wand system.
 */
public interface IWandService {
    /**
     * Checks if an item stack is a wand.
     * @param stack The item stack to check
     * @return true if the stack is a wand
     */
    boolean isWand(ItemStack stack);
    
    /**
     * Checks if a wand is attuned.
     * @param wand The wand item stack
     * @return true if the wand is attuned
     */
    boolean isAttuned(ItemStack wand);
    
    /**
     * Attunes a wand for a player.
     * @param player The player
     * @param wand The wand to attune
     * @param level The level
     * @return true if attunement was successful
     */
    boolean attuneWand(Player player, ItemStack wand, Level level);
    
    /**
     * Starts an attunement ritual for a player.
     * @param player The player
     * @param level The level
     * @return true if the ritual can start
     */
    boolean startAttunement(Player player, Level level);
    
    /**
     * Checks if a player is currently performing an attunement ritual.
     * @param player The player
     * @return true if the player is attuning
     */
    boolean isAttuning(Player player);
}






