package at.koopro.spells_n_squares.features.combat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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
        return calculateDamage(null, target, damageTypeId, baseDamage);
    }
    
    /**
     * Calculates spell damage with optional caster for power multipliers (e.g., Elder Wand).
     */
    public static float calculateDamage(net.minecraft.world.entity.player.Player caster, LivingEntity target, Identifier damageTypeId, float baseDamage) {
        SpellDamageType damageType = DAMAGE_TYPES.get(damageTypeId);
        if (damageType == null) {
            return baseDamage; // Unknown damage type, no resistance
        }
        
        // Apply Elder Wand power multiplier if caster has it
        if (caster != null) {
            float powerMultiplier = at.koopro.spells_n_squares.features.artifacts.ElderWandItem.getPowerMultiplier(caster);
            baseDamage *= powerMultiplier;
        }
        
        float resistanceValue = 0.0f;
        
        // Check target entity for resistance data component
        if (target instanceof Player player) {
            // For players, use CombatStatsData spellResistance
            CombatStatsData.CombatStatsComponent stats = CombatStatsData.getCombatStats(player);
            resistanceValue = stats.spellResistance();
        }
        // TODO: For other entities, check for entity-specific resistance data component
        // This could be extended to support resistance data components on any LivingEntity
        
        // Apply resistance modifier: finalDamage = baseDamage * (1.0f - resistanceValue)
        // Clamp resistance between 0.0 and 0.9 (max 90% resistance)
        resistanceValue = Math.max(0.0f, Math.min(0.9f, resistanceValue));
        float finalDamage = baseDamage * (1.0f - resistanceValue);
        
        // Apply minimum damage threshold (always deal at least 10% of base damage)
        float minDamage = baseDamage * 0.1f;
        return Math.max(minDamage, finalDamage);
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

