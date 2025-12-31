package at.koopro.spells_n_squares.core.item.base;

import at.koopro.spells_n_squares.core.util.PlayerValidationUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for items that only work on server-side.
 * Handles common server-side interaction patterns:
 * - Client-side check
 * - ServerPlayer casting
 * - Standardized error handling
 * 
 * Subclasses should override {@link #onServerUse(Level, ServerPlayer, InteractionHand, ItemStack)}
 * to implement their specific behavior.
 */
public abstract class BaseServerItem extends Item {
    
    public BaseServerItem(Properties properties) {
        super(properties);
    }
    
    /**
     * Handles item use interaction.
     * Delegates to {@link #onServerUse} on server-side, returns SUCCESS on client-side.
     */
    @Override
    public final InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            // Return SUCCESS to allow server-side processing
            return InteractionResult.SUCCESS;
        }
        
        ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer == null) {
            return InteractionResult.FAIL;
        }
        
        ItemStack stack = player.getItemInHand(hand);
        return onServerUse(level, serverPlayer, hand, stack);
    }
    
    /**
     * Called when a player uses this item on the server side.
     * 
     * @param level The level (guaranteed to be server-side)
     * @param player The server player (guaranteed to be non-null)
     * @param hand The hand used
     * @param stack The item stack being used
     * @return The interaction result
     */
    protected abstract InteractionResult onServerUse(Level level, ServerPlayer player, 
                                                     InteractionHand hand, ItemStack stack);
}


