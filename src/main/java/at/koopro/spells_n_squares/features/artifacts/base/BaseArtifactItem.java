package at.koopro.spells_n_squares.features.artifacts.base;

import at.koopro.spells_n_squares.core.item.base.BaseServerItem;
import at.koopro.spells_n_squares.core.util.effect.EffectUtils;
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
 * Base class for artifact items.
 * Extends BaseServerItem with artifact-specific patterns:
 * - Automatically sets stacksTo(1) for unique items
 * - Provides common artifact patterns (particles, sounds, messages)
 * - Template methods for artifact-specific behavior
 */
public abstract class BaseArtifactItem extends BaseServerItem {
    
    public BaseArtifactItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    /**
     * Called when a player uses this artifact on the server side.
     * Subclasses should override this to implement artifact-specific behavior.
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
        InteractionResult result = onArtifactUse(level, player, hand, stack);
        
        // Spawn magical particles if the artifact was successfully used
        if (result == InteractionResult.SUCCESS && level instanceof ServerLevel serverLevel) {
            spawnArtifactEffects(serverLevel, player);
        }
        
        return result;
    }
    
    /**
     * Called when a player uses this artifact.
     * Subclasses should override this to implement artifact-specific behavior.
     * 
     * @param level The level (guaranteed to be server-side)
     * @param player The server player (guaranteed to be non-null)
     * @param hand The hand used
     * @param stack The item stack being used
     * @return The interaction result
     */
    protected abstract InteractionResult onArtifactUse(Level level, ServerPlayer player, 
                                                        InteractionHand hand, ItemStack stack);
    
    /**
     * Spawns visual and audio effects when the artifact is used.
     * Can be overridden by subclasses for custom effects.
     * 
     * @param level The server level
     * @param player The player using the artifact
     */
    protected void spawnArtifactEffects(ServerLevel level, ServerPlayer player) {
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        EffectUtils.spawnMagicalParticles(level, pos);
        EffectUtils.playActivationSound(level, pos);
    }
    
    /**
     * Sends a translatable message to the player.
     * Convenience method for artifact items.
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
     * Convenience method for artifact items.
     * 
     * @param player The server player
     * @param message The message component
     */
    protected void sendMessage(ServerPlayer player, Component message) {
        InteractionUtils.sendPlayerMessage(player, message);
    }
}

