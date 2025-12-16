package at.koopro.spells_n_squares.features.automation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for auto-harvesting tools.
 */
public class AutoHarvestTool extends Item {
    
    public AutoHarvestTool(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        Player player = context.getPlayer();
        
        if (isCrop(state)) {
            harvestAndReplant(context.getLevel(), pos, state, player);
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    private boolean isCrop(BlockState state) {
        return state.is(Blocks.WHEAT) ||
               state.is(Blocks.CARROTS) ||
               state.is(Blocks.POTATOES) ||
               state.is(Blocks.BEETROOTS);
    }
    
    private void harvestAndReplant(Level level, BlockPos pos, BlockState state, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        state.getBlock().playerDestroy(level, player, pos, state, null, player.getMainHandItem());
        level.setBlock(pos, state.getBlock().defaultBlockState(), 3);
        
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.harvest.replanted"));
        }
    }
}








