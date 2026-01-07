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
    
    // Spell clash and custom particles
    public static final ModConfigSpec.BooleanValue ENABLE_SPELL_CLASH_EFFECTS;
    public static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_PARTICLES;
    public static final ModConfigSpec.DoubleValue SPELL_CLASH_RANGE;
    public static final ModConfigSpec.DoubleValue SPELL_CLASH_INTENSITY;
    public static final ModConfigSpec.IntValue SPELL_CLASH_DURATION;
    public static final ModConfigSpec.EnumValue<EffectQuality> CUSTOM_PARTICLE_QUALITY;
    public static final ModConfigSpec.BooleanValue EFFECT_GRAPH_ENABLED;

    // Gameplay balance settings
    public static final ModConfigSpec.DoubleValue SPELL_COOLDOWN_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SPELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue POTION_DURATION_MULTIPLIER;
    
    // Development/Debug logging settings
    public static final ModConfigSpec.BooleanValue ENABLE_VERBOSE_LOGGING;
    public static final ModConfigSpec.BooleanValue LOG_METHOD_ENTRY_EXIT;
    public static final ModConfigSpec.BooleanValue LOG_PARAMETERS;
    public static final ModConfigSpec.BooleanValue LOG_RETURN_VALUES;
    public static final ModConfigSpec.BooleanValue LOG_STATE_CHANGES;
    public static final ModConfigSpec.BooleanValue LOG_NETWORK_PACKETS;
    public static final ModConfigSpec.BooleanValue LOG_BLOCK_INTERACTIONS;
    public static final ModConfigSpec.BooleanValue LOG_ITEM_INTERACTIONS;
    public static final ModConfigSpec.BooleanValue LOG_ENTITY_EVENTS;
    public static final ModConfigSpec.BooleanValue LOG_DATA_OPERATIONS;
    public static final ModConfigSpec.BooleanValue ENABLE_DEBUG_TOOLTIPS;
    
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
        
        BUILDER.comment("Spell clash and custom particle settings");
        ENABLE_SPELL_CLASH_EFFECTS = BUILDER
            .comment("Enable spell clash lightning effects")
            .define("enableSpellClashEffects", true);
        
        ENABLE_CUSTOM_PARTICLES = BUILDER
            .comment("Enable custom particle types")
            .define("enableCustomParticles", true);
        
        SPELL_CLASH_RANGE = BUILDER
            .comment("Maximum range for duel detection (blocks)")
            .defineInRange("spellClashRange", 20.0, 5.0, 50.0);
        
        SPELL_CLASH_INTENSITY = BUILDER
            .comment("Intensity multiplier for clash effects")
            .defineInRange("spellClashIntensity", 1.0, 0.1, 2.0);
        
        SPELL_CLASH_DURATION = BUILDER
            .comment("Duration of clash effects in ticks")
            .defineInRange("spellClashDuration", 40, 10, 100);
        
        CUSTOM_PARTICLE_QUALITY = BUILDER
            .comment("Quality for custom particles (LOW, MEDIUM, HIGH)")
            .defineEnum("customParticleQuality", EffectQuality.MEDIUM);
        
        EFFECT_GRAPH_ENABLED = BUILDER
            .comment("Enable effect graph system")
            .define("effectGraphEnabled", true);
        
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
        
        BUILDER.push("development");
        BUILDER.comment("Development and debugging logging options");
        
        ENABLE_VERBOSE_LOGGING = BUILDER
            .comment("Master switch for verbose development logging. When disabled, all verbose logging is skipped for performance.")
            .define("enableVerboseLogging", false);
        
        LOG_METHOD_ENTRY_EXIT = BUILDER
            .comment("Log method entry and exit points")
            .define("logMethodEntryExit", false);
        
        LOG_PARAMETERS = BUILDER
            .comment("Log method parameters")
            .define("logParameters", false);
        
        LOG_RETURN_VALUES = BUILDER
            .comment("Log method return values")
            .define("logReturnValues", false);
        
        LOG_STATE_CHANGES = BUILDER
            .comment("Log state changes (block states, entity states, etc.)")
            .define("logStateChanges", false);
        
        LOG_NETWORK_PACKETS = BUILDER
            .comment("Log network packet send/receive operations")
            .define("logNetworkPackets", false);
        
        LOG_BLOCK_INTERACTIONS = BUILDER
            .comment("Log block interaction events")
            .define("logBlockInteractions", false);
        
        LOG_ITEM_INTERACTIONS = BUILDER
            .comment("Log item interaction events")
            .define("logItemInteractions", false);
        
        LOG_ENTITY_EVENTS = BUILDER
            .comment("Log entity events (spawn, despawn, updates, etc.)")
            .define("logEntityEvents", false);
        
        LOG_DATA_OPERATIONS = BUILDER
            .comment("Log data save/load operations")
            .define("logDataOperations", false);
        
        BUILDER.comment("Debug tooltip settings");
        ENABLE_DEBUG_TOOLTIPS = BUILDER
            .comment("Enable debug tooltips showing detailed data about items, blocks, entities, and players")
            .define("enableDebugTooltips", false);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
    
    /**
     * Checks if high-FX mode is enabled.
     * Uses caching for performance.
     */
    public static boolean isHighFxMode() {
        return ConfigCache.get("highFxMode", () -> HIGH_FX_MODE.get());
    }
    
    /**
     * Checks if wand particles are enabled.
     * Uses caching for performance.
     */
    public static boolean areWandParticlesEnabled() {
        return ConfigCache.get("enableWandParticles", () -> ENABLE_WAND_PARTICLES.get());
    }
    
    /**
     * Checks if cloak shimmer is enabled.
     * Uses caching for performance.
     */
    public static boolean isCloakShimmerEnabled() {
        return ConfigCache.get("enableCloakShimmer", () -> ENABLE_CLOAK_SHIMMER.get());
    }
    
    /**
     * Checks if artifact effects are enabled.
     * Uses caching for performance.
     */
    public static boolean areArtifactEffectsEnabled() {
        return ConfigCache.get("enableArtifactEffects", () -> ENABLE_ARTIFACT_EFFECTS.get());
    }
    
    /**
     * Gets the particle multiplier.
     * Uses caching for performance.
     */
    public static double getParticleMultiplier() {
        return ConfigCache.get("particleMultiplier", () -> PARTICLE_MULTIPLIER.get());
    }
    
    /**
     * Gets the screen effect intensity.
     * Uses caching for performance.
     */
    public static double getScreenEffectIntensity() {
        return ConfigCache.get("screenEffectIntensity", () -> SCREEN_EFFECT_INTENSITY.get());
    }
    
    /**
     * Checks if shader effects are enabled.
     * Uses caching for performance.
     */
    public static boolean areShaderEffectsEnabled() {
        return ConfigCache.get("shaderEffectsEnabled", () -> SHADER_EFFECTS_ENABLED.get());
    }
    
    /**
     * Checks if environmental effects are enabled.
     * Uses caching for performance.
     */
    public static boolean areEnvironmentalEffectsEnabled() {
        return ConfigCache.get("environmentalEffectsEnabled", () -> ENVIRONMENTAL_EFFECTS_ENABLED.get());
    }
    
    /**
     * Gets the maximum particle render distance.
     * Uses caching for performance.
     */
    public static int getMaxParticleDistance() {
        return ConfigCache.get("maxParticleDistance", () -> MAX_PARTICLE_DISTANCE.get());
    }
    
    /**
     * Gets the effect quality preset.
     * Uses caching for performance.
     */
    public static EffectQuality getEffectQuality() {
        return ConfigCache.get("effectQuality", () -> EFFECT_QUALITY.get());
    }

    /**
     * Gets the global spell cooldown multiplier.
     * Allows tuning spell cooldowns without code changes.
     * Uses caching for performance.
     */
    public static double getSpellCooldownMultiplier() {
        return ConfigCache.get("spellCooldownMultiplier", () -> SPELL_COOLDOWN_MULTIPLIER.get());
    }

    /**
     * Gets the global spell damage multiplier.
     * Allows tuning spell damage without code changes.
     * Uses caching for performance.
     */
    public static double getSpellDamageMultiplier() {
        return ConfigCache.get("spellDamageMultiplier", () -> SPELL_DAMAGE_MULTIPLIER.get());
    }

    /**
     * Gets the global potion duration multiplier.
     * Allows tuning potion effect durations without code changes.
     * Uses caching for performance.
     */
    public static double getPotionDurationMultiplier() {
        return ConfigCache.get("potionDurationMultiplier", () -> POTION_DURATION_MULTIPLIER.get());
    }
    
    /**
     * Checks if verbose logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isVerboseLoggingEnabled() {
        return ConfigCache.get("enableVerboseLogging", 
            () -> ConfigAccessor.getBoolean(ENABLE_VERBOSE_LOGGING, false));
    }
    
    /**
     * Checks if method entry/exit logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isMethodEntryExitEnabled() {
        return ConfigCache.get("logMethodEntryExit", 
            () -> ConfigAccessor.getBoolean(LOG_METHOD_ENTRY_EXIT, false));
    }
    
    /**
     * Checks if parameter logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isParametersEnabled() {
        return ConfigCache.get("logParameters", 
            () -> ConfigAccessor.getBoolean(LOG_PARAMETERS, false));
    }
    
    /**
     * Checks if return value logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isReturnValuesEnabled() {
        return ConfigCache.get("logReturnValues", 
            () -> ConfigAccessor.getBoolean(LOG_RETURN_VALUES, false));
    }
    
    /**
     * Checks if state change logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isStateChangesEnabled() {
        return ConfigCache.get("logStateChanges", 
            () -> ConfigAccessor.getBoolean(LOG_STATE_CHANGES, false));
    }
    
    /**
     * Checks if network packet logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isNetworkPacketsEnabled() {
        return ConfigCache.get("logNetworkPackets", 
            () -> ConfigAccessor.getBoolean(LOG_NETWORK_PACKETS, false));
    }
    
    /**
     * Checks if block interaction logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isBlockInteractionsEnabled() {
        return ConfigCache.get("logBlockInteractions", 
            () -> ConfigAccessor.getBoolean(LOG_BLOCK_INTERACTIONS, false));
    }
    
    /**
     * Checks if item interaction logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isItemInteractionsEnabled() {
        return ConfigCache.get("logItemInteractions", 
            () -> ConfigAccessor.getBoolean(LOG_ITEM_INTERACTIONS, false));
    }
    
    /**
     * Checks if entity event logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isEntityEventsEnabled() {
        return ConfigCache.get("logEntityEvents", 
            () -> ConfigAccessor.getBoolean(LOG_ENTITY_EVENTS, false));
    }
    
    /**
     * Checks if data operation logging is enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isDataOperationsEnabled() {
        return ConfigCache.get("logDataOperations", 
            () -> ConfigAccessor.getBoolean(LOG_DATA_OPERATIONS, false));
    }
    
    /**
     * Checks if spell clash effects are enabled.
     */
    public static boolean enableSpellClashEffects() {
        return ConfigCache.get("enableSpellClashEffects", () -> ENABLE_SPELL_CLASH_EFFECTS.get());
    }
    
    /**
     * Checks if custom particles are enabled.
     */
    public static boolean enableCustomParticles() {
        return ConfigCache.get("enableCustomParticles", () -> ENABLE_CUSTOM_PARTICLES.get());
    }
    
    /**
     * Gets the spell clash detection range.
     */
    public static double getSpellClashRange() {
        return ConfigCache.get("spellClashRange", () -> SPELL_CLASH_RANGE.get());
    }
    
    /**
     * Gets the spell clash intensity multiplier.
     */
    public static double getSpellClashIntensity() {
        return ConfigCache.get("spellClashIntensity", () -> SPELL_CLASH_INTENSITY.get());
    }
    
    /**
     * Gets the spell clash duration in ticks.
     */
    public static int getSpellClashDuration() {
        return ConfigCache.get("spellClashDuration", () -> SPELL_CLASH_DURATION.get());
    }
    
    /**
     * Gets the custom particle quality setting.
     */
    public static EffectQuality getCustomParticleQuality() {
        return ConfigCache.get("customParticleQuality", () -> CUSTOM_PARTICLE_QUALITY.get());
    }
    
    /**
     * Checks if effect graph system is enabled.
     */
    public static boolean isEffectGraphEnabled() {
        return ConfigCache.get("effectGraphEnabled", () -> EFFECT_GRAPH_ENABLED.get());
    }
    
    /**
     * Checks if debug tooltips are enabled.
     * Returns false if config is not yet loaded (safe to call during mod construction).
     * Uses caching for performance.
     */
    public static boolean isDebugTooltipsEnabled() {
        return ConfigCache.get("enableDebugTooltips", 
            () -> ConfigAccessor.getBoolean(ENABLE_DEBUG_TOOLTIPS, false));
    }
}