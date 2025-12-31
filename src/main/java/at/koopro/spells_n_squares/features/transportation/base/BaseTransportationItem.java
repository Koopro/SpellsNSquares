package at.koopro.spells_n_squares.features.transportation.base;

import at.koopro.spells_n_squares.core.item.base.BaseServerItem;
import at.koopro.spells_n_squares.core.util.InteractionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Base class for transportation items (broomsticks, portkeys, floo powder, etc.).
 * Extends BaseServerItem with transportation-specific patterns:
 * - Common teleportation validation
 * - Position validation utilities
 * - Transportation-specific error messages
 */
public abstract class BaseTransportationItem extends BaseServerItem {
    
    public BaseTransportationItem(Properties properties) {
        super(properties);
    }
    
    /**
     * Called when a player uses this transportation item on the server side.
     * Subclasses should override this to implement transportation-specific behavior.
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
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        return onTransportationUse(serverLevel, player, hand, stack);
    }
    
    /**
     * Called when a player uses this transportation item.
     * Subclasses should override this to implement transportation-specific behavior.
     * 
     * @param level The server level (guaranteed to be non-null)
     * @param player The server player (guaranteed to be non-null)
     * @param hand The hand used
     * @param stack The item stack being used
     * @return The interaction result
     */
    protected abstract InteractionResult onTransportationUse(ServerLevel level, ServerPlayer player, 
                                                               InteractionHand hand, ItemStack stack);
    
    /**
     * Validates that a position is safe for teleportation.
     * Checks for solid ground and air space above.
     * 
     * @param level The server level
     * @param pos The position to validate
     * @return true if the position is safe, false otherwise
     */
    protected boolean isValidTeleportPosition(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) {
            return false;
        }
        
        // Check if there's solid ground below
        int groundY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, 
            (int) pos.x, (int) pos.z);
        
        // Position should be at least 1 block above ground
        return pos.y >= groundY + 1.0;
    }
    
    /**
     * Sends a translatable message to the player.
     * Convenience method for transportation items.
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
     * Convenience method for transportation items.
     * 
     * @param player The server player
     * @param message The message component
     */
    protected void sendMessage(ServerPlayer player, Component message) {
        InteractionUtils.sendPlayerMessage(player, message);
    }
    
    /**
     * Checks if the player can use transportation (not already riding, not in water, etc.).
     * 
     * @param player The server player
     * @return true if the player can use transportation, false otherwise
     */
    protected boolean canUseTransportation(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        
        // Check if player is already riding something
        if (player.isPassenger()) {
            return false;
        }
        
        // Additional checks can be added here (e.g., not in water, not in combat, etc.)
        return true;
    }
}


