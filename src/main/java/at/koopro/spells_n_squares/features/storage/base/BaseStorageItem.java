package at.koopro.spells_n_squares.features.storage.base;

import at.koopro.spells_n_squares.core.item.base.BaseServerItem;
import at.koopro.spells_n_squares.core.util.InteractionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for storage items (bags, trunks, pocket dimensions, etc.).
 * Extends BaseServerItem with storage-specific patterns:
 * - Common inventory opening patterns
 * - Storage validation utilities
 * - Storage-specific error messages
 */
public abstract class BaseStorageItem extends BaseServerItem {
    
    public BaseStorageItem(Properties properties) {
        super(properties);
    }
    
    /**
     * Called when a player uses this storage item on the server side.
     * Subclasses should override this to implement storage-specific behavior.
     * 
     * @param level The level (guaranteed to be server-side)
     * @param player The server player (guaranteed to be non-null)
     * @param hand The hand used
     * @param stack The item stack being used
     * @return The interaction result
     */
    @Override
    protected InteractionResult onServerUse(Level level, ServerPlayer player, 
                                             InteractionHand hand, ItemStack stack) {
        // Open storage inventory
        MenuProvider menuProvider = createMenuProvider(stack);
        if (menuProvider != null) {
            openStorageMenu(player, stack, menuProvider);
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.FAIL;
    }
    
    /**
     * Creates a menu provider for this storage item.
     * Subclasses should override this to provide their specific menu.
     * 
     * @param stack The item stack
     * @return The menu provider, or null if no menu should be opened
     */
    protected abstract MenuProvider createMenuProvider(ItemStack stack);
    
    /**
     * Opens the storage menu for the player.
     * Can be overridden by subclasses for custom menu opening behavior.
     * 
     * @param player The server player
     * @param stack The item stack
     * @param menuProvider The menu provider
     */
    protected void openStorageMenu(ServerPlayer player, ItemStack stack, MenuProvider menuProvider) {
        if (player == null || stack == null || menuProvider == null) {
            return;
        }
        
        player.openMenu(menuProvider, (buffer) -> {
            ItemStack.STREAM_CODEC.encode(buffer, stack);
        });
    }
    
    /**
     * Sends a translatable message to the player.
     * Convenience method for storage items.
     * 
     * @param player The server player
     * @param translationKey The translation key
     * @param args Optional arguments for the translation
     */
    protected void sendMessage(ServerPlayer player, String translationKey, Object... args) {
        InteractionUtils.sendTranslatableMessage(player, translationKey, args);
    }
    
    /**
     * Sends a component message to the player.
     * Convenience method for storage items.
     * 
     * @param player The server player
     * @param message The message component
     */
    protected void sendMessage(ServerPlayer player, Component message) {
        InteractionUtils.sendPlayerMessage(player, message);
    }
}


