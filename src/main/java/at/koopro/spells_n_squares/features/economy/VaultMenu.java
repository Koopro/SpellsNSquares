package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Container menu for the vault GUI.
 * Vaults store currency, not items, so this is a simple menu for UI purposes.
 */
public class VaultMenu extends AbstractContainerMenu {
    private final BlockPos vaultPos;
    private final Player player;
    
    // Client-side constructor
    public VaultMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.VAULT_MENU.get(), containerId);
        this.vaultPos = buffer.readBlockPos();
        this.player = playerInventory.player;
    }
    
    // Server-side constructor
    public VaultMenu(int containerId, Inventory playerInventory, BlockPos vaultPos) {
        super(ModMenus.VAULT_MENU.get(), containerId);
        this.vaultPos = vaultPos;
        this.player = playerInventory.player;
    }
    
    @Override
    public boolean stillValid(Player player) {
        // Check if player is still near the vault
        if (vaultPos == null) {
            return false;
        }
        return player.distanceToSqr(vaultPos.getX() + 0.5, vaultPos.getY() + 0.5, vaultPos.getZ() + 0.5) <= 64.0;
    }
    
    public BlockPos getVaultPos() {
        return vaultPos;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        // Vault menu doesn't have item slots, so no quick move needed
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}











