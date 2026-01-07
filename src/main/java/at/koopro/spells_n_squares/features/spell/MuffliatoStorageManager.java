package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.util.math.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages persistent storage for Muffliato area effects in level data.
 * Each muffling area has a center position, radius, and expiration time.
 * 
 * Note: Uses in-memory storage per level. For full persistence, this should
 * be migrated to SavedData when the API is properly understood.
 */
public final class MuffliatoStorageManager {
    private MuffliatoStorageManager() {
    }
    
    // Map of level dimension key to list of muffling areas
    // Thread-safe for concurrent access
    private static final Map<net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>, List<MuffliatoArea>> levelAreas = new ConcurrentHashMap<>();
    
    /**
     * Represents a muffling area effect.
     */
    public record MuffliatoArea(
        Vec3 center,
        double radius,
        int remainingTicks,
        UUID casterId
    ) {
        public static final Codec<MuffliatoArea> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter(a -> a.center.x),
                Codec.DOUBLE.fieldOf("y").forGetter(a -> a.center.y),
                Codec.DOUBLE.fieldOf("z").forGetter(a -> a.center.z),
                Codec.DOUBLE.fieldOf("radius").forGetter(MuffliatoArea::radius),
                Codec.INT.fieldOf("remainingTicks").forGetter(MuffliatoArea::remainingTicks),
                Codec.STRING.fieldOf("casterId").xmap(UUID::fromString, UUID::toString).forGetter(MuffliatoArea::casterId)
            ).apply(instance, (x, y, z, radius, remainingTicks, casterId) ->
                new MuffliatoArea(new Vec3(x, y, z), radius, remainingTicks, casterId))
        );
        
        /**
         * Checks if a position is within this muffling area.
         */
        public boolean contains(Vec3 pos) {
            return MathUtils.distance(center, pos) <= radius;
        }
        
        /**
         * Checks if this area has expired.
         */
        public boolean isExpired() {
            return remainingTicks <= 0;
        }
    }
    
    /**
     * Gets all active muffling areas in a level.
     */
    public static List<MuffliatoArea> getMuffliatoAreas(ServerLevel level) {
        if (level == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(levelAreas.computeIfAbsent(level.dimension(), k -> new ArrayList<>()));
    }
    
    /**
     * Adds a muffling area to a level.
     */
    public static void addMuffliatoArea(ServerLevel level, Vec3 center, double radius, int durationTicks, UUID casterId) {
        if (level == null) {
            return;
        }
        
        MuffliatoArea area = new MuffliatoArea(center, radius, durationTicks, casterId);
        levelAreas.computeIfAbsent(level.dimension(), k -> new ArrayList<>()).add(area);
    }
    
    /**
     * Removes expired muffling areas and updates remaining ticks.
     * Should be called every tick.
     */
    public static void tick(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        List<MuffliatoArea> areas = levelAreas.get(level.dimension());
        if (areas == null || areas.isEmpty()) {
            return;
        }
        
        List<MuffliatoArea> updatedAreas = new ArrayList<>();
        for (MuffliatoArea area : areas) {
            if (!area.isExpired()) {
                // Update remaining ticks
                MuffliatoArea updated = new MuffliatoArea(
                    area.center(),
                    area.radius(),
                    area.remainingTicks() - 1,
                    area.casterId()
                );
                updatedAreas.add(updated);
            }
            // Expired areas are simply not added to updatedAreas
        }
        
        // Replace the list atomically
        levelAreas.put(level.dimension(), updatedAreas);
    }
    
    /**
     * Checks if a position is within any muffling area.
     */
    public static boolean isMuffled(ServerLevel level, Vec3 pos) {
        List<MuffliatoArea> areas = getMuffliatoAreas(level);
        for (MuffliatoArea area : areas) {
            if (area.contains(pos) && !area.isExpired()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes all muffling areas created by a specific caster.
     */
    public static void removeMuffliatoAreasByCaster(ServerLevel level, UUID casterId) {
        if (level == null) {
            return;
        }
        
        List<MuffliatoArea> areas = levelAreas.get(level.dimension());
        if (areas != null) {
            areas.removeIf(area -> area.casterId().equals(casterId));
        }
    }
    
    /**
     * Clears all muffling areas for a level (called when level unloads).
     */
    public static void clearLevel(ServerLevel level) {
        if (level != null) {
            levelAreas.remove(level.dimension());
        }
    }
}

