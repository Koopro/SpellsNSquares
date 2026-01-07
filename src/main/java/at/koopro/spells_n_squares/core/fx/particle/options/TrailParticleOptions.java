package at.koopro.spells_n_squares.core.fx.particle.options;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Particle options for spell trail particles.
 * Stores core type and intensity information.
 * Note: For now, using SimpleParticleType. Custom options can be added later if needed.
 */
public record TrailParticleOptions(int coreType, float intensity) implements ParticleOptions {
    // Core types: 0=Phoenix, 1=Dragon, 2=Unicorn
    
    @Override
    public ParticleType<?> getType() {
        return at.koopro.spells_n_squares.core.registry.ModParticles.SPELL_TRAIL_CORE.value();
    }
}

