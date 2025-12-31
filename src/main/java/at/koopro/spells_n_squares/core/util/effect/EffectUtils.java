package at.koopro.spells_n_squares.core.util.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for common visual and audio effects.
 * Provides pre-configured effect templates and helper methods for spawning effects.
 */
public final class EffectUtils {
    private EffectUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Spawns a magical particle effect (enchant + end rod particles) at a position.
     * 
     * @param level The server level
     * @param position The position to spawn particles at
     */
    public static void spawnMagicalParticles(ServerLevel level, Vec3 position) {
        spawnMagicalParticles(level, position, 1.0);
    }
    
    /**
     * Spawns a magical particle effect with intensity multiplier.
     * 
     * @param level The server level
     * @param position The position to spawn particles at
     * @param intensity Multiplier for particle count (1.0 = normal, 2.0 = double, etc.)
     */
    public static void spawnMagicalParticles(ServerLevel level, Vec3 position, double intensity) {
        if (level == null || position == null) {
            return;
        }
        
        int enchantCount = (int) (30 * intensity);
        int endRodCount = (int) (20 * intensity);
        
        level.sendParticles(
            ParticleTypes.ENCHANT,
            position.x, position.y, position.z,
            enchantCount, 0.5, 0.5, 0.5, 0.1
        );
        
        level.sendParticles(
            ParticleTypes.END_ROD,
            position.x, position.y, position.z,
            endRodCount, 0.3, 0.3, 0.3, 0.05
        );
    }
    
    /**
     * Spawns particles at a player's eye position.
     * 
     * @param level The server level
     * @param player The player
     * @param particle The particle type
     * @param count The number of particles
     * @param spreadX X-axis spread
     * @param spreadY Y-axis spread
     * @param spreadZ Z-axis spread
     * @param speed Particle speed
     */
    public static void spawnParticlesAtPlayer(ServerLevel level, net.minecraft.world.entity.player.Player player,
                                              ParticleOptions particle, int count,
                                              double spreadX, double spreadY, double spreadZ, double speed) {
        if (level == null || player == null) {
            return;
        }
        
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        level.sendParticles(particle, pos.x, pos.y, pos.z, count, spreadX, spreadY, spreadZ, speed);
    }
    
    /**
     * Spawns particles at a position.
     * 
     * @param level The server level
     * @param position The position
     * @param particle The particle type
     * @param count The number of particles
     * @param spreadX X-axis spread
     * @param spreadY Y-axis spread
     * @param spreadZ Z-axis spread
     * @param speed Particle speed
     */
    public static void spawnParticles(ServerLevel level, Vec3 position, ParticleOptions particle, int count,
                                      double spreadX, double spreadY, double spreadZ, double speed) {
        if (level == null || position == null) {
            return;
        }
        
        level.sendParticles(particle, position.x, position.y, position.z, count, spreadX, spreadY, spreadZ, speed);
    }
    
    /**
     * Plays an activation sound (amethyst chime) at a position.
     * 
     * @param level The server level
     * @param position The position to play the sound at
     * @param volume The volume (default 1.0)
     * @param pitch The pitch (default 1.0)
     */
    public static void playActivationSound(ServerLevel level, Vec3 position, float volume, float pitch) {
        if (level == null || position == null) {
            return;
        }
        
        level.playSound(null, position.x, position.y, position.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, volume, pitch);
    }
    
    /**
     * Plays an activation sound with default volume and pitch.
     * 
     * @param level The server level
     * @param position The position to play the sound at
     */
    public static void playActivationSound(ServerLevel level, Vec3 position) {
        playActivationSound(level, position, 1.0f, 1.0f);
    }
    
    /**
     * Plays a success/confirmation sound (villager yes) at a position.
     * 
     * @param level The server level
     * @param position The position to play the sound at
     * @param volume The volume (default 1.0)
     * @param pitch The pitch (default 1.0)
     */
    public static void playSuccessSound(ServerLevel level, Vec3 position, float volume, float pitch) {
        if (level == null || position == null) {
            return;
        }
        
        level.playSound(null, position.x, position.y, position.z,
            SoundEvents.VILLAGER_YES, SoundSource.PLAYERS, volume, pitch);
    }
    
    /**
     * Plays a success sound with default volume and pitch.
     * 
     * @param level The server level
     * @param position The position to play the sound at
     */
    public static void playSuccessSound(ServerLevel level, Vec3 position) {
        playSuccessSound(level, position, 1.0f, 1.0f);
    }
    
    /**
     * Spawns fire particles at a position.
     * 
     * @param level The server level
     * @param position The position
     * @param count The number of particles
     */
    public static void spawnFireParticles(ServerLevel level, Vec3 position, int count) {
        spawnParticles(level, position, ParticleTypes.FLAME, count, 0.3, 0.3, 0.3, 0.1);
    }
    
    /**
     * Spawns smoke particles at a position.
     * 
     * @param level The server level
     * @param position The position
     * @param count The number of particles
     */
    public static void spawnSmokeParticles(ServerLevel level, Vec3 position, int count) {
        spawnParticles(level, position, ParticleTypes.SMOKE, count, 0.5, 0.5, 0.5, 0.1);
    }
}


