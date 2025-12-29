package at.koopro.spells_n_squares.features.automation.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Magical furnace block with fast smelting.
 */
public class MagicalFurnaceBlock extends BaseInteractiveBlock {
    
    public MagicalFurnaceBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.furnace.description";
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn magical flame particles
        if (random.nextFloat() < 0.2f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.FLAME,
                center.x, center.y + 0.5, center.z,
                3, 0.2, 0.1, 0.2, 0.01);
        }
    }
}












