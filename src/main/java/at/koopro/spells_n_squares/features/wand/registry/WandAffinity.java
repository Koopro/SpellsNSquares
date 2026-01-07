package at.koopro.spells_n_squares.features.wand.registry;

/**
 * Represents affinity bonuses provided by a wand core/wood combination.
 */
public record WandAffinity(
    float critChanceBonus,      // Additional crit chance (0.0 to 1.0)
    float cooldownModifier,     // Cooldown multiplier (1.0 = normal, <1.0 = faster, >1.0 = slower)
    float powerModifier,        // Spell power multiplier (1.0 = normal, >1.0 = stronger)
    float miscastChance,        // Chance of miscast (0.0 to 1.0)
    float stabilityBonus        // Stability bonus (reduces miscast chance)
) {
    public static final WandAffinity NONE = new WandAffinity(0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
    
    /**
     * Gets affinity bonuses for a wand core/wood combination.
     * Attuned wands get full bonuses, unattuned get reduced bonuses.
     */
    public static WandAffinity getAffinity(WandCore core, WandWood wood, boolean attuned) {
        if (core == null || wood == null) {
            return NONE;
        }
        
        float attunementMultiplier = attuned ? 1.0f : 0.5f;
        
        WandAffinity baseAffinity = getBaseAffinity(core);
        
        // Apply attunement multiplier to bonuses
        return new WandAffinity(
            baseAffinity.critChanceBonus() * attunementMultiplier,
            baseAffinity.cooldownModifier() + (1.0f - baseAffinity.cooldownModifier()) * (1.0f - attunementMultiplier),
            baseAffinity.powerModifier() + (baseAffinity.powerModifier() - 1.0f) * (attunementMultiplier - 1.0f),
            baseAffinity.miscastChance() * (2.0f - attunementMultiplier),
            baseAffinity.stabilityBonus() * attunementMultiplier
        );
    }
    
    /**
     * Gets base affinity for a wand core type.
     */
    private static WandAffinity getBaseAffinity(WandCore core) {
        return switch (core) {
            case PHOENIX_FEATHER -> new WandAffinity(
                0.15f,  // +15% crit chance
                0.85f,  // 15% faster cooldowns
                1.0f,   // Normal power
                0.05f,  // 5% miscast chance
                0.1f    // +10% stability
            );
            case DRAGON_HEARTSTRING -> new WandAffinity(
                0.05f,  // +5% crit chance
                1.2f,   // 20% slower cooldowns
                1.25f,  // +25% power
                0.15f,  // 15% miscast chance
                0.0f    // No stability bonus
            );
            case UNICORN_HAIR -> new WandAffinity(
                0.0f,   // No crit bonus
                0.9f,   // 10% faster cooldowns
                0.95f,  // -5% power
                0.02f,  // 2% miscast chance
                0.2f    // +20% stability
            );
        };
    }
}

