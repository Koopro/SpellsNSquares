package at.koopro.spells_n_squares.features.mail.block;

import at.koopro.spells_n_squares.core.base.block.BaseModBlock;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Mailbox block - allows players to receive and store mail.
 */
public class MailboxBlock extends BaseModBlock implements EntityBlock {
    
    public MailboxBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MailboxBlockEntity(MailBlockEntities.MAILBOX_BLOCK_ENTITY.get(), pos, state);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                                 ServerPlayer player, InteractionHand hand,
                                                 BlockHitResult hit) {
        DevLogger.logBlockInteraction(this, "onServerInteract", player, pos, state);
        
        if (level.getBlockEntity(pos) instanceof MailboxBlockEntity) {
            MenuProvider menuProvider = new SimpleMenuProvider(
                (containerId, playerInventory, p) -> new at.koopro.spells_n_squares.features.mail.MailboxMenu(
                    containerId, playerInventory, ContainerLevelAccess.create(level, pos)
                ),
                Component.translatable("container.spells_n_squares.mailbox")
            );
            player.openMenu(menuProvider);
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}

