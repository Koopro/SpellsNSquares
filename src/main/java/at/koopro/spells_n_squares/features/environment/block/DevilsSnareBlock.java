package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Devil's Snare - entangling plant that slows entities and can be destroyed by light/fire.
 */
public class DevilsSnareBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    
    public DevilsSnareBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(ACTIVE)) {
            super.stepOn(level, pos, state, entity);
            return;
        }
        
        if (entity instanceof LivingEntity living && !level.isClientSide()) {
            // Apply slowness
            living.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                60,
                2, // Level 3
                false,
                true,
                true
            ));
            
            // Visual effect
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    entity.getX(), entity.getY(), entity.getZ(),
                    3, 0.2, 0.2, 0.2, 0.02);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
    
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        // Check for light level - destroy if too bright
        if (level.getMaxLocalRawBrightness(pos) > 12) {
            level.destroyBlock(pos, false);
        }
        
        // Check for fire nearby
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.FIRE)) {
                level.destroyBlock(pos, false);
                return;
            }
        }
    }
}
