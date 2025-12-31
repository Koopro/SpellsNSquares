package at.koopro.spells_n_squares.core.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for menu providers that use a BlockPos as context.
 * Provides common patterns for BlockPos-based menus (e.g., block entity menus).
 * 
 * <p>Subclasses should implement:
 * <ul>
 *   <li>{@link #getDisplayNameInternal(BlockPos)} - Returns the display name based on the position</li>
 *   <li>{@link #createMenuInternal(int, Inventory, Player, BlockPos)} - Creates the menu with position context</li>
 * </ul>
 */
public abstract class BlockPosMenuProvider extends BaseMenuProvider {
    protected final BlockPos pos;
    
    /**
     * Creates a menu provider with a BlockPos context.
     * 
     * @param pos The block position (must not be null)
     */
    protected BlockPosMenuProvider(BlockPos pos) {
        if (pos == null) {
            throw new IllegalArgumentException("BlockPos cannot be null");
        }
        this.pos = pos;
    }
    
    /**
     * Gets the BlockPos context for this menu provider.
     * 
     * @return The block position
     */
    protected BlockPos getPos() {
        return pos;
    }
    
    /**
     * Gets the display name for this menu based on the BlockPos.
     * Subclasses should implement this to return the appropriate translatable component.
     * 
     * @param pos The block position
     * @return The display name component
     */
    protected abstract Component getDisplayNameInternal(BlockPos pos);
    
    /**
     * Creates the menu instance with BlockPos context.
     * Subclasses should implement this to create their specific menu type.
     * 
     * @param containerId The container ID
     * @param playerInventory The player inventory
     * @param player The player
     * @param pos The block position context
     * @return The created menu, or null if creation fails
     */
    @Nullable
    protected abstract AbstractContainerMenu createMenuInternal(int containerId, 
                                                                 Inventory playerInventory, 
                                                                 Player player, 
                                                                 BlockPos pos);
    
    @Override
    protected final Component getDisplayNameInternal() {
        return getDisplayNameInternal(pos);
    }
    
    @Nullable
    @Override
    protected final AbstractContainerMenu createMenuInternal(int containerId, 
                                                               Inventory playerInventory, 
                                                               Player player) {
        return createMenuInternal(containerId, playerInventory, player, pos);
    }
}


