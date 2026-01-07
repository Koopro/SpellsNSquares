package at.koopro.spells_n_squares.core.fx.particle.options;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Particle options for magical aura particles.
 * Stores color and intensity information.
 * Note: For now, using SimpleParticleType. Custom options can be added later if needed.
 */
public record AuraParticleOptions(float red, float green, float blue, float intensity) implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return at.koopro.spells_n_squares.core.registry.ModParticles.MAGICAL_AURA.value();
    }
}

