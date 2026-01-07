package at.koopro.spells_n_squares.features.economy.block;

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
 * Vault block - provides secure storage with 54 slots.
 * Players can store valuable items and currency safely.
 */
public class VaultBlock extends BaseModBlock implements EntityBlock {
    
    public VaultBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(EconomyBlockEntities.VAULT_BLOCK_ENTITY.get(), pos, state);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                                 ServerPlayer player, InteractionHand hand,
                                                 BlockHitResult hit) {
        DevLogger.logBlockInteraction(this, "onServerInteract", player, pos, state);
        
        if (level.getBlockEntity(pos) instanceof VaultBlockEntity) {
            MenuProvider menuProvider = new SimpleMenuProvider(
                (containerId, playerInventory, p) -> new at.koopro.spells_n_squares.features.economy.VaultMenu(
                    containerId, playerInventory, ContainerLevelAccess.create(level, pos)
                ),
                Component.translatable("container.spells_n_squares.vault")
            );
            player.openMenu(menuProvider);
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}

