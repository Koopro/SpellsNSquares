package at.koopro.spells_n_squares.features.artifact.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

/**
 * Elixir Base fluid - created from Gold Block + Magma Cream in water cauldron.
 * Can be placed in world and cauldrons, and collected with buckets.
 * Based on the Colored-Water mod approach for fluid behavior.
 */
public abstract class ElixirBaseFluid extends FlowingFluid {
    
    @Override
    public Fluid getFlowing() {
        return ElixirFluids.ELIXIR_BASE_FLOWING.get();
    }
    
    @Override
    public Fluid getSource() {
        return ElixirFluids.ELIXIR_BASE.get();
    }
    
    @Override
    public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource random) {
        if (!fluidState.isSource() && !fluidState.getValue(FlowingFluid.FALLING)) {
            if (random.nextInt(64) == 0) {
                level.playLocalSound(
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5,
                    SoundEvents.WATER_AMBIENT,
                    SoundSource.BLOCKS,
                    random.nextFloat() * 0.25F + 0.75F,
                    random.nextFloat() + 0.5F,
                    false
                );
            }
        } else if (random.nextInt(10) == 0) {
            level.addParticle(
                ParticleTypes.UNDERWATER,
                blockPos.getX() + random.nextDouble(),
                blockPos.getY() + random.nextDouble(),
                blockPos.getZ() + random.nextDouble(),
                0.0,
                0.0,
                0.0
            );
        }
    }

    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }
    
    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        // Elixirs cannot convert to source blocks
        return false;
    }
    
    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockentity);
    }
    
    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 4;
    }
    
    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }
    
    @Override
    public int getAmount(FluidState state) {
        return state.getValue(LEVEL);
    }
    
    @Override
    public boolean isSource(FluidState state) {
        return state.getValue(LEVEL) == 8;
    }
    
    @Override
    public int getTickDelay(LevelReader level) {
        return 5; // Slower than water
    }
    
    @Override
    protected float getExplosionResistance() {
        return 100.0f; // Cannot be destroyed by explosions
    }
    
    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ElixirFluids.ELIXIR_BASE_BLOCK.get().defaultBlockState()
            .setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    
    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ElixirFluids.ELIXIR_BASE.get() || fluid == ElixirFluids.ELIXIR_BASE_FLOWING.get();
    }
    
    @Override
    public FluidType getFluidType() {
        return ElixirFluids.ELIXIR_BASE_TYPE.get();
    }
    
    @Override
    public Item getBucket() {
        return ElixirFluids.ELIXIR_BASE_BUCKET.get();
    }
    
    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !state.getType().isSame(fluid);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }
    
    @Override
    public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty(); // Full block
    }
    
    public static class Flowing extends ElixirBaseFluid {
        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        
        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
        
        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
    
    public static class Source extends ElixirBaseFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
        
        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}

