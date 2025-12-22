package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Whomping Willow - hostile tree that attacks nearby entities.
 */
public class WhompingWillowBlock extends Block {
    private static final int ATTACK_INTERVAL = 40; // Every 2 seconds
    private static final double ATTACK_RANGE = 4.0;
    
    public WhompingWillowBlock(Properties properties) {
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
                    living.hurt(level.damageSources().generic(), 4.0f);
                    
                    // Knockback
                    double dx = entity.getX() - pos.getX();
                    double dz = entity.getZ() - pos.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance > 0) {
                        entity.setDeltaMovement(
                            (dx / distance) * 0.5,
                            0.3,
                            (dz / distance) * 0.5
                        );
                    }
                    
                    // Visual and sound effects
                    level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                            entity.getX(), entity.getY() + 1.0, entity.getZ(),
                            10, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }
        }
    }
}
