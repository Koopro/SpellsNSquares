package at.koopro.spells_n_squares.features.environment.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Leaves block for custom tree types.
 * Extends LeavesBlock for proper decay mechanics.
 */
public class ModLeavesBlock extends LeavesBlock {
    
    public static final MapCodec<ModLeavesBlock> CODEC = simpleCodec(ModLeavesBlock::new);
    private static final float FALLING_LEAVES_CHANCE = 0.01f;
    
    public ModLeavesBlock(Properties properties) {
        super(FALLING_LEAVES_CHANCE, properties);
    }
    
    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }
    
    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // Spawn a simple leaf particle
        if (random.nextFloat() < 0.1f) {
            BlockPos belowPos = pos.below();
            if (level.getBlockState(belowPos).isAir()) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() - 0.05;
                double z = pos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
    
    /**
     * Creates default properties for a leaves block.
     */
    public static BlockBehaviour.Properties createDefaultProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .strength(0.2f)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor((state, level, pos) -> false);
    }
    
    /**
     * Creates properties for leaves with a specific color.
     */
    public static BlockBehaviour.Properties createDefaultProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(0.2f)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor((state, level, pos) -> false);
    }
}








