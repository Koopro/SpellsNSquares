package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
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
 * Tarantallegra - The Dancing Feet Spell that causes uncontrollable dancing.
 */
public class TarantallegraSpell implements Spell {
    
    private static final int COOLDOWN = 50; // 2.5 seconds
    private static final double RANGE = 10.0;
    private static final int DANCE_DURATION = 200; // 10 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("tarantallegra");
    }
    
    @Override
    public String getName() {
        return "Tarantallegra";
    }
    
    @Override
    public String getDescription() {
        return "The Dancing Feet Spell - causes uncontrollable dancing";
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
            entity -> entity instanceof LivingEntity living && 
                     entity != player && 
                     entity.isAlive() && 
                     !entity.isSpectator());
        
        boolean affected = false;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Apply dancing effect (nausea + random movement)
                living.addEffect(new MobEffectInstance(
                    MobEffects.NAUSEA,
                    DANCE_DURATION,
                    2, // Strong nausea
                    false,
                    true,
                    true
                ));
                
                // Add slowness to make dancing more noticeable
                living.addEffect(new MobEffectInstance(
                    MobEffects.SLOWNESS,
                    DANCE_DURATION,
                    1,
                    false,
                    true,
                    true
                ));
                
                affected = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.NOTE,
                    entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                    20,
                    0.5, 0.5, 0.5,
                    0.05
                );
            }
        }
        
        if (affected) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.NOTE,
                targetPos.x, targetPos.y, targetPos.z,
                25,
                1.0, 1.0, 1.0,
                0.1
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
