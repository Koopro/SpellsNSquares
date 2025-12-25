package at.koopro.spells_n_squares.features.economy.block;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.economy.GringottsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

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
        
        // Open vault GUI screen
        serverPlayer.openMenu(new VaultMenuProvider(pos));
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Menu provider for vault GUI.
     */
    private static class VaultMenuProvider implements MenuProvider {
        private final BlockPos vaultPos;
        
        public VaultMenuProvider(BlockPos vaultPos) {
            this.vaultPos = vaultPos;
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.spells_n_squares.vault");
        }
        
        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new at.koopro.spells_n_squares.features.economy.VaultMenu(containerId, playerInventory, vaultPos);
        }
    }
}








