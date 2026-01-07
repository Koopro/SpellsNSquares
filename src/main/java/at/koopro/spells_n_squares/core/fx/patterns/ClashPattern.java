package at.koopro.spells_n_squares.core.fx.patterns;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pattern for creating spell clash lightning effects.
 * Extends BeamPattern with additional clash-specific features.
 */
public class ClashPattern {
    private final Vec3 wand1Pos;
    private final Vec3 wand2Pos;
    private ParticleOptions mainParticle;
    private ParticleOptions coreParticle;
    private ParticleOptions sparkParticle;
    private int duration;
    private double intensity;
    private double branchChance;
    
    private ClashPattern(Builder builder) {
        this.wand1Pos = builder.wand1Pos;
        this.wand2Pos = builder.wand2Pos;
        this.mainParticle = builder.mainParticle != null ? builder.mainParticle : ParticleTypes.ELECTRIC_SPARK;
        this.coreParticle = builder.coreParticle != null ? builder.coreParticle : ParticleTypes.END_ROD;
        this.sparkParticle = builder.sparkParticle != null ? builder.sparkParticle : ParticleTypes.SMOKE;
        this.duration = builder.duration;
        this.intensity = builder.intensity;
        this.branchChance = builder.branchChance;
    }
    
    /**
     * Plays this clash pattern.
     */
    public void play(ServerLevel level) {
        if (level == null || wand1Pos == null || wand2Pos == null) {
            return;
        }
        
        Vec3 direction = wand2Pos.subtract(wand1Pos);
        double distance = direction.length();
        if (distance < 0.01) {
            return;
        }
        
        Vec3 normalized = direction.normalize();
        double segmentLength = 0.2;
        int segments = (int) Math.max(1, distance / segmentLength);
        Random random = new Random();
        
        List<Vec3> pathPoints = new ArrayList<>();
        pathPoints.add(wand1Pos);
        
        // Generate jagged lightning path
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            Vec3 point = wand1Pos.add(normalized.scale(distance * t));
            
            // Add random offset for jagged appearance (more intense for clashes)
            Vec3 offset = new Vec3(
                (random.nextDouble() - 0.5) * 0.3 * intensity,
                (random.nextDouble() - 0.5) * 0.3 * intensity,
                (random.nextDouble() - 0.5) * 0.3 * intensity
            );
            point = point.add(offset);
            
            pathPoints.add(point);
        }
        pathPoints.add(wand2Pos);
        
        // Spawn particles along path
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            Vec3 start = pathPoints.get(i);
            Vec3 end = pathPoints.get(i + 1);
            Vec3 segmentDir = end.subtract(start);
            double segmentDist = segmentDir.length();
            
            int particlesInSegment = (int) Math.max(1, (20 * intensity) * (segmentDist / distance));
            
            for (int j = 0; j < particlesInSegment; j++) {
                double t = (double) j / particlesInSegment;
                Vec3 pos = start.add(segmentDir.scale(t));
                
                // Main lightning particles
                ParticlePool.queueParticle(level, mainParticle, pos, 1, 0.0, 0.0, 0.0, 0.0);
                
                // Bright core particles (less frequent)
                if (j % 3 == 0) {
                    ParticlePool.queueParticle(level, coreParticle, pos, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
            
            // Spawn branches
            if (branchChance > 0 && random.nextDouble() < branchChance) {
                Vec3 branchDir = new Vec3(
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5
                ).normalize();
                Vec3 branchEnd = start.add(branchDir.scale(segmentDist * 0.3));
                
                ParticlePool.queueParticle(level, sparkParticle, branchEnd, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
        
        // Spawn impact sparks at collision point (midpoint)
        Vec3 midpoint = wand1Pos.add(wand2Pos).scale(0.5);
        ParticlePool.queueParticle(level, sparkParticle, midpoint, (int)(10 * intensity), 0.2, 0.2, 0.2, 0.1);
    }
    
    public static class Builder {
        private Vec3 wand1Pos;
        private Vec3 wand2Pos;
        private ParticleOptions mainParticle;
        private ParticleOptions coreParticle;
        private ParticleOptions sparkParticle;
        private int duration = 40;
        private double intensity = 1.0;
        private double branchChance = 0.1;
        
        public Builder between(Vec3 wand1Pos, Vec3 wand2Pos) {
            this.wand1Pos = wand1Pos;
            this.wand2Pos = wand2Pos;
            return this;
        }
        
        public Builder mainParticle(ParticleOptions particle) {
            this.mainParticle = particle;
            return this;
        }
        
        public Builder coreParticle(ParticleOptions particle) {
            this.coreParticle = particle;
            return this;
        }
        
        public Builder sparkParticle(ParticleOptions particle) {
            this.sparkParticle = particle;
            return this;
        }
        
        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }
        
        public Builder intensity(double intensity) {
            this.intensity = intensity;
            return this;
        }
        
        public Builder branchChance(double chance) {
            this.branchChance = chance;
            return this;
        }
        
        public ClashPattern build() {
            if (wand1Pos == null || wand2Pos == null) {
                throw new IllegalStateException("wand1Pos and wand2Pos must be set");
            }
            return new ClashPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

