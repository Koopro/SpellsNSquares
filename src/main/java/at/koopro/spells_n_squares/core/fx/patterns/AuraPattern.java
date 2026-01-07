package at.koopro.spells_n_squares.core.fx.patterns;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Pattern for creating continuous aura effects around entities or positions.
 */
public class AuraPattern {
    private final Vec3 center;
    private final Entity entity;
    private ParticleOptions particle;
    private int particleCount;
    private double radius;
    private double height;
    private boolean followEntity;
    
    private AuraPattern(Builder builder) {
        this.center = builder.center;
        this.entity = builder.entity;
        this.particle = builder.particle != null ? builder.particle : ParticleTypes.ELECTRIC_SPARK;
        this.particleCount = builder.particleCount;
        this.radius = builder.radius;
        this.height = builder.height;
        this.followEntity = builder.followEntity;
    }
    
    /**
     * Plays this aura pattern.
     */
    public void play(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        Vec3 pos = center;
        if (entity != null && followEntity) {
            pos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        }
        
        if (pos == null) {
            return;
        }
        
        Random random = new Random();
        
        for (int i = 0; i < particleCount; i++) {
            // Random position in sphere/cylinder
            double theta = random.nextDouble() * 2 * Math.PI;
            double r = random.nextDouble() * radius;
            double y = (random.nextDouble() - 0.5) * height;
            
            Vec3 offset = new Vec3(
                r * Math.cos(theta),
                y,
                r * Math.sin(theta)
            );
            
            Vec3 particlePos = pos.add(offset);
            
            ParticlePool.queueParticle(level, particle, particlePos, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    public static class Builder {
        private Vec3 center;
        private Entity entity;
        private ParticleOptions particle;
        private int particleCount = 5;
        private double radius = 1.0;
        private double height = 2.0;
        private boolean followEntity = false;
        
        public Builder center(Vec3 center) {
            this.center = center;
            return this;
        }
        
        public Builder entity(Entity entity) {
            this.entity = entity;
            this.followEntity = true;
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
        
        public Builder height(double height) {
            this.height = height;
            return this;
        }
        
        public AuraPattern build() {
            if (center == null && entity == null) {
                throw new IllegalStateException("center or entity must be set");
            }
            return new AuraPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

