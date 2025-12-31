package at.koopro.spells_n_squares.features.automation.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Magical farm block for automated crop management.
 */
public class MagicalFarmBlock extends BaseInteractiveBlock {
    
    private static final int HARVEST_RADIUS = 5;
    
    public MagicalFarmBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.farm.description";
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn growth particles periodically
        if (random.nextFloat() < 0.05f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                center.x, center.y + 0.5, center.z,
                5, 0.3, 0.1, 0.3, 0.01);
        }
    }
}













