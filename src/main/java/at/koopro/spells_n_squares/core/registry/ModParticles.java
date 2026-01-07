package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registry for custom particle types.
 * Custom particles extend Minecraft's particle system with mod-specific visual effects.
 */
public class ModParticles {
    private ModParticles() {
    }
    
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = 
        DeferredRegister.create(Registries.PARTICLE_TYPE, SpellsNSquares.MODID);
    
    // Custom particle types - using SimpleParticleType for now (can be extended later)
    // Note: For particles with custom data (colors, glyph types, etc.), we'll use ParticleOptions classes
    
    /**
     * Runic glyph particles for alchemy and rituals.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RUNE_GLYPH = 
        PARTICLE_TYPES.register("rune_glyph", () -> new SimpleParticleType(false));
    
    /**
     * House crest particles - Gryffindor.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOUSE_CREST_GRYFFINDOR = 
        PARTICLE_TYPES.register("house_crest_gryffindor", () -> new SimpleParticleType(false));
    
    /**
     * House crest particles - Slytherin.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOUSE_CREST_SLYTHERIN = 
        PARTICLE_TYPES.register("house_crest_slytherin", () -> new SimpleParticleType(false));
    
    /**
     * House crest particles - Hufflepuff.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOUSE_CREST_HUFFLEPUFF = 
        PARTICLE_TYPES.register("house_crest_hufflepuff", () -> new SimpleParticleType(false));
    
    /**
     * House crest particles - Ravenclaw.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOUSE_CREST_RAVENCLAW = 
        PARTICLE_TYPES.register("house_crest_ravenclaw", () -> new SimpleParticleType(false));
    
    /**
     * Patronus silhouette particles.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PATRONUS_SILHOUETTE = 
        PARTICLE_TYPES.register("patronus_silhouette", () -> new SimpleParticleType(false));
    
    /**
     * Magical aura particles (configurable color).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAGICAL_AURA = 
        PARTICLE_TYPES.register("magical_aura", () -> new SimpleParticleType(false));
    
    /**
     * Spell trail core particles (wand-specific).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPELL_TRAIL_CORE = 
        PARTICLE_TYPES.register("spell_trail_core", () -> new SimpleParticleType(false));
    
    /**
     * Spell trail spark particles (side sparks).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPELL_TRAIL_SPARK = 
        PARTICLE_TYPES.register("spell_trail_spark", () -> new SimpleParticleType(false));
    
    /**
     * Dark curse wisp particles (sickly green).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DARK_CURSE_WISP = 
        PARTICLE_TYPES.register("dark_curse_wisp", () -> new SimpleParticleType(false));
    
    /**
     * Soul mote particles (pale blue for Horcrux/soul magic).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_MOTE = 
        PARTICLE_TYPES.register("soul_mote", () -> new SimpleParticleType(false));
    
    /**
     * Alchemical glyph particles (red-gold for Philosopher's Stone).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ALCHEMICAL_GLYPH = 
        PARTICLE_TYPES.register("alchemical_glyph", () -> new SimpleParticleType(false));
}

