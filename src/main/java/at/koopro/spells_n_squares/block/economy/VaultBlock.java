package at.koopro.spells_n_squares.block.economy;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.economy.GringottsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Vault block for Gringotts bank storage.
 */
public class VaultBlock extends BaseInteractiveBlock {
    
    public VaultBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        // Set vault location for player
        GringottsSystem.VaultData vault = GringottsSystem.getVault(serverPlayer);
        vault.setVaultLocation(pos);
        
        // TODO: Open vault GUI screen
        // - Create VaultMenu and VaultScreen classes
        // - Use MenuProvider pattern: serverPlayer.openMenu(new VaultMenuProvider(pos))
        // - Display currency storage interface with deposit/withdraw functionality
        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.vault.description"));
        return InteractionResult.SUCCESS;
    }
}






