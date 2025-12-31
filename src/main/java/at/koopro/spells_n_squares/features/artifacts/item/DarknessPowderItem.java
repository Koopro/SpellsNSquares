package at.koopro.spells_n_squares.features.artifacts.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Peruvian Instant Darkness Powder - escape tool from Weasley's Wizard Wheezes.
 * Creates a cloud of darkness that blinds nearby entities.
 */
public class DarknessPowderItem extends Item {
    
    private static final double EFFECT_RANGE = 8.0;
    private static final int BLINDNESS_DURATION = 200; // 10 seconds
    
    public DarknessPowderItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            Vec3 lookVec = player.getLookAngle();
            Vec3 throwPos = pos.add(lookVec.scale(2.0));
            
            // Create darkness cloud
            AABB effectArea = new AABB(throwPos, throwPos).inflate(EFFECT_RANGE);
            var entities = level.getEntitiesOfClass(LivingEntity.class, effectArea,
                entity -> entity != player && entity.isAlive());
            
            for (LivingEntity entity : entities) {
                // Apply blindness
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_DURATION, 0));
            }
            
            // Visual effect - dark particles
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                throwPos.x, throwPos.y, throwPos.z,
                100, EFFECT_RANGE, 2.0, EFFECT_RANGE, 0.1);
            
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                throwPos.x, throwPos.y, throwPos.z,
                50, EFFECT_RANGE * 0.5, 1.0, EFFECT_RANGE * 0.5, 0.05);
            
            level.playSound(null, throwPos.x, throwPos.y, throwPos.z,
                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 0.5f);
            
            // Consume item
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}
















