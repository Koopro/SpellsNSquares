package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Helper utility class for player identity operations.
 * Provides methods for getting display names, descriptions, and validating combinations.
 */
public final class PlayerIdentityHelper {
    private PlayerIdentityHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the display name for a blood status.
     */
    public static String getBloodStatusDisplayName(PlayerIdentityData.BloodStatus bloodStatus) {
        return bloodStatus.getDisplayName();
    }
    
    /**
     * Gets the display name for a magical type.
     */
    public static String getMagicalTypeDisplayName(PlayerIdentityData.MagicalType magicalType) {
        return magicalType.getDisplayName();
    }
    
    /**
     * Gets a description for a blood status.
     */
    public static String getBloodStatusDescription(PlayerIdentityData.BloodStatus bloodStatus) {
        return switch (bloodStatus) {
            case PURE_BLOOD -> "Both parents are magical";
            case HALF_BLOOD -> "One magical parent, one Muggle parent";
            case MUGGLE_BORN -> "No magical parents (born with magic)";
            case SQUIB -> "Born to magical parents but has no magical ability";
        };
    }
    
    /**
     * Gets a description for a magical type.
     */
    public static String getMagicalTypeDescription(PlayerIdentityData.MagicalType magicalType) {
        return switch (magicalType) {
            case WIZARD -> "Male magical person";
            case WITCH -> "Female magical person";
            case SQUIB -> "Born to magical parents but has no magical ability";
            case WEREWOLF -> "Person infected with lycanthropy (transforms during full moon)";
            case VEELA -> "Enchanting magical being with alluring powers";
            case VAMPIRE -> "Undead magical being with enhanced abilities";
            case GOBLIN -> "Magical humanoid race (bankers, craftsmen)";
            case HOUSE_ELF -> "Magical servant race bound to families";
            case GIANT -> "Person with giant heritage (Half-Giant)";
            case CENTAUR -> "Half-human, half-horse magical being";
        };
    }
    
    /**
     * Validates if a blood status and magical type combination is valid.
     */
    public static boolean isValidCombination(PlayerIdentityData.BloodStatus bloodStatus, 
                                            PlayerIdentityData.MagicalType magicalType) {
        // Squib type must have Squib blood status
        if (magicalType == PlayerIdentityData.MagicalType.SQUIB) {
            return bloodStatus == PlayerIdentityData.BloodStatus.SQUIB;
        }
        // Squib blood status must have Squib type
        if (bloodStatus == PlayerIdentityData.BloodStatus.SQUIB) {
            return magicalType == PlayerIdentityData.MagicalType.SQUIB;
        }
        // All other combinations are valid
        return true;
    }
    
    /**
     * Gets the default magical type based on player gender.
     */
    public static PlayerIdentityData.MagicalType getDefaultTypeForGender(boolean isMale) {
        return isMale ? PlayerIdentityData.MagicalType.WIZARD : PlayerIdentityData.MagicalType.WITCH;
    }
    
    /**
     * Gets the default magical type for a player.
     */
    public static PlayerIdentityData.MagicalType getDefaultTypeForPlayer(Player player) {
        if (player == null) {
            return PlayerIdentityData.MagicalType.WIZARD;
        }
        // Check player gender - Minecraft doesn't have explicit gender, so we'll use a simple heuristic
        // For now, default to Wizard (can be enhanced later)
        return PlayerIdentityData.MagicalType.WIZARD;
    }
    
    /**
     * Checks if a race has special abilities or restrictions.
     * This can be used for future gameplay mechanics.
     */
    public static boolean hasSpecialAbilities(PlayerIdentityData.MagicalType magicalType) {
        return switch (magicalType) {
            case WEREWOLF, VEELA, VAMPIRE, GOBLIN, HOUSE_ELF, GIANT, CENTAUR -> true;
            case WIZARD, WITCH, SQUIB -> false;
        };
    }
    
    /**
     * Gets a formatted component for displaying player identity.
     */
    public static Component getIdentityDisplayComponent(Player player) {
        if (player == null) {
            return Component.literal("Unknown");
        }
        
        PlayerIdentityData.IdentityData identity = PlayerDataHelper.getIdentityData(player);
        String bloodStatus = getBloodStatusDisplayName(identity.bloodStatus());
        String magicalType = getMagicalTypeDisplayName(identity.magicalType());
        
        return Component.literal(magicalType + " (" + bloodStatus + ")");
    }
    
    /**
     * Gets the default scale for a magical type.
     * Returns the overall scale multiplier for the race.
     */
    public static float getDefaultScale(PlayerIdentityData.MagicalType magicalType) {
        return switch (magicalType) {
            case GIANT -> 1.5f;        // Half-Giants are larger
            case GOBLIN -> 0.7f;       // Goblins are smaller
            case HOUSE_ELF -> 0.6f;    // House Elves are very small
            case CENTAUR -> 1.2f;      // Centaurs are slightly larger (taller)
            case WIZARD, WITCH, SQUIB, WEREWOLF, VEELA, VAMPIRE -> 1.0f; // Normal size
        };
    }
    
    /**
     * Gets the default head scale for a magical type.
     * Some races have proportionally different head sizes.
     */
    public static float getDefaultHeadScale(PlayerIdentityData.MagicalType magicalType) {
        return switch (magicalType) {
            case GOBLIN -> 1.2f;       // Goblins have larger heads relative to body
            case HOUSE_ELF -> 1.3f;     // House Elves have large eyes/heads
            case GIANT -> 0.9f;         // Giants have proportionally smaller heads
            case CENTAUR -> 1.0f;       // Centaurs have normal head size
            case WIZARD, WITCH, SQUIB, WEREWOLF, VEELA, VAMPIRE -> 1.0f; // Normal size
        };
    }
    
    /**
     * Gets the default body scale for a magical type.
     * Some races have different body proportions.
     */
    public static float getDefaultBodyScale(PlayerIdentityData.MagicalType magicalType) {
        return switch (magicalType) {
            case GIANT -> 1.1f;        // Giants have larger torsos
            case GOBLIN -> 0.8f;       // Goblins have smaller bodies
            case HOUSE_ELF -> 0.7f;     // House Elves have very small bodies
            case CENTAUR -> 1.0f;      // Centaurs have normal human torso
            case WIZARD, WITCH, SQUIB, WEREWOLF, VEELA, VAMPIRE -> 1.0f; // Normal size
        };
    }
    
    /**
     * Applies race-based size scaling to a player based on their magical type.
     * This should be called when identity is set or when player logs in.
     */
    public static void applyRaceScaling(ServerPlayer player) {
        if (player == null) {
            return;
        }
        
        PlayerIdentityData.IdentityData identity = PlayerDataHelper.getIdentityData(player);
        if (identity == null) {
            return;
        }
        
        // Get current model data
        PlayerModelDataComponent.PlayerModelData currentData = 
            at.koopro.spells_n_squares.core.util.player.PlayerModelUtils.getModelData(player);
        
        // Only apply race scaling if player hasn't manually set a custom scale
        // If scale is already different from 1.0, assume it was manually set
        boolean hasCustomScale = currentData.scale() != 1.0f || 
                                 currentData.headScale() != 1.0f || 
                                 currentData.bodyScale() != 1.0f;
        
        if (hasCustomScale) {
            // Player has custom scaling, don't override it
            return;
        }
        
        // Get race-based scales
        float overallScale = getDefaultScale(identity.magicalType());
        float headScale = getDefaultHeadScale(identity.magicalType());
        float bodyScale = getDefaultBodyScale(identity.magicalType());
        
        // Only apply if different from default
        if (overallScale != 1.0f || headScale != 1.0f || bodyScale != 1.0f) {
            // Hitbox scale should match overall scale for race-based scaling
            float hitboxScale = overallScale;
            
            PlayerModelDataComponent.PlayerModelData newData = new PlayerModelDataComponent.PlayerModelData(
                overallScale,
                headScale,
                bodyScale,
                currentData.leftArmScale(),
                currentData.rightArmScale(),
                currentData.leftLegScale(),
                currentData.rightLegScale(),
                hitboxScale, // Match overall scale for race-based sizing
                currentData.width(),
                currentData.height()
            );
            
            at.koopro.spells_n_squares.core.util.player.PlayerModelUtils.setModelData(player, newData);
        }
    }
}

