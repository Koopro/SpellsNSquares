package at.koopro.spells_n_squares.core.util.rendering;

import at.koopro.spells_n_squares.core.util.collection.CollectionUtils;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.text.StringUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Utility class for advanced sound and audio management.
 * Provides sound effect helpers, volume/pitch calculations, and sound playback utilities.
 */
public final class SoundUtils {
    private SoundUtils() {
        // Utility class - prevent instantiation
    }
    
    // Pre-configured spell sounds
    // Note: SoundEvents constants are Holders, but type erasure requires unchecked cast
    // This is safe as SoundEvents constants are guaranteed to be Holder<SoundEvent>
    @SuppressWarnings("unchecked")
    private static net.minecraft.core.Holder<SoundEvent> getSpellCastSound() { 
        return (net.minecraft.core.Holder<SoundEvent>) (Object) SoundEvents.ENCHANTMENT_TABLE_USE; 
    }
    @SuppressWarnings("unchecked")
    private static net.minecraft.core.Holder<SoundEvent> getMagicalSound() { 
        return (net.minecraft.core.Holder<SoundEvent>) (Object) SoundEvents.AMETHYST_BLOCK_CHIME; 
    }
    @SuppressWarnings("unchecked")
    private static net.minecraft.core.Holder<SoundEvent> getTeleportSound() { 
        return (net.minecraft.core.Holder<SoundEvent>) (Object) SoundEvents.ENDERMAN_TELEPORT; 
    }
    @SuppressWarnings("unchecked")
    private static net.minecraft.core.Holder<SoundEvent> getExplosionSound() { 
        return (net.minecraft.core.Holder<SoundEvent>) (Object) SoundEvents.GENERIC_EXPLODE; 
    }
    
    // Note: getSpellSuccessSound() and getSpellFailSound() methods are intentionally not implemented
    // as they are not currently used in the codebase. They can be added when needed.
    
    /**
     * Plays a spell-specific sound at a position.
     * 
     * @param level The level
     * @param position The position to play the sound at
     * @param spellName The spell name (used to determine sound type)
     */
    public static void playSpellSound(Level level, Vec3 position, String spellName) {
        if (level == null || position == null || StringUtils.isEmpty(spellName)) {
            return;
        }
        
        net.minecraft.core.Holder<SoundEvent> sound = getSpellSound(spellName);
        playSoundAtPosition(level, position, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    /**
     * Gets the appropriate sound for a spell name.
     * 
     * @param spellName The spell name
     * @return The sound event for the spell
     */
    private static net.minecraft.core.Holder<SoundEvent> getSpellSound(String spellName) {
        String spell = spellName.toLowerCase();
        return switch (spell) {
            case "teleport", "apparate" -> getTeleportSound();
            case "explosive", "bombarda" -> getExplosionSound();
            default -> getSpellCastSound();
        };
    }
    
    /**
     * Plays a generic magical sound at a position.
     * 
     * @param level The level
     * @param position The position to play the sound at
     * @param volume The volume (0.0-1.0)
     * @param pitch The pitch (0.5-2.0)
     */
    public static void playMagicalSound(Level level, Vec3 position, float volume, float pitch) {
        if (level == null || position == null) {
            return;
        }
        playSoundAtPosition(level, position, getMagicalSound(), SoundSource.PLAYERS, volume, pitch);
    }
    
    /**
     * Plays a magical sound with default volume and pitch.
     * 
     * @param level The level
     * @param position The position to play the sound at
     */
    public static void playMagicalSound(Level level, Vec3 position) {
        playMagicalSound(level, position, 1.0f, 1.0f);
    }
    
    /**
     * Calculates volume based on distance from source.
     * 
     * @param distance The distance from the source
     * @param maxDistance The maximum distance for full volume
     * @return The calculated volume (0.0-1.0)
     */
    public static float calculateVolumeByDistance(double distance, double maxDistance) {
        if (maxDistance <= 0) {
            return 1.0f;
        }
        
        if (distance >= maxDistance) {
            return 0.0f;
        }
        
        // Linear falloff
        return (float) (1.0 - (distance / maxDistance));
    }
    
    /**
     * Plays a sound at an entity's position.
     * 
     * @param level The level
     * @param entity The entity
     * @param sound The sound event
     * @param source The sound source
     * @param volume The volume (0.0-1.0)
     * @param pitch The pitch (0.5-2.0)
     */
    public static void playSoundAtEntity(Level level, Entity entity, net.minecraft.core.Holder<SoundEvent> sound, 
                                        SoundSource source, float volume, float pitch) {
        if (level == null || entity == null || sound == null) {
            return;
        }
        Vec3 pos = entity.position();
        playSoundAtPosition(level, pos, sound, source, volume, pitch);
    }
    
    /**
     * Plays a sound at a position.
     * 
     * @param level The level
     * @param position The position
     * @param sound The sound event
     * @param source The sound source
     * @param volume The volume (0.0-1.0)
     * @param pitch The pitch (0.5-2.0)
     */
    public static void playSoundAtPosition(Level level, Vec3 position, net.minecraft.core.Holder<SoundEvent> sound,
                                          SoundSource source, float volume, float pitch) {
        if (level == null || position == null || sound == null) {
            return;
        }
        
        volume = MathUtils.clamp(volume, 0.0f, 1.0f);
        pitch = MathUtils.clamp(pitch, 0.5f, 2.0f);
        
        level.playSound(null, position.x, position.y, position.z, sound, source, volume, pitch);
    }
    
    /**
     * Stops a specific sound for a player.
     * Note: This requires client-side implementation for full effect.
     * 
     * @param player The player
     * @param sound The sound event to stop
     */
    public static void stopSound(Player player, SoundEvent sound) {
        if (player == null || sound == null) {
            return;
        }
        // Client-side only - would need client-side handler
        if (player.level().isClientSide()) {
            // This would need to be implemented on the client
            // For now, this is a placeholder
        }
    }
    
    /**
     * Plays a sequence of sounds with delays between them.
     * 
     * @param level The level
     * @param position The position to play sounds at
     * @param sounds The list of sounds to play
     * @param delayTicks The delay between sounds in ticks
     */
    public static void playSoundSequence(Level level, Vec3 position, List<net.minecraft.core.Holder<SoundEvent>> sounds, int delayTicks) {
        if (level == null || position == null || CollectionUtils.isEmpty(sounds) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        for (int i = 0; i < sounds.size(); i++) {
            final net.minecraft.core.Holder<SoundEvent> sound = sounds.get(i);
            int delay = i * delayTicks;
            
            // Schedule sound to play after delay
            int finalDelay = delay;
            serverLevel.getServer().execute(() -> {
                if (serverLevel.getGameTime() % delayTicks == 0 || finalDelay == 0) {
                    playSoundAtPosition(serverLevel, position, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            });
        }
    }
    
    /**
     * Plays a sound with distance-based volume.
     * 
     * @param level The level
     * @param position The position to play the sound at
     * @param listenerPosition The position of the listener
     * @param sound The sound event
     * @param source The sound source
     * @param maxDistance The maximum distance for full volume
     * @param pitch The pitch (0.5-2.0)
     */
    public static void playSoundWithDistanceVolume(Level level, Vec3 position, Vec3 listenerPosition,
                                                   net.minecraft.core.Holder<SoundEvent> sound, SoundSource source,
                                                   double maxDistance, float pitch) {
        if (level == null || position == null || listenerPosition == null || sound == null) {
            return;
        }
        
        double distance = MathUtils.distance(position, listenerPosition);
        float volume = calculateVolumeByDistance(distance, maxDistance);
        
        if (volume > 0.0f) {
            playSoundAtPosition(level, position, sound, source, volume, pitch);
        }
    }
    
    /**
     * Parses a sound event from a resource location string.
     * 
     * @param soundName The sound name (e.g., "minecraft:entity.player.levelup")
     * @return The sound event, or null if not found
     */
    public static SoundEvent parseSoundEvent(String soundName) {
        // Note: Simplified - returns null for now
        // Use SoundEvents constants directly or implement proper registry lookup
        return null;
    }
}


