package at.koopro.spells_n_squares.features.spell;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Interface for all spells in the mod.
 * All spells must implement this interface.
 */
public interface Spell {
    /**
     * Gets the unique identifier for this spell.
     * @return The spell ID
     */
    Identifier getId();
    
    /**
     * Gets the display name of the spell.
     * @return The spell name
     */
    String getName();
    
    /**
     * Gets the translatable name component for this spell.
     * @return The translatable name component
     */
    default Component getTranslatableName() {
        String translationKey = "spell." + getId().getNamespace() + "." + getId().getPath() + ".name";
        return Component.translatable(translationKey);
    }
    
    /**
     * Gets the description of what the spell does.
     * @return The spell description
     */
    String getDescription();
    
    /**
     * Gets the cooldown duration in ticks.
     * @return Cooldown in ticks
     */
    int getCooldown();
    
    /**
     * Gets the icon texture identifier for this spell.
     * Default implementation returns a texture based on the spell ID.
     * @return The icon texture identifier
     */
    default Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(getId().getNamespace(), "textures/spell/" + getId().getPath() + ".png");
    }
    
    /**
     * Casts the spell.
     * @param player The player casting the spell
     * @param level The level/world
     * @return true if the spell was successfully cast
     */
    boolean cast(Player player, Level level);
    
    /**
     * Gets the visual effect intensity (0.0 to 1.0).
     * Used for particle effects and visual feedback.
     * @return Visual effect intensity
     */
    float getVisualEffectIntensity();
    
    /**
     * Checks if this spell requires holding the cast key to maintain its effect.
     * Hold-to-cast spells will continuously update while the key is held.
     * 
     * To make a spell holdable:
     * 1. Override this method to return true
     * 2. Override onHoldTick() to implement the continuous effect
     * 3. In cast(), initialize any tracking data needed
     * 4. In onHoldTick(), update the effect each tick and return true to continue, false to stop
     * 
     * Example: Wingardium Leviosa tracks entities and moves them each tick while held.
     * 
     * @return true if this spell requires holding the key, false for one-time cast spells
     */
    default boolean isHoldToCast() {
        return false;
    }
    
    /**
     * Called every tick while the spell is being held (for hold-to-cast spells only).
     * This is called on the server side every tick after the initial cast().
     * 
     * For hold-to-cast spells, this method should:
     * - Update the spell's effect (e.g., move entities, maintain effects)
     * - Return true to continue holding, false to stop (will clean up automatically)
     * - Handle cleanup of tracked entities/state when returning false
     * 
     * @param player The player casting the spell
     * @param level The level/world
     * @return true if the spell should continue being held, false to stop and clean up
     */
    default boolean onHoldTick(Player player, Level level) {
        return false;
    }
    
    /**
     * Spawns visual effects when the spell is cast.
     * @param player The player casting the spell
     * @param level The level/world
     * @param success Whether the spell was successfully cast
     */
    default void spawnCastEffects(Player player, Level level, boolean success) {
        if (success && level.isClientSide()) {
            at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
            
            // Screen shake for powerful spells
            if (getVisualEffectIntensity() > 0.7f) {
                at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerShake(
                    0.1f * getVisualEffectIntensity(), 10);
            }
        }
    }
}




