package at.koopro.spells_n_squares.core.fx.patterns;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Pattern for creating teleportation effects (Apparition, Portkey, portals).
 */
public class TeleportPattern {
    private final Vec3 origin;
    private final Vec3 destination;
    private ParticleOptions particle;
    private int particleCount;
    private boolean createTrail;
    private boolean createRings;
    
    private TeleportPattern(Builder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.particle = builder.particle != null ? builder.particle : ParticleTypes.PORTAL;
        this.particleCount = builder.particleCount;
        this.createTrail = builder.createTrail;
        this.createRings = builder.createRings;
    }
    
    /**
     * Plays this teleport pattern.
     */
    public void play(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        Random random = new Random();
        
        // Create rings at origin and destination
        if (createRings) {
            if (origin != null) {
                createSwirlingRing(level, origin, particleCount / 2);
            }
            if (destination != null) {
                createSwirlingRing(level, destination, particleCount / 2);
            }
        }
        
        // Create trail between origin and destination
        if (createTrail && origin != null && destination != null) {
            Vec3 direction = destination.subtract(origin);
            double distance = direction.length();
            Vec3 normalized = direction.normalize();
            
            int trailParticles = particleCount;
            for (int i = 0; i < trailParticles; i++) {
                double t = (double) i / trailParticles;
                Vec3 pos = origin.add(normalized.scale(distance * t));
                
                // Add slight random offset for trail effect
                Vec3 offset = new Vec3(
                    (random.nextDouble() - 0.5) * 0.2,
                    (random.nextDouble() - 0.5) * 0.2,
                    (random.nextDouble() - 0.5) * 0.2
                );
                
                ParticlePool.queueParticle(level, particle, pos.add(offset), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
    
    private void createSwirlingRing(ServerLevel level, Vec3 center, int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            double angle = (i * 2 * Math.PI) / count;
            double radius = 0.5 + random.nextDouble() * 0.5;
            
            Vec3 point = center.add(
                radius * Math.cos(angle),
                (random.nextDouble() - 0.5) * 0.5,
                radius * Math.sin(angle)
            );
            
            ParticlePool.queueParticle(level, particle, point, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    public static class Builder {
        private Vec3 origin;
        private Vec3 destination;
        private ParticleOptions particle;
        private int particleCount = 30;
        private boolean createTrail = true;
        private boolean createRings = true;
        
        public Builder origin(Vec3 origin) {
            this.origin = origin;
            return this;
        }
        
        public Builder destination(Vec3 destination) {
            this.destination = destination;
            return this;
        }
        
        public Builder particle(ParticleOptions particle) {
            this.particle = particle;
            return this;
        }
        
        public Builder count(int count) {
            this.particleCount = count;
            return this;
        }
        
        public Builder trail(boolean createTrail) {
            this.createTrail = createTrail;
            return this;
        }
        
        public Builder rings(boolean createRings) {
            this.createRings = createRings;
            return this;
        }
        
        public TeleportPattern build() {
            return new TeleportPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

