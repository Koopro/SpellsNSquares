package at.koopro.spells_n_squares.modules.wand.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration for the wand module.
 * Each module can have its own configuration.
 */
public class WandConfig {
    public static final ModConfigSpec SPEC;
    
    public static final ModConfigSpec.BooleanValue ENABLE_WAND_GLOW;
    public static final ModConfigSpec.IntValue MAX_ATTUNEMENT_LEVEL;
    public static final ModConfigSpec.DoubleValue ATTUNEMENT_MULTIPLIER;
    
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        
        builder.comment("Wand System Configuration")
               .push("wand");
        
        ENABLE_WAND_GLOW = builder
            .comment("Enable visual glow effect on attuned wands")
            .define("enableWandGlow", true);
        
        MAX_ATTUNEMENT_LEVEL = builder
            .comment("Maximum attunement level for wands")
            .defineInRange("maxAttunementLevel", 100, 1, 1000);
        
        ATTUNEMENT_MULTIPLIER = builder
            .comment("Multiplier for attunement gain")
            .defineInRange("attunementMultiplier", 1.0, 0.1, 10.0);
        
        builder.pop();
        SPEC = builder.build();
    }
}

