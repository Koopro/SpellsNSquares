package at.koopro.spells_n_squares.core.registry;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for reusable particle effect templates.
 */
public final class ParticleEffectRegistry {
    private ParticleEffectRegistry() {
    }
    
    private static final Map<Identifier, ParticleEffectTemplate> templates = new HashMap<>();
    
    /**
     * Template for a particle effect.
     */
    public record ParticleEffectTemplate(
        ParticleOptions particle,
        int count,
        double spreadX, double spreadY, double spreadZ,
        double speed
    ) {
        /**
         * Spawns this effect at a position.
         */
        public void spawn(ServerLevel level, Vec3 position) {
            level.sendParticles(
                particle,
                position.x, position.y, position.z,
                count,
                spreadX, spreadY, spreadZ,
                speed
            );
        }
        
        /**
         * Spawns this effect with a multiplier.
         */
        public void spawn(ServerLevel level, Vec3 position, double multiplier) {
            int adjustedCount = (int) Math.max(1, count * multiplier);
            level.sendParticles(
                particle,
                position.x, position.y, position.z,
                adjustedCount,
                spreadX, spreadY, spreadZ,
                speed
            );
        }
    }
    
    /**
     * Registers a particle effect template.
     */
    public static void register(Identifier id, ParticleEffectTemplate template) {
        templates.put(id, template);
    }
    
    /**
     * Gets a particle effect template.
     */
    public static ParticleEffectTemplate get(Identifier id) {
        return templates.get(id);
    }
    
    /**
     * Initializes default particle effect templates.
     */
    public static void initializeDefaults() {
        // Magical spark effect
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "magical_spark"),
            new ParticleEffectTemplate(ParticleTypes.END_ROD, 5, 0.2, 0.2, 0.2, 0.05)
        );
        
        // Spell burst effect
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_burst"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 15, 0.5, 0.5, 0.5, 0.1)
        );
        
        // Impact explosion
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "impact_explosion"),
            new ParticleEffectTemplate(ParticleTypes.EXPLOSION, 10, 0.3, 0.3, 0.3, 0.1)
        );
        
        // Aura particles
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "aura"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        // Golden burst (Phoenix Feather)
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "golden_burst"),
            new ParticleEffectTemplate(ParticleTypes.TOTEM_OF_UNDYING, 20, 0.4, 0.4, 0.4, 0.1)
        );
        
        // Fire burst (Dragon Heartstring)
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "fire_burst"),
            new ParticleEffectTemplate(ParticleTypes.FLAME, 15, 0.3, 0.3, 0.3, 0.1)
        );
        
        // Silver mist (Unicorn Hair)
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "silver_mist"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 12, 0.2, 0.2, 0.2, 0.05)
        );
    }
}















