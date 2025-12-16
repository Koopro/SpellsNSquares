package at.koopro.spells_n_squares.block.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Venomous Tentacula - hostile plant that attacks nearby entities with tentacles.
 */
public class VenomousTentaculaBlock extends Block {
    private static final int ATTACK_INTERVAL = 60; // Every 3 seconds
    private static final double ATTACK_RANGE = 3.0;
    
    public VenomousTentaculaBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        // Attack nearby entities periodically
        if (level.getGameTime() % ATTACK_INTERVAL == 0) {
            AABB attackBox = new AABB(pos).inflate(ATTACK_RANGE);
            
            for (Entity entity : level.getEntitiesOfClass(Entity.class, attackBox)) {
                if (entity instanceof LivingEntity living && entity != null) {
                    // Deal damage
                    living.hurt(level.damageSources().magic(), 3.0f);
                    
                    // Apply poison effect
                    living.addEffect(new MobEffectInstance(
                        MobEffects.POISON,
                        200, // 10 seconds
                        1,   // Level 2
                        false,
                        true,
                        true
                    ));
                    
                    // Visual tentacle effect
                    level.sendParticles(ParticleTypes.ITEM_SLIME,
                        living.getX(), living.getY() + 1.0, living.getZ(),
                        15, 0.4, 0.4, 0.4, 0.05);
                    
                    // Sound effect
                    level.playSound(null, pos, SoundEvents.SPIDER_AMBIENT, SoundSource.BLOCKS, 0.8f, 0.6f);
                }
            }
            
            // Tentacle animation particles
            level.sendParticles(ParticleTypes.ITEM_SLIME,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5, 0.3, 0.3, 0.3, 0.02);
        }
        
        // Check for fire nearby - destroy if on fire
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.FIRE)) {
                level.destroyBlock(pos, false);
                return;
            }
        }
    }
    
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        // Apply slow movement when stepping on
        if (entity instanceof LivingEntity living && !level.isClientSide()) {
            living.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                40,
                1, // Level 2
                false,
                true,
                true
            ));
        }
    }
}
