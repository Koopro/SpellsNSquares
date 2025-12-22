package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Mandrake Plant - screams when harvested, stunning nearby entities.
 */
public class MandrakePlantBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    
    public MandrakePlantBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }
    
    @Override
    public int getMaxAge() {
        return 7;
    }
    
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, 
                              net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        // Scream when harvested
        if (state.getValue(AGE) >= getMaxAge()) {
            scream(level, pos);
        }
        
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
    
    /**
     * Screams and stuns nearby entities.
     */
    private void scream(Level level, BlockPos pos) {
        // Play scream sound
        level.playSound(null, pos, SoundEvents.VILLAGER_HURT, SoundSource.NEUTRAL, 2.0f, 0.5f);
        
        if (level instanceof ServerLevel serverLevel) {
            // Stun nearby entities
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(pos).inflate(8.0))) {
                    entity.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        100, // 5 seconds
                        3,   // Level 4 (very slow)
                        false,
                        true,
                        true
                    ));
                
                // Visual effect
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
            }
            
            // Scream particles
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
