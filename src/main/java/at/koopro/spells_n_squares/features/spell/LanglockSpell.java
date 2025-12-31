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
 * Langlock - A jinx that glues the target's tongue to the roof of their mouth.
 */
public class LanglockSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 10.0;
    private static final int SILENCE_DURATION = 150; // 7.5 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("langlock");
    }
    
    @Override
    public String getName() {
        return "Langlock";
    }
    
    @Override
    public String getDescription() {
        return "A jinx that glues the target's tongue, preventing speech";
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
                // Apply silence effect (using weakness as proxy)
                living.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    SILENCE_DURATION,
                    0,
                    false,
                    true,
                    true
                ));
                
                affected = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.ITEM_SLIME,
                    entityPos.x, entityPos.y + entity.getBbHeight() * 0.8, entityPos.z,
                    15,
                    0.2, 0.2, 0.2,
                    0.02
                );
            }
        }
        
        if (affected) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.SLIME_ATTACK, SoundSource.PLAYERS, 0.6f, 0.8f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.ITEM_SLIME,
                targetPos.x, targetPos.y, targetPos.z,
                20,
                0.5, 0.5, 0.5,
                0.05
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.3f;
    }
}














