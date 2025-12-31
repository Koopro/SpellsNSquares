package at.koopro.spells_n_squares.core.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for menu providers that use an ItemStack as context.
 * Provides common patterns for ItemStack-based menus.
 * 
 * <p>Subclasses should implement:
 * <ul>
 *   <li>{@link #getDisplayNameInternal(ItemStack)} - Returns the display name based on the stack</li>
 *   <li>{@link #createMenuInternal(int, Inventory, Player, ItemStack)} - Creates the menu with stack context</li>
 * </ul>
 */
public abstract class ItemStackMenuProvider extends BaseMenuProvider {
    protected final ItemStack stack;
    
    /**
     * Creates a menu provider with an ItemStack context.
     * 
     * @param stack The item stack (must not be null)
     */
    protected ItemStackMenuProvider(ItemStack stack) {
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }
        this.stack = stack;
    }
    
    /**
     * Gets the ItemStack context for this menu provider.
     * 
     * @return The item stack
     */
    protected ItemStack getStack() {
        return stack;
    }
    
    /**
     * Gets the display name for this menu based on the ItemStack.
     * Subclasses should implement this to return the appropriate translatable component.
     * 
     * @param stack The item stack
     * @return The display name component
     */
    protected abstract Component getDisplayNameInternal(ItemStack stack);
    
    /**
     * Creates the menu instance with ItemStack context.
     * Subclasses should implement this to create their specific menu type.
     * 
     * @param containerId The container ID
     * @param playerInventory The player inventory
     * @param player The player
     * @param stack The item stack context
     * @return The created menu, or null if creation fails
     */
    @Nullable
    protected abstract AbstractContainerMenu createMenuInternal(int containerId, 
                                                                  Inventory playerInventory, 
                                                                  Player player, 
                                                                  ItemStack stack);
    
    @Override
    protected final Component getDisplayNameInternal() {
        return getDisplayNameInternal(stack);
    }
    
    @Nullable
    @Override
    protected final AbstractContainerMenu createMenuInternal(int containerId, 
                                                               Inventory playerInventory, 
                                                               Player player) {
        return createMenuInternal(containerId, playerInventory, player, stack);
    }
}


