package at.koopro.spells_n_squares.features.automation.block;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Resource generator block for automatic resource generation.
 */
public class ResourceGeneratorBlock extends Block {
    
    private static final int GENERATION_INTERVAL = 200; // 10 seconds
    
    public ResourceGeneratorBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            // Simplified: send message for now
            // Full implementation would show generation status
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.generator.description"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Generate resources periodically
        if (random.nextFloat() < 0.01f) {
            long gameTime = serverLevel.getGameTime();
            if (gameTime % GENERATION_INTERVAL == 0) {
                // Spawn a resource item (simplified)
                Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 1.0, 0);
                ItemStack resource = new ItemStack(Items.IRON_INGOT);
                ItemEntity itemEntity = 
                    new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, resource);
                level.addFreshEntity(itemEntity);
                
                // Visual effect
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    20, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }
}



