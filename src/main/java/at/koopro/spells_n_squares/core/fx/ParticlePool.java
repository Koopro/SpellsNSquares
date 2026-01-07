package at.koopro.spells_n_squares.core.fx;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Enhanced particle pooling system for performance optimization.
 * Tracks and batches particle sends to reduce overhead.
 * Includes per-chunk particle limits, distance-based culling, and off-screen culling.
 */
public final class ParticlePool {
    private ParticlePool() {
    }
    
    /**
     * Maximum particles per chunk per tick to prevent particle spam.
     */
    private static final int MAX_PARTICLES_PER_CHUNK = 200;
    
    /**
     * Maximum distance for particle rendering (blocks).
     * Particles beyond this distance are culled.
     */
    private static final double MAX_PARTICLE_DISTANCE = 64.0;
    
    /**
     * Maximum distance squared for efficient distance checks.
     * Used as fallback when config is not available.
     */
    private static final double MAX_PARTICLE_DISTANCE_SQ = MAX_PARTICLE_DISTANCE * MAX_PARTICLE_DISTANCE;
    
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
    private static final java.util.Map<ServerLevel, List<ParticleRequest>> requestQueues = CollectionFactory.createMap();
    
    // Per-chunk particle count tracking (resets each tick)
    private static final java.util.Map<ServerLevel, java.util.Map<BlockPos, Integer>> chunkParticleCounts = CollectionFactory.createMap();
    
    /**
     * Queues a particle for batch sending with per-chunk limits and distance-based culling.
     *
     * @param level The server level
     * @param particle The particle type
     * @param position The position to spawn at
     * @param count The number of particles
     * @param spreadX X spread
     * @param spreadY Y spread
     * @param spreadZ Z spread
     * @param speed Particle speed
     */
    public static void queueParticle(ServerLevel level, ParticleOptions particle, Vec3 position,
                                     int count, double spreadX, double spreadY, double spreadZ, double speed) {
        if (level == null || particle == null || position == null) {
            return;
        }
        
        // Early exit if no players in level
        List<ServerPlayer> players = level.players();
        
        if (players.isEmpty()) {
            return; // No players to see particles
        }
        
        // Distance-based culling: Check if any nearby player is within range
        // Use squared distances to avoid expensive sqrt calculations
        boolean shouldRender = false;
        double minDistanceSq = Double.MAX_VALUE;
        double maxDistance = Config.getMaxParticleDistance();
        double maxDistanceSq = maxDistance * maxDistance;
        
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer == null) {
                continue;
            }
            Vec3 playerPos = serverPlayer.position();
            double distanceSq = position.distanceToSqr(playerPos);
            minDistanceSq = Math.min(minDistanceSq, distanceSq);
            
            if (distanceSq <= maxDistanceSq) {
                shouldRender = true;
                break; // At least one player is in range
            }
        }
        
        // If no players in range, cull the particle
        if (!shouldRender) {
            return;
        }
        
        // Distance-based LOD: Reduce particle count at distance using FXConfigHelper
        if (minDistanceSq < Double.MAX_VALUE) {
            double distance = Math.sqrt(minDistanceSq);
            // Use FXConfigHelper for LOD calculations if a player is available
            ServerPlayer nearestPlayer = null;
            double nearestDistSq = Double.MAX_VALUE;
            for (ServerPlayer player : players) {
                if (player != null) {
                    double distSq = position.distanceToSqr(player.position());
                    if (distSq < nearestDistSq) {
                        nearestDistSq = distSq;
                        nearestPlayer = player;
                    }
                }
            }
            
            if (nearestPlayer != null) {
                // Use FXConfigHelper for distance-based LOD
                double distanceMultiplier = FXConfigHelper.getDistanceMultiplier(nearestPlayer, position);
                double qualityMultiplier = FXConfigHelper.getEffectiveParticleMultiplier();
                count = (int) Math.max(1, Math.round(count * qualityMultiplier * distanceMultiplier));
            } else {
                // Fallback: simple distance-based reduction
                if (distance > maxDistance * 0.5) {
                    double distanceRatio = (distance - maxDistance * 0.5) / (maxDistance * 0.5);
                    count = (int) Math.max(1, count * (1.0 - distanceRatio * 0.7)); // Up to 70% reduction
                }
            }
        } else {
            // Apply quality multiplier even if no distance info
            double qualityMultiplier = FXConfigHelper.getEffectiveParticleMultiplier();
            count = (int) Math.max(1, Math.round(count * qualityMultiplier));
        }
        
        // Calculate chunk position for limiting (cached calculation)
        BlockPos chunkPos = new BlockPos(
            (int) position.x >> 4,
            (int) position.y >> 4,
            (int) position.z >> 4
        );
        
        // Check per-chunk limit
        java.util.Map<BlockPos, Integer> chunkCounts = chunkParticleCounts.computeIfAbsent(level, k -> CollectionFactory.createMap());
        int currentCount = chunkCounts.getOrDefault(chunkPos, 0);
        if (currentCount >= MAX_PARTICLES_PER_CHUNK) {
            // Chunk limit reached, reduce particle count proportionally
            int remaining = MAX_PARTICLES_PER_CHUNK - currentCount;
            if (remaining <= 0) {
                return; // Skip this particle entirely
            }
            count = Math.min(count, remaining);
        }
        
        // Update chunk count
        chunkCounts.put(chunkPos, currentCount + count);
        
        requestQueues.computeIfAbsent(level, k -> CollectionFactory.createList()).add(
            new ParticleRequest(particle, position, count, spreadX, spreadY, spreadZ, speed)
        );
    }

    /**
     * Convenience overload for common case where spread is uniform.
     *
     * @param level   The server level
     * @param particle The particle type
     * @param x       X position
     * @param y       Y position
     * @param z       Z position
     * @param count   Number of particles
     * @param spread  Uniform spread for X/Y/Z
     * @param speed   Particle speed
     */
    public static void queueParticle(ServerLevel level, ParticleOptions particle,
                                     double x, double y, double z,
                                     int count, double spread, double speed) {
        queueParticle(level, particle, new Vec3(x, y, z), count, spread, spread, spread, speed);
    }
    
    /**
     * Flushes all queued particles for a level.
     * Call this at the end of each tick.
     * Also resets per-chunk particle counts.
     */
    public static void flush(ServerLevel level) {
        List<ParticleRequest> queue = requestQueues.get(level);
        if (queue == null || queue.isEmpty()) {
            // Still reset chunk counts even if no particles
            java.util.Map<BlockPos, Integer> chunkCounts = chunkParticleCounts.get(level);
            if (chunkCounts != null) {
                chunkCounts.clear();
            }
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
        
        // Reset per-chunk counts for next tick
        java.util.Map<BlockPos, Integer> chunkCounts = chunkParticleCounts.get(level);
        if (chunkCounts != null) {
            chunkCounts.clear();
        }
    }
    
    /**
     * Flushes all levels (called on server tick).
     * Uses a copy of the key set to avoid concurrent modification issues.
     * Also cleans up unloaded levels to prevent memory leaks.
     */
    public static void flushAll() {
        // Create a copy to avoid concurrent modification if levels are unloaded during iteration
        java.util.Set<ServerLevel> levels = CollectionFactory.createSetFrom(requestQueues.keySet());
        java.util.Set<ServerLevel> levelsToRemove = CollectionFactory.createSet();
        
        for (ServerLevel level : levels) {
            if (level == null || level.isClientSide()) {
                levelsToRemove.add(level);
                continue;
            }
            
            // Check if level is still valid (not unloaded)
            try {
                if (level.getServer() == null) {
                    levelsToRemove.add(level);
                    continue;
                }
            } catch (Exception e) {
                // Level is unloaded, mark for removal
                levelsToRemove.add(level);
                continue;
            }
            
            flush(level);
        }
        
        // Clean up unloaded levels to prevent memory leaks
        for (ServerLevel level : levelsToRemove) {
            requestQueues.remove(level);
            chunkParticleCounts.remove(level);
        }
    }
    
    /**
     * Clears all queues (called on server shutdown).
     */
    public static void clear() {
        requestQueues.clear();
        chunkParticleCounts.clear();
    }
    
    /**
     * Cleans up data for a specific level (called when level is unloaded).
     * Prevents memory leaks from unloaded levels.
     * 
     * @param level The level to clean up
     */
    public static void cleanupLevel(ServerLevel level) {
        if (level != null) {
            requestQueues.remove(level);
            chunkParticleCounts.remove(level);
        }
    }
    
    /**
     * Gets the maximum particles allowed per chunk per tick.
     * 
     * @return The maximum particle count per chunk
     */
    public static int getMaxParticlesPerChunk() {
        return MAX_PARTICLES_PER_CHUNK;
    }
    
    /**
     * Queues a custom particle type for batch sending.
     * Custom particles are handled the same way as vanilla particles.
     *
     * @param level The server level
     * @param particle The custom particle type (must implement ParticleOptions)
     * @param position The position to spawn at
     * @param count The number of particles
     * @param spreadX X spread
     * @param spreadY Y spread
     * @param spreadZ Z spread
     * @param speed Particle speed
     */
    public static void queueCustomParticle(ServerLevel level, ParticleOptions particle, Vec3 position,
                                          int count, double spreadX, double spreadY, double spreadZ, double speed) {
        // Custom particles use the same queueing system as vanilla particles
        queueParticle(level, particle, position, count, spreadX, spreadY, spreadZ, speed);
    }
    
    /**
     * Queues multiple particles as a batch for efficient network transmission.
     * All particles in the batch are sent together.
     *
     * @param level The server level
     * @param requests List of particle requests to batch together
     */
    public static void queueEffectBatch(ServerLevel level, List<ParticleRequest> requests) {
        if (level == null || requests == null || requests.isEmpty()) {
            return;
        }
        
        // Add all requests to the queue (they'll be flushed together)
        List<ParticleRequest> queue = requestQueues.computeIfAbsent(level, k -> CollectionFactory.createList());
        queue.addAll(requests);
    }
    
    /**
     * Flushes critical particles immediately, bypassing normal batching.
     * Use sparingly for effects that need immediate visual feedback.
     *
     * @param level The server level
     * @param particle The particle type
     * @param position The position
     * @param count The number of particles
     * @param spreadX X spread
     * @param spreadY Y spread
     * @param spreadZ Z spread
     * @param speed Particle speed
     */
    public static void flushWithPriority(ServerLevel level, ParticleOptions particle, Vec3 position,
                                         int count, double spreadX, double spreadY, double spreadZ, double speed) {
        if (level == null || particle == null || position == null) {
            return;
        }
        
        // Send immediately without queuing
        level.sendParticles(particle, position.x, position.y, position.z, count, spreadX, spreadY, spreadZ, speed);
    }
}
