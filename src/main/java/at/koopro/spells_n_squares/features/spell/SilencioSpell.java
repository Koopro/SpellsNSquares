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
 * Silencio - A silencing spell that prevents entities from making sounds.
 */
public class SilencioSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 10.0;
    private static final int SILENCE_DURATION = 200; // 10 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("silencio");
    }
    
    @Override
    public String getName() {
        return "Silencio";
    }
    
    @Override
    public String getDescription() {
        return "A silencing spell that prevents entities from making sounds";
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
        
        boolean silenced = false;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Apply silence effect (using weakness as a proxy for silence)
                // In a full implementation, this would prevent sound generation
                living.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    SILENCE_DURATION,
                    0,
                    false,
                    true,
                    true
                ));
                
                silenced = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                    15,
                    0.3, 0.3, 0.3,
                    0.02
                );
            }
        }
        
        if (silenced) {
            // Audio feedback (ironically, the spell makes a sound)
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.5f, 0.1f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.CLOUD,
                targetPos.x, targetPos.y, targetPos.z,
                20,
                1.0, 1.0, 1.0,
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

