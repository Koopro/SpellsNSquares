package at.koopro.spells_n_squares.features.environment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Represents an active aurora event.
 */
public class AuroraEvent {
    private int remainingTicks;
    private final net.minecraft.util.RandomSource random;
    
    // Aurora duration in ticks (5 minutes)
    private static final int AURORA_DURATION = 6000;
    
    public AuroraEvent(net.minecraft.util.RandomSource random) {
        this.remainingTicks = AURORA_DURATION;
        this.random = random;
    }
    
    /**
     * Ticks the aurora event and spawns particles.
     * @return true if aurora is still active, false if it ended
     */
    public boolean tick(ServerLevel level) {
        if (remainingTicks <= 0) {
            return false;
        }
        
        remainingTicks--;
        
        // Spawn aurora particles every 5 ticks
        if (remainingTicks % 5 == 0) {
            spawnAuroraParticles(level);
        }
        
        return true;
    }
    
    /**
     * Spawns aurora particles in the sky.
     */
    private void spawnAuroraParticles(ServerLevel level) {
        // Spawn particles in a wide arc across the sky
        int particleCount = 50;
        // Use first player's position or world origin
        double centerX = 0.0;
        double centerZ = 0.0;
        if (!level.players().isEmpty()) {
            Player firstPlayer = level.players().get(0);
            centerX = firstPlayer.getX();
            centerZ = firstPlayer.getZ();
        }
        double y = 200.0; // High in the sky
        
        for (int i = 0; i < particleCount; i++) {
            double angle = (i / (double) particleCount) * Math.PI * 2;
            double radius = 50.0 + random.nextDouble() * 30.0;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            double offsetY = Math.sin(angle * 2) * 10.0; // Wave pattern
            
            // Use end rod particles with custom colors (Minecraft doesn't support colored particles directly,
            // but we can use different particle types for variety)
            net.minecraft.core.particles.ParticleOptions particle;
            int particleType = random.nextInt(4);
            switch (particleType) {
                case 0 -> particle = ParticleTypes.END_ROD;
                case 1 -> particle = ParticleTypes.ELECTRIC_SPARK;
                case 2 -> particle = ParticleTypes.WAX_ON;
                default -> particle = ParticleTypes.GLOW;
            }
            
            level.sendParticles(particle, x, y + offsetY, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}








