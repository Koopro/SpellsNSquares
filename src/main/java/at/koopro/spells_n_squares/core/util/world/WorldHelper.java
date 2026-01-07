package at.koopro.spells_n_squares.core.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.*;

/**
 * Utility class for world queries, structure detection, and biome checks.
 * Provides methods for querying world state and finding structures.
 */
public final class WorldHelper {
    
    private WorldHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the biome at a position.
     * 
     * @param level The level
     * @param pos The position
     * @return The biome resource key, or null if not available
     */
    public static ResourceKey<Biome> getBiome(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }
        
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getBiome(pos).unwrapKey().orElse(null);
        }
        
        return null;
    }
    
    /**
     * Checks if a position is in a specific biome.
     * 
     * @param level The level
     * @param pos The position
     * @param biomeKey The biome to check for
     * @return true if position is in the biome
     */
    public static boolean isInBiome(Level level, BlockPos pos, ResourceKey<Biome> biomeKey) {
        if (level == null || pos == null || biomeKey == null) {
            return false;
        }
        
        ResourceKey<Biome> currentBiome = getBiome(level, pos);
        return currentBiome != null && currentBiome.equals(biomeKey);
    }
    
    /**
     * Gets the dimension key for a level.
     * 
     * @param level The level
     * @return The dimension key, or null if not available
     */
    public static ResourceKey<Level> getDimension(Level level) {
        if (level == null) {
            return null;
        }
        return level.dimension();
    }
    
    /**
     * Checks if a level is a specific dimension.
     * 
     * @param level The level
     * @param dimensionKey The dimension to check
     * @return true if level is in the dimension
     */
    public static boolean isDimension(Level level, ResourceKey<Level> dimensionKey) {
        if (level == null || dimensionKey == null) {
            return false;
        }
        return level.dimension().equals(dimensionKey);
    }
    
    /**
     * Finds the nearest player to a position.
     * 
     * @param level The level
     * @param pos The position
     * @param maxDistance Maximum search distance
     * @return The nearest player, or null if not found
     */
    public static Player findNearestPlayer(Level level, BlockPos pos, double maxDistance) {
        if (level == null || pos == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        
        Player nearest = null;
        double nearestDistance = maxDistance;
        
        for (Player player : serverLevel.players()) {
            double distance = BlockHelper.getDistance(pos, player.blockPosition());
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        
        return nearest;
    }
    
    /**
     * Gets all players within a radius of a position.
     * 
     * @param level The level
     * @param pos The position
     * @param radius The radius
     * @return List of players within radius
     */
    public static List<Player> getPlayersInRadius(Level level, BlockPos pos, double radius) {
        if (level == null || pos == null || !(level instanceof ServerLevel serverLevel)) {
            return Collections.emptyList();
        }
        
        List<Player> players = new ArrayList<>();
        for (Player player : serverLevel.players()) {
            double distance = BlockHelper.getDistance(pos, player.blockPosition());
            if (distance <= radius) {
                players.add(player);
            }
        }
        
        return players;
    }
    
    /**
     * Gets all entities within a radius of a position.
     * 
     * @param level The level
     * @param pos The position
     * @param radius The radius
     * @param entityClass The entity class to filter by (null for all entities)
     * @return List of entities within radius
     */
    public static <T extends Entity> List<T> getEntitiesInRadius(
            Level level, BlockPos pos, double radius, Class<T> entityClass) {
        
        if (level == null || pos == null) {
            return Collections.emptyList();
        }
        
        net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius
        );
        
        if (entityClass != null) {
            return level.getEntitiesOfClass(entityClass, aabb, e -> 
                BlockHelper.getDistance(pos, e.blockPosition()) <= radius);
        } else {
            @SuppressWarnings("unchecked")
            List<T> entities = (List<T>) level.getEntities((Entity) null, aabb, e -> 
                BlockHelper.getDistance(pos, e.blockPosition()) <= radius);
            return entities;
        }
    }
    
    /**
     * Checks if a structure exists near a position.
     * 
     * @param level The level
     * @param pos The position
     * @param structureKey The structure to look for
     * @param radius The search radius
     * @return true if structure is found
     */
    public static boolean hasStructureNearby(ServerLevel level, BlockPos pos, 
                                            ResourceKey<Structure> structureKey, int radius) {
        if (level == null || pos == null || structureKey == null) {
            return false;
        }
        
        // Simplified implementation - structure detection requires specific API access
        // In a full implementation, would use structure manager with proper API
        return false;
    }
    
    /**
     * Gets the time of day in ticks (0-24000).
     * 
     * @param level The level
     * @return Time of day in ticks
     */
    public static long getTimeOfDay(Level level) {
        if (level == null) {
            return 0;
        }
        return level.getDayTime() % 24000;
    }
    
    /**
     * Checks if it's day time.
     * 
     * @param level The level
     * @return true if it's day (6000-18000 ticks)
     */
    public static boolean isDayTime(Level level) {
        long time = getTimeOfDay(level);
        return time >= 6000 && time < 18000;
    }
    
    /**
     * Checks if it's night time.
     * 
     * @param level The level
     * @return true if it's night
     */
    public static boolean isNightTime(Level level) {
        return !isDayTime(level);
    }
    
    /**
     * Gets the weather state of the level.
     * 
     * @param level The level
     * @return Weather info (raining, thundering)
     */
    public static WeatherInfo getWeather(Level level) {
        if (level == null) {
            return new WeatherInfo(false, false);
        }
        return new WeatherInfo(level.isRaining(), level.isThundering());
    }
    
    /**
     * Represents weather information.
     */
    public record WeatherInfo(boolean raining, boolean thundering) {}
}

