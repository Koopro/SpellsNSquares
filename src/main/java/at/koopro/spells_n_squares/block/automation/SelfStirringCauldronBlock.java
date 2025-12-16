package at.koopro.spells_n_squares.block.automation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Self-stirring cauldron block for automated potion brewing.
 */
public class SelfStirringCauldronBlock extends Block {
    
    public SelfStirringCauldronBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            // Simplified: send message for now
            // Full implementation would open brewing interface
            serverPlayer.sendSystemMessage(Component.literal("Self-Stirring Cauldron - Automated brewing"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn bubbling particles
        if (random.nextFloat() < 0.1f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.BUBBLE,
                center.x, center.y, center.z,
                2, 0.2, 0.1, 0.2, 0.01);
        }
    }
}

