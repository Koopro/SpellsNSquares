package at.koopro.spells_n_squares.features.combat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * System for managing spell resistance types and damage calculations.
 */
public final class SpellResistanceSystem {
    private static final Map<Identifier, SpellDamageType> DAMAGE_TYPES = new HashMap<>();
    
    private SpellResistanceSystem() {
    }
    
    /**
     * Registers a spell damage type.
     */
    public static void register(Identifier id, SpellDamageType damageType) {
        DAMAGE_TYPES.put(id, damageType);
    }
    
    /**
     * Gets a damage type by ID.
     */
    public static SpellDamageType get(Identifier id) {
        return DAMAGE_TYPES.get(id);
    }
    
    /**
     * Calculates damage after applying resistances.
     */
    public static float calculateDamage(LivingEntity target, Identifier damageTypeId, float baseDamage) {
        SpellDamageType damageType = DAMAGE_TYPES.get(damageTypeId);
        if (damageType == null) {
            return baseDamage; // Unknown damage type, no resistance
        }
        
        // TODO: Implement resistance calculation
        // - Check target entity for resistance data component (if applicable)
        // - Look up resistance value for damageType.getResistanceType()
        // - Apply resistance modifier: finalDamage = baseDamage * (1.0f - resistanceValue)
        // - Consider minimum damage threshold (e.g., always deal at least 10% damage)
        return baseDamage;
    }
    
    /**
     * Represents a type of spell damage.
     */
    public static class SpellDamageType {
        private final Identifier id;
        private final String name;
        private final Identifier resistanceType; // What resistance affects this
        
        public SpellDamageType(Identifier id, String name, Identifier resistanceType) {
            this.id = id;
            this.name = name;
            this.resistanceType = resistanceType;
        }
        
        public Identifier getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public Identifier getResistanceType() {
            return resistanceType;
        }
    }
}

