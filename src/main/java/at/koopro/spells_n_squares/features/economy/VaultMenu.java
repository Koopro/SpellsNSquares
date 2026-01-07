package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.core.menu.BaseModMenu;
import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.math.PositionUtils;
import at.koopro.spells_n_squares.features.economy.block.VaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the Vault block.
 * Provides secure storage with multiple slots.
 */
public class VaultMenu extends BaseModMenu {
    private final ContainerLevelAccess access;
    private final BlockPos vaultPos;
    private static final int VAULT_SLOTS = 54; // 6 rows x 9 columns
    
    // Client-side constructor
    public VaultMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.VAULT_MENU.get(), containerId, playerInventory);
        this.vaultPos = buffer.readBlockPos();
        this.access = ContainerLevelAccess.NULL;
        DevLogger.logMethodEntry(this, "VaultMenu", 
            "containerId=" + containerId + 
            ", vaultPos=" + DevLogger.formatPos(vaultPos) + " (client-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 140);
    }
    
    // Server-side constructor
    public VaultMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.VAULT_MENU.get(), containerId, playerInventory);
        this.access = access;
        this.vaultPos = access.evaluate((level, pos) -> pos).orElse(BlockPos.ZERO);
        DevLogger.logMethodEntry(this, "VaultMenu", 
            "containerId=" + containerId + 
            ", vaultPos=" + DevLogger.formatPos(vaultPos) + " (server-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 140);
    }
    
    @Override
    protected void addCustomSlots() {
        // Vault storage slots (6 rows x 9 columns)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(getVaultInventory(), col + row * 9, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return true; // Allow any item in vault
                    }
                });
            }
        }
    }
    
    /**
     * Gets the vault inventory from the BlockEntity.
     */
    private net.minecraft.world.Container getVaultInventory() {
        return access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VaultBlockEntity blockEntity) {
                return blockEntity.getInventory();
            }
            // Fallback: return empty container if BlockEntity not found
            return new net.minecraft.world.SimpleContainer(VAULT_SLOTS);
        }).orElse(new net.minecraft.world.SimpleContainer(VAULT_SLOTS));
    }
    
    @Override
    protected boolean isValid(Player player) {
        DevLogger.logMethodEntry(this, "isValid", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", vaultPos=" + DevLogger.formatPos(vaultPos));
        // Check if player is still near the vault
        if (vaultPos == null) {
            DevLogger.logMethodExit(this, "isValid", false);
            return false;
        }
        boolean result = MathUtils.distanceSquared(player.position(), PositionUtils.toVec3(vaultPos)) <= 64.0;
        DevLogger.logReturnValue(this, "isValid", result);
        return result;
    }
    
    @Override
    protected int getContainerStartSlot() {
        return 0;
    }
    
    @Override
    protected int getContainerEndSlot() {
        return VAULT_SLOTS;
    }
    
    @Override
    protected int getPlayerInventoryStartSlot() {
        return VAULT_SLOTS;
    }
    
    @Override
    protected int getPlayerInventoryEndSlot() {
        return VAULT_SLOTS + 36;
    }
    
    public BlockPos getVaultPos() {
        return vaultPos;
    }
}

