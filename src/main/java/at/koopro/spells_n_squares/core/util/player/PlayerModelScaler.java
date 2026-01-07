package at.koopro.spells_n_squares.core.util.player;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import net.minecraft.world.entity.player.Player;

/**
 * Handles setting and getting player model scales.
 * Separated from PlayerModelUtils for better organization.
 */
public final class PlayerModelScaler {
    private PlayerModelScaler() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sets the overall player scale.
     * 
     * @param player The player
     * @param scale The scale (1.0 = normal, 0.5 = half size, 2.0 = double)
     */
    public static void setPlayerScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withScale(scale));
    }
    
    /**
     * Sets the head scale.
     */
    public static void setHeadScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withHeadScale(scale));
    }
    
    /**
     * Sets the body scale.
     */
    public static void setBodyScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withBodyScale(scale));
    }
    
    /**
     * Sets the left arm scale.
     */
    public static void setLeftArmScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withLeftArmScale(scale));
    }
    
    /**
     * Sets the right arm scale.
     */
    public static void setRightArmScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withRightArmScale(scale));
    }
    
    /**
     * Sets the left leg scale.
     */
    public static void setLeftLegScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withLeftLegScale(scale));
    }
    
    /**
     * Sets the right leg scale.
     */
    public static void setRightLegScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withRightLegScale(scale));
    }
    
    /**
     * Sets the scale for a specific body part.
     */
    public static void setBodyPartScale(Player player, PlayerModelDataComponent.BodyPart part, float scale) {
        if (player == null || part == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withBodyPartScale(part, scale));
    }
    
    /**
     * Sets the hitbox scale (independent from visual model).
     */
    public static void setHitboxScale(Player player, float scale) {
        if (player == null) {
            return;
        }
        scale = MathUtils.clamp(scale, 0.1f, 10.0f);
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.withHitboxScale(scale));
    }
    
    /**
     * Resets all player model modifications to default.
     */
    public static void resetPlayerModel(Player player) {
        if (player == null) {
            return;
        }
        PlayerModelUtils.setModelData(player, PlayerModelDataComponent.PlayerModelData.empty());
    }
    
    /**
     * Resets a specific body part to default scale.
     */
    public static void resetBodyPart(Player player, PlayerModelDataComponent.BodyPart part) {
        if (player == null || part == null) {
            return;
        }
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(player);
        PlayerModelUtils.setModelData(player, data.resetBodyPart(part));
    }
    
    /**
     * Gets the current overall player scale.
     */
    public static float getPlayerScale(Player player) {
        if (player == null) {
            return 1.0f;
        }
        return PlayerModelDataAccessor.getModelData(player).scale();
    }
    
    /**
     * Gets the current head scale.
     */
    public static float getHeadScale(Player player) {
        if (player == null) {
            return 1.0f;
        }
        return PlayerModelDataAccessor.getModelData(player).headScale();
    }
    
    /**
     * Gets the scale for a specific body part.
     */
    public static float getBodyPartScale(Player player, PlayerModelDataComponent.BodyPart part) {
        if (player == null || part == null) {
            return 1.0f;
        }
        return PlayerModelDataAccessor.getModelData(player).getBodyPartScale(part);
    }
}

