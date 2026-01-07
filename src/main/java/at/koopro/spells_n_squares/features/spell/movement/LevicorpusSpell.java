package at.koopro.spells_n_squares.features.spell.movement;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Levicorpus - A jinx that hangs the victim upside down by their ankle.
 */
public class LevicorpusSpell implements Spell {
    
    private static final int COOLDOWN = 60; // 3 seconds
    private static final double RANGE = 10.0;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("levicorpus");
    }
    
    @Override
    public String getName() {
        return "Levicorpus";
    }
    
    @Override
    public String getDescription() {
        return "A jinx that hangs the victim upside down by their ankle";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Find entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(Entity.class, searchBox,
            entity -> entity instanceof LivingEntity && 
                     entity != player && 
                     entity.isAlive() && 
                     !entity.isSpectator());
        
        boolean affected = false;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Lift entity up and invert them
                Vec3 currentPos = living.position();
                living.teleportTo(currentPos.x, currentPos.y + 2.0, currentPos.z);
                
                // Apply upward velocity to keep them hanging
                living.setDeltaMovement(0, 0.1, 0);
                living.hurtMarked = true;
                
                // Store hanging state (simplified - in full implementation would use data component)
                affected = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    entityPos.x, entityPos.y + entity.getBbHeight(), entityPos.z,
                    20,
                    0.3, 0.5, 0.3,
                    0.05
                );
            }
        }
        
        if (affected) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7f, 1.2f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                targetPos.x, targetPos.y + 1.0, targetPos.z,
                25,
                0.5, 1.0, 0.5,
                0.1
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}

