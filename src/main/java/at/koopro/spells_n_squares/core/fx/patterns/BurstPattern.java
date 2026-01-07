package at.koopro.spells_n_squares.core.fx.patterns;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Pattern for creating burst/explosion particle effects.
 */
public class BurstPattern {
    private final Vec3 center;
    private ParticleOptions particle;
    private int particleCount;
    private double radius;
    private double speed;
    
    private BurstPattern(Builder builder) {
        this.center = builder.center;
        this.particle = builder.particle != null ? builder.particle : ParticleTypes.EXPLOSION;
        this.particleCount = builder.particleCount;
        this.radius = builder.radius;
        this.speed = builder.speed;
    }
    
    /**
     * Plays this burst pattern.
     */
    public void play(ServerLevel level) {
        if (level == null || center == null) {
            return;
        }
        
        Random random = new Random();
        
        for (int i = 0; i < particleCount; i++) {
            // Random direction
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = random.nextDouble() * Math.PI;
            double r = random.nextDouble() * radius;
            
            Vec3 offset = new Vec3(
                r * Math.sin(phi) * Math.cos(theta),
                r * Math.cos(phi),
                r * Math.sin(phi) * Math.sin(theta)
            );
            
            Vec3 pos = center.add(offset);
            Vec3 velocity = offset.normalize().scale(speed);
            
            ParticlePool.queueParticle(level, particle, pos, 1, 
                velocity.x, velocity.y, velocity.z, 0.0);
        }
    }
    
    public static class Builder {
        private Vec3 center;
        private ParticleOptions particle;
        private int particleCount = 15;
        private double radius = 0.5;
        private double speed = 0.1;
        
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
        
        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }
        
        public BurstPattern build() {
            if (center == null) {
                throw new IllegalStateException("center must be set");
            }
            return new BurstPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

