package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for reusable particle effect templates.
 * All effects now use ParticlePool for performance optimization.
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
         * Spawns this effect at a position using ParticlePool.
         */
        public void spawn(ServerLevel level, Vec3 position) {
            ParticlePool.queueParticle(level, particle, position, count, spreadX, spreadY, spreadZ, speed);
        }
        
        /**
         * Spawns this effect with a multiplier using ParticlePool.
         */
        public void spawn(ServerLevel level, Vec3 position, double multiplier) {
            int adjustedCount = (int) Math.max(1, count * multiplier);
            ParticlePool.queueParticle(level, particle, position, adjustedCount, spreadX, spreadY, spreadZ, speed);
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
        // Existing templates
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "magical_spark"),
            new ParticleEffectTemplate(ParticleTypes.END_ROD, 5, 0.2, 0.2, 0.2, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_burst"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 15, 0.5, 0.5, 0.5, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "impact_explosion"),
            new ParticleEffectTemplate(ParticleTypes.EXPLOSION, 10, 0.3, 0.3, 0.3, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "aura"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "golden_burst"),
            new ParticleEffectTemplate(ParticleTypes.TOTEM_OF_UNDYING, 20, 0.4, 0.4, 0.4, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "fire_burst"),
            new ParticleEffectTemplate(ParticleTypes.FLAME, 15, 0.3, 0.3, 0.3, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "silver_mist"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 12, 0.2, 0.2, 0.2, 0.05)
        );
        
        // Wand & Spellcasting
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "wand_cast_trail_core"),
            new ParticleEffectTemplate(ParticleTypes.END_ROD, 10, 0.1, 0.1, 0.1, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "wand_cast_trail_sparks"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 5, 0.2, 0.2, 0.2, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "wand_core_phoenix"),
            new ParticleEffectTemplate(ParticleTypes.TOTEM_OF_UNDYING, 8, 0.15, 0.15, 0.15, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "wand_core_dragon"),
            new ParticleEffectTemplate(ParticleTypes.FLAME, 8, 0.15, 0.15, 0.15, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "wand_core_unicorn"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 8, 0.15, 0.15, 0.15, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_channeling_aura"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        // Spell Clashes
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_clash_lightning"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 20, 0.0, 0.0, 0.0, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_clash_spark"),
            new ParticleEffectTemplate(ParticleTypes.END_ROD, 15, 0.2, 0.2, 0.2, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_clash_impact"),
            new ParticleEffectTemplate(ParticleTypes.EXPLOSION, 10, 0.3, 0.3, 0.3, 0.1)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "spell_clash_branch"),
            new ParticleEffectTemplate(ParticleTypes.SMOKE, 5, 0.1, 0.1, 0.1, 0.05)
        );
        
        // Elemental Spells
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "fire_embers"),
            new ParticleEffectTemplate(ParticleTypes.FLAME, 8, 0.2, 0.3, 0.2, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "fire_smoke_column"),
            new ParticleEffectTemplate(ParticleTypes.SMOKE, 10, 0.3, 0.5, 0.3, 0.02)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "water_mist"),
            new ParticleEffectTemplate(ParticleTypes.CLOUD, 8, 0.4, 0.2, 0.4, 0.01)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "water_droplets"),
            new ParticleEffectTemplate(ParticleTypes.SPLASH, 5, 0.2, 0.1, 0.2, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "lightning_arc"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 10, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "lightning_scorch"),
            new ParticleEffectTemplate(ParticleTypes.SMALL_FLAME, 8, 0.2, 0.0, 0.2, 0.0)
        );
        
        // Dark Magic
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "dark_curse_wisp"),
            new ParticleEffectTemplate(ParticleTypes.SMOKE, 6, 0.2, 0.2, 0.2, 0.03)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "soul_mote"),
            new ParticleEffectTemplate(ParticleTypes.SOUL, 5, 0.1, 0.1, 0.1, 0.02)
        );
        
        // Defensive/Utility
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "protego_hexagon"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 6, 0.0, 0.0, 0.0, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "protego_refraction"),
            new ParticleEffectTemplate(ParticleTypes.END_ROD, 2, 0.05, 0.05, 0.05, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "protego_impact_ring"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 20, 0.0, 0.0, 0.0, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "apparition_trail"),
            new ParticleEffectTemplate(ParticleTypes.PORTAL, 15, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "apparition_ring"),
            new ParticleEffectTemplate(ParticleTypes.PORTAL, 25, 0.5, 0.5, 0.5, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "detection_ring"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 30, 0.0, 0.0, 0.0, 0.0)
        );
        
        // Artifacts
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "time_turner_gears"),
            new ParticleEffectTemplate(ParticleTypes.TOTEM_OF_UNDYING, 10, 0.2, 0.2, 0.2, 0.05)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "philosopher_stone_glyph"),
            new ParticleEffectTemplate(ParticleTypes.ENCHANT, 8, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "elixir_steam"),
            new ParticleEffectTemplate(ParticleTypes.CLOUD, 5, 0.2, 0.3, 0.2, 0.01)
        );
        
        // World/Atmosphere
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "house_aura_gryffindor"),
            new ParticleEffectTemplate(ParticleTypes.FLAME, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "house_aura_slytherin"),
            new ParticleEffectTemplate(ParticleTypes.SMOKE, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "house_aura_hufflepuff"),
            new ParticleEffectTemplate(ParticleTypes.TOTEM_OF_UNDYING, 3, 0.1, 0.1, 0.1, 0.0)
        );
        
        register(
            Identifier.fromNamespaceAndPath("spells_n_squares", "house_aura_ravenclaw"),
            new ParticleEffectTemplate(ParticleTypes.ELECTRIC_SPARK, 3, 0.1, 0.1, 0.1, 0.0)
        );
    }
}
















