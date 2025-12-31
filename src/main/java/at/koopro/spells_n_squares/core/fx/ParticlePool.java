package at.koopro.spells_n_squares.core.fx;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple particle pooling system for performance optimization.
 * Tracks and batches particle sends to reduce overhead.
 */
public final class ParticlePool {
    private ParticlePool() {
    }
    
    /**
     * Batched particle send request.
     */
    private static class ParticleRequest {
        final ParticleOptions particle;
        final Vec3 position;
        final int count;
        final double spreadX, spreadY, spreadZ;
        final double speed;
        
        ParticleRequest(ParticleOptions particle, Vec3 position, int count,
                       double spreadX, double spreadY, double spreadZ, double speed) {
            this.particle = particle;
            this.position = position;
            this.count = count;
            this.spreadX = spreadX;
            this.spreadY = spreadY;
            this.spreadZ = spreadZ;
            this.speed = speed;
        }
    }
    
    // Per-level particle request queues
    private static final java.util.Map<ServerLevel, List<ParticleRequest>> requestQueues = new HashMap<>();
    
    /**
     * Queues a particle for batch sending.
     */
    public static void queueParticle(ServerLevel level, ParticleOptions particle, Vec3 position,
                                    int count, double spreadX, double spreadY, double spreadZ, double speed) {
        if (level == null || particle == null || position == null) {
            return;
        }
        
        requestQueues.computeIfAbsent(level, k -> new ArrayList<>()).add(
            new ParticleRequest(particle, position, count, spreadX, spreadY, spreadZ, speed)
        );
    }
    
    /**
     * Flushes all queued particles for a level.
     * Call this at the end of each tick.
     */
    public static void flush(ServerLevel level) {
        List<ParticleRequest> queue = requestQueues.get(level);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        
        // Send all queued particles
        for (ParticleRequest request : queue) {
            level.sendParticles(
                request.particle,
                request.position.x, request.position.y, request.position.z,
                request.count,
                request.spreadX, request.spreadY, request.spreadZ,
                request.speed
            );
        }
        
        queue.clear();
    }
    
    /**
     * Flushes all levels (called on server tick).
     * Uses a copy of the key set to avoid concurrent modification issues.
     */
    public static void flushAll() {
        // Create a copy to avoid concurrent modification if levels are unloaded during iteration
        java.util.Set<ServerLevel> levels = new java.util.HashSet<>(requestQueues.keySet());
        for (ServerLevel level : levels) {
            if (level != null && !level.isClientSide()) {
                flush(level);
            }
        }
    }
    
    /**
     * Clears all queues (called on server shutdown).
     */
    public static void clear() {
        requestQueues.clear();
    }
}
