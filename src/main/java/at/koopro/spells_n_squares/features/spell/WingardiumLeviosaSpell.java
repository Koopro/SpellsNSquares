package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Wingardium Leviosa - The Levitation Charm that levitates items and small entities.
 * This is a hold-to-cast spell - hold the cast key to continuously move entities.
 */
public class WingardiumLeviosaSpell implements Spell {
    
    private static final int COOLDOWN = 0; // No cooldown for hold spells
    private static final double RANGE = 12.0; // blocks
    private static final double LEVITATION_SPEED = 0.2;
    
    // Per-player tracked entities (UUID -> Entity ID)
    private static final java.util.Map<java.util.UUID, java.util.Set<Integer>> trackedEntities = new java.util.HashMap<>();
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("wingardium_leviosa");
    }
    
    @Override
    public String getName() {
        return "Wingardium Leviosa";
    }
    
    @Override
    public String getDescription() {
        return "The Levitation Charm - levitates items and small entities";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean isHoldToCast() {
        return true;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        // Initial cast - find entities to track
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Find items and small entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(3.0);
        var entities = level.getEntitiesOfClass(Entity.class, searchBox,
            entity -> {
                if (entity == player || !entity.isAlive() || entity.isSpectator()) {
                    return false;
                }
                // Only affect items and small entities
                return entity instanceof ItemEntity || 
                       (entity.getBbWidth() < 1.0 && entity.getBbHeight() < 1.5);
            });
        
        java.util.UUID playerUUID = player.getUUID();
        java.util.Set<Integer> tracked = trackedEntities.computeIfAbsent(playerUUID, k -> new java.util.HashSet<>());
        
        // Track new entities
        for (Entity entity : entities) {
            tracked.add(entity.getId());
        }
        
        // Visual and audio feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.7f, 1.3f);
        
        Vec3 castPos = playerPos.add(lookVec.scale(1.0));
        serverLevel.sendParticles(
            ParticleTypes.END_ROD,
            castPos.x, castPos.y, castPos.z,
            15,
            0.3, 0.3, 0.3,
            0.05
        );
        
        return !entities.isEmpty();
    }
    
    /**
     * Cleans up tracked entities when spell stops.
     */
    public static void cleanupForPlayer(java.util.UUID playerUUID) {
        trackedEntities.remove(playerUUID);
    }
    
    /**
     * Shoots all tracked entities/blocks away from the player.
     * Called when player right-clicks while holding the spell.
     */
    public static void shootEntities(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        java.util.UUID playerUUID = player.getUUID();
        java.util.Set<Integer> tracked = trackedEntities.get(playerUUID);
        if (tracked == null || tracked.isEmpty()) {
            return; // No entities to shoot
        }
        
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 lookVec = player.getLookAngle();
        double shootSpeed = 1.5; // Speed to shoot entities away
        
        java.util.Iterator<Integer> iterator = tracked.iterator();
        while (iterator.hasNext()) {
            Integer entityId = iterator.next();
            Entity entity = level.getEntity(entityId);
            
            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }
            
            // Calculate direction away from player (in the direction player is looking)
            Vec3 entityPos = entity.position();
            Vec3 direction = lookVec.normalize();
            
            // Apply velocity to shoot entity away
            Vec3 velocity = direction.scale(shootSpeed);
            entity.setDeltaMovement(velocity);
            entity.hurtMarked = true;
            
            // Visual effect
            serverLevel.sendParticles(
                ParticleTypes.CLOUD,
                entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                5,
                0.2, 0.2, 0.2,
                0.05
            );
        }
        
        // Play sound effect
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.7f, 1.5f);
        
        // Clear tracked entities after shooting
        trackedEntities.remove(playerUUID);
    }
    
    @Override
    public boolean onHoldTick(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        java.util.UUID playerUUID = player.getUUID();
        java.util.Set<Integer> tracked = trackedEntities.get(playerUUID);
        if (tracked == null || tracked.isEmpty()) {
            return false; // No entities to move
        }
        
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        boolean hasActiveEntities = false;
        java.util.Iterator<Integer> iterator = tracked.iterator();
        
        while (iterator.hasNext()) {
            Integer entityId = iterator.next();
            Entity entity = level.getEntity(entityId);
            
            if (entity == null || !entity.isAlive() || entity.distanceTo(player) > RANGE * 2) {
                iterator.remove();
                continue;
            }
            
            // Move entity toward target position (where player is looking)
            Vec3 entityPos = entity.position();
            Vec3 direction = targetPos.subtract(entityPos);
            double distance = direction.length();
            
            if (distance > 0.1) {
                Vec3 movement = direction.normalize().scale(Math.min(LEVITATION_SPEED, distance * 0.1));
                entity.setDeltaMovement(movement);
                entity.hurtMarked = true;
                hasActiveEntities = true;
                
                // Visual effect trail - only spawn particles occasionally (every 10 ticks, staggered by entity ID) to avoid spam
                long gameTime = serverLevel.getGameTime();
                if ((gameTime + entityId) % 10 == 0) {
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANT,
                        entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                        1,
                        0.1, 0.1, 0.1,
                        0.01
                    );
                }
            } else {
                hasActiveEntities = true; // Entity is at target, still active
            }
        }
        
        // Clean up if no entities left
        if (!hasActiveEntities) {
            trackedEntities.remove(playerUUID);
            return false;
        }
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.4f;
    }
}












