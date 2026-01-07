package at.koopro.spells_n_squares.features.spell.combat;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Stupefy - The Stunning Spell that stuns and knocks back opponents.
 */
public class StupefySpell implements Spell {
    
    private static final int COOLDOWN = 80; // 4 seconds
    private static final double RANGE = 20.0; // blocks
    private static final int STUN_DURATION = 100; // 5 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("stupefy");
    }
    
    @Override
    public String getName() {
        return "Stupefy";
    }
    
    @Override
    public String getDescription() {
        return "The Stunning Spell - stuns and knocks back opponents";
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
        
        Vec3 playerPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Find entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());
        
        boolean stunned = false;
        for (LivingEntity target : entities) {
            // Apply stunning effect (slowness + weakness)
            target.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                STUN_DURATION,
                3, // Level 4 (very slow)
                false,
                true,
                true
            ));
            
            target.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                STUN_DURATION,
                1, // Level 2
                false,
                true,
                true
            ));
            
            // Knockback effect
            Vec3 knockback = lookVec.scale(0.8).add(0, 0.3, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
            
            stunned = true;
            
            // Visual effect
            Vec3 entityPos = target.position().add(0, target.getBbHeight() / 2, 0);
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.ENCHANT,
                entityPos,
                30,
                0.8, 0.8, 0.8,
                0.15
            );
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.ELECTRIC_SPARK,
                entityPos,
                20,
                0.5, 0.5, 0.5,
                0.1
            );
        }
        
        if (stunned) {
            // Audio feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.5f, 1.5f);
            
            // Visual effect at cast point
            Vec3 castPos = playerPos.add(lookVec.scale(1.0));
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.ENCHANT,
                castPos,
                20,
                0.3, 0.3, 0.3,
                0.1
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.7f;
    }
    
    @Override
    public void spawnCastEffects(Player player, Level level, boolean success) {
        // Call default implementation
        Spell.super.spawnCastEffects(player, level, success);
        
        if (success && level.isClientSide()) {
            // Apply chromatic aberration for stunning spell (disorientation effect)
            if (Config.areShaderEffectsEnabled()) {
                ShaderEffectHandler.triggerChromaticAberration(0.4f);
            }
        }
    }
}

