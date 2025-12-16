package at.koopro.spells_n_squares.block.resource;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Magical composter block with fast composting.
 */
public class MagicalComposterBlock extends BaseInteractiveBlock {
    
    public MagicalComposterBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.composter.description";
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn composting particles
        if (random.nextFloat() < 0.2f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                center.x, center.y, center.z,
                3, 0.2, 0.1, 0.2, 0.01);
        }
    }
}

