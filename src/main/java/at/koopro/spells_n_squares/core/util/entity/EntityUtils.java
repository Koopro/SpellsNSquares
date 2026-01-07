package at.koopro.spells_n_squares.core.util.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for common entity operations.
 * Provides entity type checking, distance calculations, visibility checks, filtering, and more.
 */
public final class EntityUtils {
    private EntityUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if an entity is a hostile mob.
     * 
     * @param entity The entity to check
     * @return true if hostile
     */
    public static boolean isHostile(Entity entity) {
        if (entity == null) {
            return false;
        }
        // Check if entity is a hostile mob by checking if it can attack
        if (entity instanceof LivingEntity living) {
            // Hostile mobs typically have attack goals or are enemies of players
            return living instanceof net.minecraft.world.entity.monster.Monster;
        }
        return false;
    }
    
    /**
     * Checks if an entity is a player.
     * 
     * @param entity The entity to check
     * @return true if player
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof Player;
    }
    
    /**
     * Checks if an entity is a living entity.
     * 
     * @param entity The entity to check
     * @return true if living
     */
    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity;
    }
    
    /**
     * Calculates the distance between two entities.
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return The distance, or Double.MAX_VALUE if either entity is null
     */
    public static double distance(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null) {
            return Double.MAX_VALUE;
        }
        return entity1.distanceTo(entity2);
    }
    
    /**
     * Calculates the squared distance between two entities (faster, no sqrt).
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return The squared distance, or Double.MAX_VALUE if either entity is null
     */
    public static double distanceSquared(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null) {
            return Double.MAX_VALUE;
        }
        return entity1.distanceToSqr(entity2);
    }
    
    /**
     * Calculates the distance from an entity to a position.
     * 
     * @param entity The entity
     * @param pos The position
     * @return The distance, or Double.MAX_VALUE if entity is null
     */
    public static double distanceTo(Entity entity, Vec3 pos) {
        if (entity == null || pos == null) {
            return Double.MAX_VALUE;
        }
        return entity.position().distanceTo(pos);
    }
    
    /**
     * Calculates the distance from an entity to a block position.
     * 
     * @param entity The entity
     * @param pos The block position
     * @return The distance, or Double.MAX_VALUE if entity is null
     */
    public static double distanceTo(Entity entity, BlockPos pos) {
        if (entity == null || pos == null) {
            return Double.MAX_VALUE;
        }
        return Math.sqrt(entity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
    
    /**
     * Gets all entities within a range of a center position.
     * 
     * @param level The level
     * @param center The center position
     * @param radius The radius
     * @return List of entities within range
     */
    public static List<Entity> getEntitiesInRange(Level level, Vec3 center, double radius) {
        if (level == null || center == null || radius <= 0) {
            return List.of();
        }
        AABB aabb = new AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius
        );
        return level.getEntitiesOfClass(Entity.class, aabb);
    }
    
    /**
     * Gets all entities within a range of a center entity.
     * 
     * @param level The level
     * @param center The center entity
     * @param radius The radius
     * @return List of entities within range (excluding the center entity)
     */
    public static List<Entity> getEntitiesInRange(Level level, Entity center, double radius) {
        if (level == null || center == null || radius <= 0) {
            return List.of();
        }
        List<Entity> entities = getEntitiesInRange(level, center.position(), radius);
        entities.remove(center);
        return entities;
    }
    
    /**
     * Gets all entities within a range matching a predicate.
     * 
     * @param level The level
     * @param center The center position
     * @param radius The radius
     * @param predicate The filter predicate
     * @return List of matching entities within range
     */
    public static <T extends Entity> List<T> getEntitiesInRange(Level level, Vec3 center, double radius, Class<T> entityClass, Predicate<T> predicate) {
        if (level == null || center == null || radius <= 0 || entityClass == null) {
            return List.of();
        }
        AABB aabb = new AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius
        );
        List<T> entities = level.getEntitiesOfClass(entityClass, aabb);
        if (predicate != null) {
            return entities.stream()
                .filter(predicate)
                .toList();
        }
        return entities;
    }
    
    /**
     * Checks if an entity can see another entity (line of sight).
     * 
     * @param level The level
     * @param viewer The viewing entity
     * @param target The target entity
     * @return true if viewer can see target
     */
    public static boolean canSee(Level level, Entity viewer, Entity target) {
        if (level == null || viewer == null || target == null) {
            return false;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        Vec3 viewerPos = viewer.getEyePosition();
        Vec3 targetPos = target.getEyePosition();
        return serverLevel.clip(new net.minecraft.world.level.ClipContext(
            viewerPos, targetPos,
            net.minecraft.world.level.ClipContext.Block.COLLIDER,
            net.minecraft.world.level.ClipContext.Fluid.NONE,
            viewer
        )).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }
    
    /**
     * Checks if an entity is within a certain distance of another entity.
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @param maxDistance The maximum distance
     * @return true if within range
     */
    public static boolean isWithinRange(Entity entity1, Entity entity2, double maxDistance) {
        if (entity1 == null || entity2 == null) {
            return false;
        }
        return distanceSquared(entity1, entity2) <= maxDistance * maxDistance;
    }
    
    /**
     * Checks if an entity is within a certain distance of a position.
     * 
     * @param entity The entity
     * @param pos The position
     * @param maxDistance The maximum distance
     * @return true if within range
     */
    public static boolean isWithinRange(Entity entity, Vec3 pos, double maxDistance) {
        if (entity == null || pos == null) {
            return false;
        }
        return distanceSquared(entity, pos) <= maxDistance * maxDistance;
    }
    
    /**
     * Calculates the squared distance from an entity to a position (faster, no sqrt).
     * 
     * @param entity The entity
     * @param pos The position
     * @return The squared distance, or Double.MAX_VALUE if entity is null
     */
    public static double distanceSquared(Entity entity, Vec3 pos) {
        if (entity == null || pos == null) {
            return Double.MAX_VALUE;
        }
        return entity.position().distanceToSqr(pos);
    }
    
    /**
     * Filters entities based on a predicate.
     * 
     * @param entities The entities to filter
     * @param predicate The filter predicate
     * @return List of matching entities
     */
    public static <T extends Entity> List<T> filter(List<T> entities, Predicate<T> predicate) {
        if (entities == null || predicate == null) {
            return List.of();
        }
        return entities.stream()
            .filter(predicate)
            .toList();
    }
}


