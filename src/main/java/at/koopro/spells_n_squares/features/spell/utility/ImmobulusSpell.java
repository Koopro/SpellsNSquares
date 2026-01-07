package at.koopro.spells_n_squares.features.spell.utility;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Immobulus - A freezing spell that immobilizes entities.
 */
public class ImmobulusSpell implements Spell {
    
    private static final int COOLDOWN = 60; // 3 seconds
    private static final double RANGE = 12.0;
    private static final int FREEZE_DURATION = 100; // 5 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("immobulus");
    }
    
    @Override
    public String getName() {
        return "Immobulus";
    }
    
    @Override
    public String getDescription() {
        return "A freezing spell that immobilizes entities";
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
        
        boolean frozen = false;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Apply freezing effects
                living.addEffect(new MobEffectInstance(
                    MobEffects.SLOWNESS,
                    FREEZE_DURATION,
                    127, // Maximum slowness level
                    false,
                    true,
                    true
                ));
                
                living.addEffect(new MobEffectInstance(
                    MobEffects.MINING_FATIGUE,
                    FREEZE_DURATION,
                    127,
                    false,
                    true,
                    true
                ));
                
                // Stop entity movement
                living.setDeltaMovement(0, 0, 0);
                living.hurtMarked = true;
                frozen = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                    20,
                    0.5, 0.5, 0.5,
                    0.05
                );
            }
        }
        
        if (frozen) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.8f, 0.5f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.ITEM_SNOWBALL,
                targetPos.x, targetPos.y, targetPos.z,
                15,
                1.0, 1.0, 1.0,
                0.05
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}
