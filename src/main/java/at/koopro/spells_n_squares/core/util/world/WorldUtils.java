package at.koopro.spells_n_squares.core.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for world and level utilities.
 * Provides biome detection, structure detection, light level checks, weather utilities, and time queries.
 */
public final class WorldUtils {
    private WorldUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the biome at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @return The biome holder, or null if invalid
     */
    public static Holder<Biome> getBiomeAt(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }
        
        return level.getBiome(pos);
    }
    
    /**
     * Checks if a position is in a specific biome.
     * 
     * @param level The level
     * @param pos The block position
     * @param biomeKey The biome resource key
     * @return true if in the biome
     */
    public static boolean isInBiome(Level level, BlockPos pos, ResourceKey<Biome> biomeKey) {
        if (level == null || pos == null || biomeKey == null) {
            return false;
        }
        
        Holder<Biome> biome = getBiomeAt(level, pos);
        return biome != null && biome.is(biomeKey);
    }
    
    /**
     * Finds nearby biomes within a radius.
     * 
     * @param level The level
     * @param center The center position
     * @param radius The search radius
     * @return Set of unique biome resource keys found
     */
    public static Set<ResourceKey<Biome>> findNearbyBiomes(Level level, BlockPos center, double radius) {
        if (level == null || center == null || radius <= 0) {
            return Set.of();
        }
        
        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        int radiusInt = (int) Math.ceil(radius);
        
        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int z = -radiusInt; z <= radiusInt; z++) {
                if (x * x + z * z <= radius * radius) {
                    BlockPos pos = center.offset(x, 0, z);
                    Holder<Biome> biome = getBiomeAt(level, pos);
                    if (biome != null) {
                        biome.unwrapKey().ifPresent(biomes::add);
                    }
                }
            }
        }
        
        return biomes;
    }
    
    /**
     * Checks if a structure exists at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @param structureKey The structure resource key
     * @return true if structure exists at position
     */
    public static boolean hasStructure(Level level, BlockPos pos, ResourceKey<Structure> structureKey) {
        // Note: Simplified - structure detection API may need adjustment
        // For now, returns false
        return false;
    }
    
    /**
     * Gets the light level at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @return The light level (0-15)
     */
    public static int getLightLevel(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return 0;
        }
        
        return level.getMaxLocalRawBrightness(pos);
    }
    
    /**
     * Checks if a mob can spawn at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @return true if spawn conditions are met (light level <= 7, solid block below, air at position)
     */
    public static boolean canMobSpawn(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        
        // Check light level
        if (getLightLevel(level, pos) > 7) {
            return false;
        }
        
        // Check if block below is solid
        BlockPos below = pos.below();
        if (!BlockUtils.isSolid(level, below)) {
            return false;
        }
        
        // Check if position is air
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the time of day in ticks (0-24000).
     * 
     * @param level The level
     * @return The time of day, or 0 if invalid
     */
    public static long getTimeOfDay(Level level) {
        if (level == null) {
            return 0;
        }
        
        return level.getDayTime() % 24000;
    }
    
    /**
     * Checks if it is day (time between 0 and 12000).
     * 
     * @param level The level
     * @return true if day
     */
    public static boolean isDay(Level level) {
        if (level == null) {
            return false;
        }
        
        long time = getTimeOfDay(level);
        return time >= 0 && time < 12000;
    }
    
    /**
     * Checks if it is night (time between 12000 and 24000).
     * 
     * @param level The level
     * @return true if night
     */
    public static boolean isNight(Level level) {
        if (level == null) {
            return false;
        }
        
        return !isDay(level);
    }
    
    /**
     * Checks if it is raining.
     * 
     * @param level The level
     * @return true if raining
     */
    public static boolean isRaining(Level level) {
        if (level == null) {
            return false;
        }
        
        return level.isRaining();
    }
    
    /**
     * Checks if it is thundering.
     * 
     * @param level The level
     * @return true if thundering
     */
    public static boolean isThundering(Level level) {
        if (level == null) {
            return false;
        }
        
        return level.isThundering();
    }
    
    /**
     * Gets the day count (number of days since world creation).
     * 
     * @param level The level
     * @return The day count
     */
    public static long getDayCount(Level level) {
        if (level == null) {
            return 0;
        }
        
        return level.getDayTime() / 24000;
    }
    
    /**
     * Gets the weather info as a string.
     * 
     * @param level The level
     * @return Weather description
     */
    public static String getWeatherInfo(Level level) {
        if (level == null) {
            return "Unknown";
        }
        
        if (isThundering(level)) {
            return "Thundering";
        } else if (isRaining(level)) {
            return "Raining";
        } else {
            return "Clear";
        }
    }
}

