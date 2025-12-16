package at.koopro.spells_n_squares.block.resource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Item collector block that automatically collects nearby items.
 */
public class ItemCollectorBlock extends Block {
    
    private static final int COLLECTION_RADIUS = 8;
    
    public ItemCollectorBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            // Simplified: send message for now
            // Full implementation would show filter configuration
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.collector.description"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Collect nearby items periodically
        if (random.nextFloat() < 0.1f) {
            AABB searchArea = new AABB(pos).inflate(COLLECTION_RADIUS);
            var items = level.getEntitiesOfClass(ItemEntity.class, searchArea);
            
            for (ItemEntity item : items) {
                // Simplified: teleport items to collector
                // Full implementation would store in inventory
                Vec3 target = Vec3.atCenterOf(pos);
                item.teleportTo(target.x, target.y + 1.0, target.z);
                
                // Visual effect
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    target.x, target.y, target.z,
                    3, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
}

