package at.koopro.spells_n_squares.features.combat.system;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * System for managing shield spells that block incoming spells.
 */
public final class ShieldSystem {
    private static final Map<UUID, ShieldInstance> ACTIVE_SHIELDS = new HashMap<>();
    
    private ShieldSystem() {
    }
    
    /**
     * Creates a shield for an entity.
     */
    public static void createShield(LivingEntity entity, float strength, int duration) {
        ACTIVE_SHIELDS.put(entity.getUUID(), new ShieldInstance(strength, duration));
    }
    
    /**
     * Removes a shield from an entity.
     */
    public static void removeShield(LivingEntity entity) {
        ACTIVE_SHIELDS.remove(entity.getUUID());
    }
    
    /**
     * Checks if an entity has an active shield.
     */
    public static boolean hasShield(LivingEntity entity) {
        ShieldInstance shield = ACTIVE_SHIELDS.get(entity.getUUID());
        return shield != null && shield.isActive();
    }
    
    /**
     * Checks if a spell can penetrate a shield.
     */
    public static boolean canPenetrate(LivingEntity entity, float spellPower) {
        ShieldInstance shield = ACTIVE_SHIELDS.get(entity.getUUID());
        if (shield == null || !shield.isActive()) {
            return true; // No shield
        }
        return spellPower > shield.getStrength();
    }
    
    /**
     * Damages a shield.
     */
    public static void damageShield(LivingEntity entity, float damage) {
        ShieldInstance shield = ACTIVE_SHIELDS.get(entity.getUUID());
        if (shield != null) {
            shield.damage(damage);
            if (!shield.isActive()) {
                ACTIVE_SHIELDS.remove(entity.getUUID());
            }
        }
    }
    
    /**
     * Represents an active shield instance.
     */
    public static class ShieldInstance {
        private float strength;
        private int remainingTicks;
        
        public ShieldInstance(float strength, int duration) {
            this.strength = strength;
            this.remainingTicks = duration;
        }
        
        public float getStrength() {
            return strength;
        }
        
        public boolean isActive() {
            return remainingTicks > 0 && strength > 0;
        }
        
        public void damage(float damage) {
            strength = Math.max(0, strength - damage);
        }
        
        public void tick() {
            if (remainingTicks > 0) {
                remainingTicks--;
            }
        }
    }
}












