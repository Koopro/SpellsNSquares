package at.koopro.spells_n_squares.core.fx.particle.options;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Particle options for runic glyph particles.
 * Stores glyph type and color information.
 * Note: For now, using SimpleParticleType. Custom options can be added later if needed.
 */
public record GlyphParticleOptions(int glyphType, float red, float green, float blue) implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return at.koopro.spells_n_squares.core.registry.ModParticles.RUNE_GLYPH.value();
    }
}

