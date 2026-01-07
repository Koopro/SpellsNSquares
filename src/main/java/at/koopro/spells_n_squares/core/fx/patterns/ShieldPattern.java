package at.koopro.spells_n_squares.core.fx.patterns;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Pattern for creating shield effects (Protego, impact rings, etc.).
 */
public class ShieldPattern {
    private final Vec3 center;
    private ParticleOptions particle;
    private int particleCount;
    private double radius;
    private boolean hexagon;
    private boolean impactRing;
    
    private ShieldPattern(Builder builder) {
        this.center = builder.center;
        this.particle = builder.particle != null ? builder.particle : ParticleTypes.ENCHANT;
        this.particleCount = builder.particleCount;
        this.radius = builder.radius;
        this.hexagon = builder.hexagon;
        this.impactRing = builder.impactRing;
    }
    
    /**
     * Plays this shield pattern.
     */
    public void play(ServerLevel level) {
        if (level == null || center == null) {
            return;
        }
        
        Random random = new Random();
        
        if (hexagon) {
            // Create hexagonal pattern
            for (int i = 0; i < 6; i++) {
                double angle = (i * Math.PI) / 3.0;
                Vec3 point = center.add(
                    radius * Math.cos(angle),
                    0,
                    radius * Math.sin(angle)
                );
                ParticlePool.queueParticle(level, particle, point, particleCount / 6, 0.1, 0.1, 0.1, 0.05);
            }
        } else if (impactRing) {
            // Create expanding ring
            for (int i = 0; i < particleCount; i++) {
                double angle = (i * 2 * Math.PI) / particleCount;
                Vec3 point = center.add(
                    radius * Math.cos(angle),
                    0,
                    radius * Math.sin(angle)
                );
                ParticlePool.queueParticle(level, particle, point, 1, 0.0, 0.0, 0.0, 0.0);
            }
        } else {
            // Default: sphere around center
            for (int i = 0; i < particleCount; i++) {
                double theta = random.nextDouble() * 2 * Math.PI;
                double phi = random.nextDouble() * Math.PI;
                double r = radius;
                
                Vec3 offset = new Vec3(
                    r * Math.sin(phi) * Math.cos(theta),
                    r * Math.cos(phi),
                    r * Math.sin(phi) * Math.sin(theta)
                );
                
                Vec3 pos = center.add(offset);
                ParticlePool.queueParticle(level, particle, pos, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
    
    public static class Builder {
        private Vec3 center;
        private ParticleOptions particle;
        private int particleCount = 20;
        private double radius = 1.5;
        private boolean hexagon = false;
        private boolean impactRing = false;
        
        public Builder center(Vec3 center) {
            this.center = center;
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
        
        public Builder radius(double radius) {
            this.radius = radius;
            return this;
        }
        
        public Builder hexagon(boolean hexagon) {
            this.hexagon = hexagon;
            return this;
        }
        
        public Builder impactRing(boolean impactRing) {
            this.impactRing = impactRing;
            return this;
        }
        
        public ShieldPattern build() {
            if (center == null) {
                throw new IllegalStateException("center must be set");
            }
            return new ShieldPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

