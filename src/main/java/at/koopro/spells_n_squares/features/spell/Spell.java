package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.util.TranslationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Base interface for all spells in the mod.
 * Implement this interface to create custom spells.
 */
public interface Spell {
    /**
     * Gets the unique identifier for this spell.
     * @return The Identifier
     */
    Identifier getId();
    
    /**
     * Gets the display name of the spell.
     * @return The spell name
     * @deprecated Use getTranslatableName() for translatable names
     */
    @Deprecated
    String getName();
    
    /**
     * Gets the translatable display name of the spell.
     * Default implementation uses the translation key "spell.{namespace}.{path}.name"
     * @return The translatable Component for the spell name
     */
    default Component getTranslatableName() {
        return TranslationUtils.spellTranslatableName(getId());
    }
    
    /**
     * Gets the description of what the spell does.
     * @return The spell description
     */
    String getDescription();
    
    /**
     * Gets the cooldown time in ticks (20 ticks = 1 second).
     * @return Cooldown in ticks
     */
    int getCooldown();
    
    /**
     * Gets the icon texture identifier for this spell.
     * Default implementation returns a path based on the spell ID: textures/spell/{spell_id_path}.png
     * Addons can override this to use their own texture paths.
     * @return The Identifier for the spell icon texture
     */
    default net.minecraft.resources.Identifier getIcon() {
        // Default path: textures/spell/{spell_id_path}.png in the spell's namespace
        String path = "textures/spell/" + getId().getPath() + ".png";
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(getId().getNamespace(), path);
    }
    
    /**
     * Gets the mana cost of the spell.
     * @return Mana cost (0 if no mana system)
     */
    default int getManaCost() {
        return 0;
    }
    
    /**
     * Checks if the spell can be cast by the player.
     * @param player The player attempting to cast
     * @param level The level/world
     * @return true if the spell can be cast
     */
    default boolean canCast(Player player, Level level) {
        return true;
    }
    
    /**
     * Casts the spell. This is called when the spell is activated.
     * @param player The player casting the spell
     * @param level The level/world
     * @return true if the spell was successfully cast
     */
    boolean cast(Player player, Level level);
    
    /**
     * Gets the visual effect intensity for this spell.
     * Higher values create more dramatic visual effects.
     * @return Effect intensity (0.0 to 1.0, default 0.5)
     */
    default float getVisualEffectIntensity() {
        return 0.5f;
    }
    
    /**
     * Spawns spell-specific visual effects on cast.
     * Called after the spell is successfully cast.
     * @param player The player casting
     * @param level The level/world
     * @param success Whether the spell cast was successful
     */
    default void spawnCastEffects(Player player, Level level, boolean success) {
        // Default implementation: trigger screen flash
        if (level.isClientSide()) {
            at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
        }
    }
    
    /**
     * Spawns spell-specific impact effects when the spell hits.
     * @param level The level/world
     * @param position The impact position
     */
    default void spawnImpactEffects(Level level, net.minecraft.world.phys.Vec3 position) {
        // Default implementation: no special effects
    }
}
