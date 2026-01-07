package at.koopro.spells_n_squares.features.artifact;

import at.koopro.spells_n_squares.core.data.PersistentDataAccessHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

/**
 * Helper for accessing player immortality state.
 * Uses MobEffect for active immortality and persistent data for "hasEverDrunk" flag.
 */
public final class ImmortalityHelper {
    private ImmortalityHelper() {
    }
    
    private static final String HAS_EVER_DRUNK_KEY = "spells_n_squares:has_ever_drunk_elixir";
    private static final int IMMORTALITY_DURATION = 72000; // 60 minutes = 3 Minecraft days
    
    /**
     * Grants immortality to a player by applying the immortality effect.
     * Also marks them as having ever drunk the elixir (permanent curse).
     */
    public static void grantImmortality(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        // Apply immortality effect
        player.addEffect(new MobEffectInstance(
            ArtifactRegistry.IMMORTALITY_EFFECT,
            IMMORTALITY_DURATION,
            0,
            false,
            false // Show particles
        ));
        
        // Mark as having ever drunk (permanent curse)
        setHasEverDrunk(player, true);
    }
    
    /**
     * Marks a player as withered (immortality expired).
     */
    public static void markAsWithered(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        // Mark as having ever drunk (they're now cursed)
        setHasEverDrunk(player, true);
    }
    
    /**
     * Checks if a player is currently immortal (has active effect).
     */
    public static boolean isImmortal(Player player) {
        if (player == null) {
            return false;
        }
        return player.hasEffect(ArtifactRegistry.IMMORTALITY_EFFECT);
    }
    
    /**
     * Checks if a player has ever drunk the elixir (permanent curse).
     */
    public static boolean hasEverDrunk(Player player) {
        if (player == null) {
            return false;
        }
        return PersistentDataAccessHelper.load(
            player,
            HAS_EVER_DRUNK_KEY,
            com.mojang.serialization.Codec.BOOL,
            () -> false,
            "has_ever_drunk"
        );
    }
    
    /**
     * Checks if a player is withered (has ever drunk but effect expired).
     */
    public static boolean isWithered(Player player) {
        return hasEverDrunk(player) && !isImmortal(player);
    }
    
    /**
     * Gets the remaining duration of immortality in ticks.
     */
    public static int getRemainingTicks(Player player) {
        if (player == null) {
            return 0;
        }
        MobEffectInstance effect = player.getEffect(ArtifactRegistry.IMMORTALITY_EFFECT);
        return effect != null ? effect.getDuration() : 0;
    }
    
    /**
     * Sets the "has ever drunk" flag.
     */
    private static void setHasEverDrunk(Player player, boolean value) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        PersistentDataAccessHelper.save(
            player,
            HAS_EVER_DRUNK_KEY,
            com.mojang.serialization.Codec.BOOL,
            value,
            "has_ever_drunk"
        );
    }
}

