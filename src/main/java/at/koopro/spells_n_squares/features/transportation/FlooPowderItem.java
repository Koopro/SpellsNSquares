package at.koopro.spells_n_squares.features.transportation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Floo Powder item for activating Floo Network travel.
 */
public class FlooPowderItem extends Item {
    
    public FlooPowderItem(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(Level level, Player player, InteractionHand hand, 
                                 net.minecraft.world.phys.BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        BlockPos pos = hitResult.getBlockPos();
        
        if (!isFireplace(level, pos)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.floo_powder.fireplace_only"));
            return InteractionResult.FAIL;
        }
        
        activateFlooNetwork(serverLevel, serverPlayer, pos);
        
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    private boolean isFireplace(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return state.is(Blocks.FURNACE) || 
               state.is(Blocks.CAMPFIRE) || 
               state.is(Blocks.SOUL_CAMPFIRE) ||
               state.is(Blocks.FIRE) ||
               state.is(Blocks.SOUL_FIRE);
    }
    
    private void activateFlooNetwork(ServerLevel level, ServerPlayer player, BlockPos fireplacePos) {
        FlooNetworkManager.FlooLocation location = new FlooNetworkManager.FlooLocation(
            level.dimension(),
            fireplacePos,
            "Fireplace at " + fireplacePos.getX() + ", " + fireplacePos.getY() + ", " + fireplacePos.getZ()
        );
        FlooNetworkManager.registerFireplace(level.dimension(), fireplacePos, location.name());
        
        Vec3 pos = Vec3.atCenterOf(fireplacePos);
        level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 50, 0.3, 0.5, 0.3, 0.1);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 30, 0.2, 0.3, 0.2, 0.05);
        
        var connected = FlooNetworkManager.getConnectedFireplaces(location);
        if (connected.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.floo_powder.activated_no_destinations"));
        } else {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.floo_powder.activated_destinations", connected.size()));
        }
    }
}




















