package at.koopro.spells_n_squares.features.wand.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Wand Lathe block - a crafting station for creating wands.
 * Players can combine wand wood and cores to craft custom wands.
 */
public class WandLatheBlock extends Block {
    
    public WandLatheBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, 
                                                  net.minecraft.world.entity.player.Player player, 
                                                  BlockHitResult hitResult) {
        DevLogger.logBlockInteraction(this, "useWithoutItem", player, pos, state);
        DevLogger.logMethodEntry(this, "useWithoutItem", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null"));
        
        if (level.isClientSide()) {
            DevLogger.logMethodExit(this, "useWithoutItem", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            DevLogger.logMethodExit(this, "useWithoutItem", InteractionResult.PASS);
            return InteractionResult.PASS;
        }
        
        // Open wand lathe GUI screen
        DevLogger.logStateChange(this, "useWithoutItem", 
            "Opening wand lathe menu, pos=" + DevLogger.formatPos(pos));
        serverPlayer.openMenu(new WandLatheMenuProvider(pos));
        DevLogger.logMethodExit(this, "useWithoutItem", InteractionResult.SUCCESS);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Menu provider for wand lathe GUI.
     */
    private static class WandLatheMenuProvider implements net.minecraft.world.MenuProvider {
        private final BlockPos lathePos;
        
        public WandLatheMenuProvider(BlockPos lathePos) {
            this.lathePos = lathePos;
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.spells_n_squares.wand_lathe");
        }
        
        @Override
        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, 
                                                                              net.minecraft.world.entity.player.Inventory playerInventory, 
                                                                              net.minecraft.world.entity.player.Player player) {
            return new at.koopro.spells_n_squares.features.wand.WandLatheMenu(containerId, playerInventory, lathePos);
        }
    }
}




