package at.koopro.spells_n_squares.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    public static final ModConfigSpec.BooleanValue HIGH_FX_MODE;
    public static final ModConfigSpec.BooleanValue ENABLE_WAND_PARTICLES;
    public static final ModConfigSpec.BooleanValue ENABLE_CLOAK_SHIMMER;
    public static final ModConfigSpec.BooleanValue ENABLE_ARTIFACT_EFFECTS;
    
    // New FX options
    public static final ModConfigSpec.DoubleValue PARTICLE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SCREEN_EFFECT_INTENSITY;
    public static final ModConfigSpec.BooleanValue SHADER_EFFECTS_ENABLED;
    public static final ModConfigSpec.BooleanValue ENVIRONMENTAL_EFFECTS_ENABLED;
    public static final ModConfigSpec.IntValue MAX_PARTICLE_DISTANCE;
    public static final ModConfigSpec.EnumValue<EffectQuality> EFFECT_QUALITY;

    // Gameplay balance settings
    public static final ModConfigSpec.DoubleValue SPELL_COOLDOWN_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SPELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue POTION_DURATION_MULTIPLIER;
    
    public static final ModConfigSpec SPEC;
    
    /**
     * Effect quality presets.
     */
    public enum EffectQuality {
        LOW, MEDIUM, HIGH, ULTRA
    }
    
    static {
        BUILDER.push("visual_effects");
        
        HIGH_FX_MODE = BUILDER
            .comment("Enable high-FX mode (full particles, shaders, effects)")
            .define("highFxMode", false);
        
        ENABLE_WAND_PARTICLES = BUILDER
            .comment("Enable wand particle trails and effects")
            .define("enableWandParticles", true);
        
        ENABLE_CLOAK_SHIMMER = BUILDER
            .comment("Enable invisibility cloak shimmer effects")
            .define("enableCloakShimmer", true);
        
        ENABLE_ARTIFACT_EFFECTS = BUILDER
            .comment("Enable artifact visual effects (Time-Turner, Deluminator, etc.)")
            .define("enableArtifactEffects", true);
        
        PARTICLE_MULTIPLIER = BUILDER
            .comment("Particle count multiplier (0.0 = no particles, 1.0 = normal, 2.0 = double)")
            .defineInRange("particleMultiplier", 1.0, 0.0, 2.0);
        
        SCREEN_EFFECT_INTENSITY = BUILDER
            .comment("Screen effect intensity (0.0 = disabled, 1.0 = full intensity)")
            .defineInRange("screenEffectIntensity", 0.8, 0.0, 1.0);
        
        SHADER_EFFECTS_ENABLED = BUILDER
            .comment("Enable shader-based post-processing effects (requires shader support)")
            .define("shaderEffectsEnabled", true);
        
        ENVIRONMENTAL_EFFECTS_ENABLED = BUILDER
            .comment("Enable environmental effects (auras, ambient particles, world effects)")
            .define("environmentalEffectsEnabled", true);
        
        MAX_PARTICLE_DISTANCE = BUILDER
            .comment("Maximum distance to render particles (in blocks)")
            .defineInRange("maxParticleDistance", 64, 16, 128);
        
        EFFECT_QUALITY = BUILDER
            .comment("Overall effect quality preset (LOW, MEDIUM, HIGH, ULTRA)")
            .defineEnum("effectQuality", EffectQuality.MEDIUM);
        
        BUILDER.pop();
        
        BUILDER.push("gameplay");
        
        BUILDER.comment("Spell balance settings");
        SPELL_COOLDOWN_MULTIPLIER = BUILDER
            .defineInRange("spellCooldownMultiplier", 1.0, 0.1, 10.0);
        SPELL_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("spellDamageMultiplier", 1.0, 0.1, 10.0);
        POTION_DURATION_MULTIPLIER = BUILDER
            .comment("Global potion duration multiplier (1.0 = default)")
            .defineInRange("potionDurationMultiplier", 1.0, 0.1, 5.0);
        BUILDER.define("enableDarkMagic", true);
        
        BUILDER.comment("Creature settings");
        BUILDER.define("creatureSpawnRate", 1.0);
        BUILDER.define("enableHostileCreatures", true);
        
        BUILDER.comment("Economy settings");
        BUILDER.define("currencyDropRate", 1.0);
        BUILDER.define("enableTrading", true);
        
        BUILDER.comment("Education settings");
        BUILDER.define("enableHousePoints", true);
        BUILDER.define("enableHomework", true);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
    
    /**
     * Checks if high-FX mode is enabled.
     */
    public static boolean isHighFxMode() {
        return HIGH_FX_MODE.get();
    }
    
    /**
     * Checks if wand particles are enabled.
     */
    public static boolean areWandParticlesEnabled() {
        return ENABLE_WAND_PARTICLES.get();
    }
    
    /**
     * Checks if cloak shimmer is enabled.
     */
    public static boolean isCloakShimmerEnabled() {
        return ENABLE_CLOAK_SHIMMER.get();
    }
    
    /**
     * Checks if artifact effects are enabled.
     */
    public static boolean areArtifactEffectsEnabled() {
        return ENABLE_ARTIFACT_EFFECTS.get();
    }
    
    /**
     * Gets the particle multiplier.
     */
    public static double getParticleMultiplier() {
        return PARTICLE_MULTIPLIER.get();
    }
    
    /**
     * Gets the screen effect intensity.
     */
    public static double getScreenEffectIntensity() {
        return SCREEN_EFFECT_INTENSITY.get();
    }
    
    /**
     * Checks if shader effects are enabled.
     */
    public static boolean areShaderEffectsEnabled() {
        return SHADER_EFFECTS_ENABLED.get();
    }
    
    /**
     * Checks if environmental effects are enabled.
     */
    public static boolean areEnvironmentalEffectsEnabled() {
        return ENVIRONMENTAL_EFFECTS_ENABLED.get();
    }
    
    /**
     * Gets the maximum particle render distance.
     */
    public static int getMaxParticleDistance() {
        return MAX_PARTICLE_DISTANCE.get();
    }
    
    /**
     * Gets the effect quality preset.
     */
    public static EffectQuality getEffectQuality() {
        return EFFECT_QUALITY.get();
    }

    /**
     * Gets the global spell cooldown multiplier.
     * Allows tuning spell cooldowns without code changes.
     */
    public static double getSpellCooldownMultiplier() {
        return SPELL_COOLDOWN_MULTIPLIER.get();
    }

    /**
     * Gets the global spell damage multiplier.
     * Allows tuning spell damage without code changes.
     */
    public static double getSpellDamageMultiplier() {
        return SPELL_DAMAGE_MULTIPLIER.get();
    }

    /**
     * Gets the global potion duration multiplier.
     * Allows tuning potion effect durations without code changes.
     */
    public static double getPotionDurationMultiplier() {
        return POTION_DURATION_MULTIPLIER.get();
    }
}