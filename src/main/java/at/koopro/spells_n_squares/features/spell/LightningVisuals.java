package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Reusable lightning / beam-like visual for spells.
 * Uses particles only (no custom render pipeline) and supports configurable colors.
 */
public class LightningVisuals {

    private static final Random RANDOM = new Random();

    /**
     * Spawns a jagged lightning beam between two points.
     *
     * @param level      server level to send particles in
     * @param start      start position (e.g. wand tip / eye position)
     * @param end        end position (e.g. hit position / target)
     * @param startColor ARGB color for inner/core portion (0xAARRGGBB)
     * @param endColor   ARGB color for outer/fade portion (0xAARRGGBB)
     * @param strength   visual intensity (segment count multiplier)
     */
    public static void spawnLightningBeam(
        ServerLevel level,
        Vec3 start,
        Vec3 end,
        int startColor,
        int endColor,
        float strength
    ) {
        if (level == null || start == null || end == null) {
            return;
        }

        // Clamp strength
        float s = Math.max(0.5f, Math.min(strength, 3.0f));

        Vec3 delta = end.subtract(start);
        double distance = delta.length();
        if (distance <= 0.01) {
            return;
        }

        Vec3 dir = delta.normalize();

        // Number of segments based on distance and strength
        int segments = (int) Math.max(6, distance * 6 * s);

        // Extract RGB from ARGB int (DustParticleOptions uses packed RGB)
        int rgb = startColor & 0x00FFFFFF;

        // Colored dust particle for the core beam
        DustParticleOptions dust = new DustParticleOptions(rgb, 0.5f);

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / (double) segments;

            // Base position along the line
            Vec3 base = start.add(dir.scale(t * distance));

            // Add small random sideways offsets for jagged lightning look
            double sidewaysScale = 0.15 * (1.0 + s * 0.5);
            double offX = (RANDOM.nextDouble() - 0.5) * sidewaysScale;
            double offY = (RANDOM.nextDouble() - 0.5) * sidewaysScale;
            double offZ = (RANDOM.nextDouble() - 0.5) * sidewaysScale;

            double px = base.x + offX;
            double py = base.y + offY;
            double pz = base.z + offZ;

            // Colored core beam
            level.sendParticles(
                dust,
                px, py, pz,
                1,
                0.0, 0.0, 0.0,
                0.0
            );

            // Optional electric spark overlay for extra energy
            if (RANDOM.nextFloat() < 0.4f) {
                level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    px, py, pz,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
                );
            }
        }
    }

}

