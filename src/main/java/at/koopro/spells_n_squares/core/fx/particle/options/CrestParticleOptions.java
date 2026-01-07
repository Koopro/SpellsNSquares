package at.koopro.spells_n_squares.core.fx.particle.options;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Particle options for house crest particles.
 * Stores house identifier.
 * Note: For now, using SimpleParticleType. Custom options can be added later if needed.
 */
public record CrestParticleOptions(int houseId) implements ParticleOptions {
    // House IDs: 0=Gryffindor, 1=Slytherin, 2=Hufflepuff, 3=Ravenclaw
    
    @Override
    public ParticleType<?> getType() {
        // Return appropriate particle type based on house
        return switch (houseId) {
            case 0 -> at.koopro.spells_n_squares.core.registry.ModParticles.HOUSE_CREST_GRYFFINDOR.value();
            case 1 -> at.koopro.spells_n_squares.core.registry.ModParticles.HOUSE_CREST_SLYTHERIN.value();
            case 2 -> at.koopro.spells_n_squares.core.registry.ModParticles.HOUSE_CREST_HUFFLEPUFF.value();
            case 3 -> at.koopro.spells_n_squares.core.registry.ModParticles.HOUSE_CREST_RAVENCLAW.value();
            default -> at.koopro.spells_n_squares.core.registry.ModParticles.HOUSE_CREST_GRYFFINDOR.value();
        };
    }
}

