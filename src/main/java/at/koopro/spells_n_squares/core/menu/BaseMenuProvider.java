package at.koopro.spells_n_squares.core.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for menu providers.
 * Provides template methods for common menu provider patterns.
 * 
 * <p>Subclasses should implement:
 * <ul>
 *   <li>{@link #getDisplayNameInternal()} - Returns the display name for the menu</li>
 *   <li>{@link #createMenuInternal(int, Inventory, Player)} - Creates the menu instance</li>
 * </ul>
 */
public abstract class BaseMenuProvider implements MenuProvider {
    
    /**
     * Gets the display name for this menu.
     * Subclasses should implement this to return the appropriate translatable component.
     * 
     * @return The display name component
     */
    protected abstract Component getDisplayNameInternal();
    
    /**
     * Creates the menu instance.
     * Subclasses should implement this to create their specific menu type.
     * 
     * @param containerId The container ID
     * @param playerInventory The player inventory
     * @param player The player
     * @return The created menu, or null if creation fails
     */
    @Nullable
    protected abstract AbstractContainerMenu createMenuInternal(int containerId, 
                                                                 Inventory playerInventory, 
                                                                 Player player);
    
    @Override
    public final Component getDisplayName() {
        return getDisplayNameInternal();
    }
    
    @Nullable
    @Override
    public final AbstractContainerMenu createMenu(int containerId, 
                                                   Inventory playerInventory, 
                                                   Player player) {
        return createMenuInternal(containerId, playerInventory, player);
    }
}









