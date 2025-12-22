package at.koopro.spells_n_squares.features.transportation.item;

import at.koopro.spells_n_squares.features.transportation.FlooNetworkManager;
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
import net.minecraft.world.phys.BlockHitResult;
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
        
        // Check if target block is a fireplace (furnace, campfire, or fire)
        if (!isFireplace(level, pos)) {
            serverPlayer.sendSystemMessage(Component.literal("Floo Powder can only be used on fireplaces"));
            return InteractionResult.FAIL;
        }
        
        // Activate Floo Network travel
        activateFlooNetwork(serverLevel, serverPlayer, pos);
        
        // Consume one powder
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Checks if a block position is a valid fireplace.
     */
    private boolean isFireplace(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return state.is(Blocks.FURNACE) || 
               state.is(Blocks.CAMPFIRE) || 
               state.is(Blocks.SOUL_CAMPFIRE) ||
               state.is(Blocks.FIRE) ||
               state.is(Blocks.SOUL_FIRE);
    }
    
    /**
     * Activates Floo Network travel.
     */
    private void activateFlooNetwork(ServerLevel level, ServerPlayer player, BlockPos fireplacePos) {
        // Register fireplace if not already registered
        FlooNetworkManager.FlooLocation location = new FlooNetworkManager.FlooLocation(
            level.dimension(),
            fireplacePos,
            "Fireplace at " + fireplacePos.getX() + ", " + fireplacePos.getY() + ", " + fireplacePos.getZ()
        );
        FlooNetworkManager.registerFireplace(level.dimension(), fireplacePos, location.name());
        
        // Visual effect: green flames
        Vec3 pos = Vec3.atCenterOf(fireplacePos);
        level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 50, 0.3, 0.5, 0.3, 0.1);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 30, 0.2, 0.3, 0.2, 0.05);
        
        // Show available destinations
        var connected = FlooNetworkManager.getConnectedFireplaces(location);
        if (connected.isEmpty()) {
            player.sendSystemMessage(Component.literal("Floo Network activated! No connected destinations yet."));
        } else {
            player.sendSystemMessage(Component.literal("Floo Network activated! Connected to " + connected.size() + " destinations."));
        }
    }
}

