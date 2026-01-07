package at.koopro.spells_n_squares.core.util.rendering;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for advanced particle effects and patterns.
 * Provides particle spawning in various patterns (circle, line, sphere, spiral, etc.).
 */
public final class ParticleUtils {
    private ParticleUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Spawns particles in a circle pattern.
     * 
     * @param level The server level
     * @param center The center position
     * @param radius The radius of the circle
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleCircle(ServerLevel level, Vec3 center, double radius, int count, ParticleOptions particle) {
        if (level == null || center == null || radius <= 0 || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            double angle = (2.0 * Math.PI * i) / count;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            ParticlePool.queueParticle(level, particle, new Vec3(x, center.y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles along a line between two points.
     * 
     * @param level The server level
     * @param start The start position
     * @param end The end position
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleLine(ServerLevel level, Vec3 start, Vec3 end, int count, ParticleOptions particle) {
        if (level == null || start == null || end == null || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            float t = (float) i / (count - 1);
            double x = MathUtils.lerp(start.x, end.x, t);
            double y = MathUtils.lerp(start.y, end.y, t);
            double z = MathUtils.lerp(start.z, end.z, t);
            ParticlePool.queueParticle(level, particle, new Vec3(x, y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles in a sphere pattern.
     * 
     * @param level The server level
     * @param center The center position
     * @param radius The radius of the sphere
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleSphere(ServerLevel level, Vec3 center, double radius, int count, ParticleOptions particle) {
        if (level == null || center == null || radius <= 0 || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            // Generate random point on sphere surface
            double theta = Math.random() * 2.0 * Math.PI;
            double phi = Math.acos(2.0 * Math.random() - 1.0);
            
            double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
            double y = center.y + radius * Math.sin(phi) * Math.sin(theta);
            double z = center.z + radius * Math.cos(phi);
            
            ParticlePool.queueParticle(level, particle, new Vec3(x, y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles in a spiral pattern.
     * 
     * @param level The server level
     * @param center The center position
     * @param radius The radius of the spiral
     * @param height The height of the spiral
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleSpiral(ServerLevel level, Vec3 center, double radius, double height, int count, ParticleOptions particle) {
        if (level == null || center == null || radius <= 0 || height <= 0 || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            double angle = t * 4.0 * Math.PI; // 2 full rotations
            double r = radius * t; // Radius increases with t
            double y = center.y + height * t; // Height increases with t
            
            double x = center.x + r * Math.cos(angle);
            double z = center.z + r * Math.sin(angle);
            
            ParticlePool.queueParticle(level, particle, new Vec3(x, y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles in a burst pattern (explosion-like).
     * 
     * @param level The server level
     * @param center The center position
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleBurst(ServerLevel level, Vec3 center, int count, ParticleOptions particle) {
        if (level == null || center == null || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            // For pooled particles we approximate the burst with position-based spawning;
            // velocity variation is handled on the client side by the particle system.
            ParticlePool.queueParticle(level, particle, center,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles in a beam pattern between two points.
     * 
     * @param level The server level
     * @param start The start position
     * @param end The end position
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleBeam(ServerLevel level, Vec3 start, Vec3 end, int count, ParticleOptions particle) {
        if (level == null || start == null || end == null || count <= 0 || particle == null) {
            return;
        }
        
        // Spawn particles along the line with some spread
        for (int i = 0; i < count; i++) {
            float t = (float) i / (count - 1);
            double x = MathUtils.lerp(start.x, end.x, t);
            double y = MathUtils.lerp(start.y, end.y, t);
            double z = MathUtils.lerp(start.z, end.z, t);
            
            // Add small random offset for beam effect
            double spread = 0.1;
            x += (Math.random() - 0.5) * spread;
            y += (Math.random() - 0.5) * spread;
            z += (Math.random() - 0.5) * spread;
            
            ParticlePool.queueParticle(level, particle, new Vec3(x, y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns particles in a trail following an entity.
     * 
     * @param level The server level
     * @param entity The entity to follow
     * @param count The number of particles per spawn
     * @param particle The particle type
     */
    public static void spawnParticleTrail(ServerLevel level, Entity entity, int count, ParticleOptions particle) {
        if (level == null || entity == null || count <= 0 || particle == null) {
            return;
        }
        
        Vec3 pos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        Vec3 motion = entity.getDeltaMovement();
        
        // Spawn particles behind the entity
        double offsetX = -motion.x * 0.5;
        double offsetY = -motion.y * 0.5;
        double offsetZ = -motion.z * 0.5;

        ParticlePool.queueParticle(level, particle,
            new Vec3(pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ),
            count, 0.2, 0.2, 0.2, 0.01);
    }
    
    /**
     * Spawns particles in a ring pattern (horizontal circle).
     * 
     * @param level The server level
     * @param center The center position
     * @param radius The radius of the ring
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleRing(ServerLevel level, Vec3 center, double radius, int count, ParticleOptions particle) {
        spawnParticleCircle(level, center, radius, count, particle);
    }
    
    /**
     * Spawns particles in a vertical circle pattern.
     * 
     * @param level The server level
     * @param center The center position
     * @param radius The radius of the circle
     * @param count The number of particles
     * @param particle The particle type
     */
    public static void spawnParticleVerticalCircle(ServerLevel level, Vec3 center, double radius, int count, ParticleOptions particle) {
        if (level == null || center == null || radius <= 0 || count <= 0 || particle == null) {
            return;
        }
        
        for (int i = 0; i < count; i++) {
            double angle = (2.0 * Math.PI * i) / count;
            double y = center.y + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            ParticlePool.queueParticle(level, particle, new Vec3(center.x, y, z),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}


