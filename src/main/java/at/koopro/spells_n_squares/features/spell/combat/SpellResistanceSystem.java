package at.koopro.spells_n_squares.features.spell.combat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/**
 * System for calculating and applying spell resistance to entities.
 * Handles spell damage reduction based on entity properties and effects.
 */
public final class SpellResistanceSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private SpellResistanceSystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a damage source is from a spell.
     * 
     * @param source The damage source
     * @return true if the damage is from a spell
     */
    public static boolean isSpellDamage(DamageSource source) {
        if (source == null) {
            return false;
        }
        
        // Check for custom spell damage types
        // In NeoForge, we can check damage type tags or custom damage types
        String msgId = source.getMsgId();
        if (msgId != null) {
            // Check if damage type indicates spell damage
            // This would need to be registered as a custom damage type
            return msgId.contains("spell") || msgId.contains("magic");
        }
        
        // Check if damage source entity is a spell entity
        if (source.getEntity() != null) {
            // Could check for spell projectiles or spell entities
            String entityType = source.getEntity().getType().toString();
            if (entityType.contains("spell") || entityType.contains("magic")) {
                return true;
            }
        }
        
        // Check for indirect spell damage (e.g., from spell effects)
        if (source.getDirectEntity() != null) {
            String directEntityType = source.getDirectEntity().getType().toString();
            if (directEntityType.contains("spell") || directEntityType.contains("magic")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculates the spell resistance for an entity.
     * 
     * @param entity The entity
     * @return Resistance value (0.0 = no resistance, 1.0 = complete immunity)
     */
    public static float getSpellResistance(LivingEntity entity) {
        if (entity == null) {
            return 0.0f;
        }
        
        float resistance = 0.0f;
        
        // Check for player spell resistance
        // Note: Combat stats integration is planned but not yet implemented.
        // When CombatStatsData is fully integrated, this should use:
        // PlayerDataHelper.getCombatStats(player).spellResistance()
        // For now, spell resistance is calculated from other sources (effects, etc.)
        
        // Check for resistance effects (e.g., Protego)
        // Vanilla RESISTANCE effect already handles damage reduction,
        // but we can add additional spell-specific resistance here
        
        // Clamp resistance to valid range (0.0 to 0.9)
        return Math.max(0.0f, Math.min(0.9f, resistance));
    }
    
    /**
     * Checks if an entity has complete spell immunity.
     * 
     * @param entity The entity
     * @return true if the entity is immune to spell damage
     */
    public static boolean hasSpellImmunity(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        
        // Check for complete immunity (resistance >= 1.0 or special immunity flag)
        float resistance = getSpellResistance(entity);
        return resistance >= 1.0f;
    }
    
    /**
     * Calculates the modified damage amount after applying spell resistance.
     * 
     * @param originalDamage The original damage amount
     * @param entity The entity taking damage
     * @param source The damage source
     * @return The modified damage amount
     */
    public static float calculateResistedDamage(float originalDamage, LivingEntity entity, DamageSource source) {
        if (entity == null || source == null || originalDamage <= 0.0f) {
            return originalDamage;
        }
        
        // Only apply resistance to spell damage
        if (!isSpellDamage(source)) {
            return originalDamage;
        }
        
        // Check for complete immunity
        if (hasSpellImmunity(entity)) {
            return 0.0f;
        }
        
        // Calculate resistance
        float resistance = getSpellResistance(entity);
        
        // Apply resistance: damage = original * (1 - resistance)
        float resistedDamage = originalDamage * (1.0f - resistance);
        
        return Math.max(0.0f, resistedDamage);
    }
    
    /**
     * Applies spell resistance to damage and returns the modified amount.
     * This is the main method to use when processing spell damage.
     * 
     * @param entity The entity taking damage
     * @param source The damage source
     * @param originalDamage The original damage amount
     * @return The modified damage amount after resistance
     */
    public static float applyResistance(LivingEntity entity, DamageSource source, float originalDamage) {
        try {
            return calculateResistedDamage(originalDamage, entity, source);
        } catch (Exception e) {
            LOGGER.error("Error applying spell resistance: {}", e.getMessage(), e);
            return originalDamage; // Return original damage on error
        }
    }
}

