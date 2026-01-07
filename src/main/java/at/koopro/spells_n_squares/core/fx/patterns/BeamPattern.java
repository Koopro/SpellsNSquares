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
 * Pattern for creating beam-like particle effects (lightning, trails, connections).
 */
public class BeamPattern {
    private final Vec3 from;
    private final Vec3 to;
    private ParticleOptions particle;
    private int particleCount;
    private double segmentLength;
    private double branchChance;
    private double jaggedness;
    private boolean animated;
    
    private BeamPattern(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.particle = builder.particle != null ? builder.particle : ParticleTypes.ELECTRIC_SPARK;
        this.particleCount = builder.particleCount;
        this.segmentLength = builder.segmentLength;
        this.branchChance = builder.branchChance;
        this.jaggedness = builder.jaggedness;
        this.animated = builder.animated;
    }
    
    /**
     * Plays this beam pattern.
     */
    public void play(ServerLevel level) {
        if (level == null || from == null || to == null) {
            return;
        }
        
        Vec3 direction = to.subtract(from);
        double distance = direction.length();
        if (distance < 0.01) {
            return;
        }
        
        Vec3 normalized = direction.normalize();
        int segments = (int) Math.max(1, distance / segmentLength);
        Random random = new Random();
        
        List<Vec3> pathPoints = new ArrayList<>();
        pathPoints.add(from);
        
        // Generate jagged path
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            Vec3 point = from.add(normalized.scale(distance * t));
            
            // Add random offset for jagged appearance
            if (jaggedness > 0) {
                Vec3 offset = new Vec3(
                    (random.nextDouble() - 0.5) * jaggedness,
                    (random.nextDouble() - 0.5) * jaggedness,
                    (random.nextDouble() - 0.5) * jaggedness
                );
                point = point.add(offset);
            }
            
            pathPoints.add(point);
        }
        pathPoints.add(to);
        
        // Spawn particles along path
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            Vec3 start = pathPoints.get(i);
            Vec3 end = pathPoints.get(i + 1);
            Vec3 segmentDir = end.subtract(start);
            double segmentDist = segmentDir.length();
            
            int particlesInSegment = (int) Math.max(1, particleCount * (segmentDist / distance));
            
            for (int j = 0; j < particlesInSegment; j++) {
                double t = (double) j / particlesInSegment;
                Vec3 pos = start.add(segmentDir.scale(t));
                
                ParticlePool.queueParticle(level, particle, pos, 1, 0.0, 0.0, 0.0, 0.0);
            }
            
            // Spawn branches occasionally
            if (branchChance > 0 && random.nextDouble() < branchChance) {
                Vec3 branchDir = new Vec3(
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5
                ).normalize();
                Vec3 branchEnd = start.add(branchDir.scale(segmentDist * 0.3));
                
                ParticlePool.queueParticle(level, particle, branchEnd, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }
    
    public static class Builder {
        private Vec3 from;
        private Vec3 to;
        private ParticleOptions particle;
        private int particleCount = 20;
        private double segmentLength = 0.2;
        private double branchChance = 0.1;
        private double jaggedness = 0.2;
        private boolean animated = false;
        
        public Builder from(Vec3 from) {
            this.from = from;
            return this;
        }
        
        public Builder to(Vec3 to) {
            this.to = to;
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
        
        public Builder segmentLength(double length) {
            this.segmentLength = length;
            return this;
        }
        
        public Builder branchChance(double chance) {
            this.branchChance = chance;
            return this;
        }
        
        public Builder jaggedness(double jaggedness) {
            this.jaggedness = jaggedness;
            return this;
        }
        
        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }
        
        public BeamPattern build() {
            if (from == null || to == null) {
                throw new IllegalStateException("from and to must be set");
            }
            return new BeamPattern(this);
        }
        
        public void play(ServerLevel level) {
            build().play(level);
        }
    }
}

