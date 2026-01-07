package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.core.menu.BaseModMenu;
import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.math.PositionUtils;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the Enchantment Table block.
 * Allows players to enchant items.
 */
public class EnchantmentTableMenu extends BaseModMenu {
    private final ContainerLevelAccess access;
    private final BlockPos tablePos;
    
    // Client-side constructor
    public EnchantmentTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.ENCHANTMENT_TABLE_MENU.get(), containerId, playerInventory);
        this.tablePos = buffer.readBlockPos();
        this.access = ContainerLevelAccess.NULL;
        DevLogger.logMethodEntry(this, "EnchantmentTableMenu", 
            "containerId=" + containerId + 
            ", tablePos=" + DevLogger.formatPos(tablePos) + " (client-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 84);
    }
    
    // Server-side constructor
    public EnchantmentTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.ENCHANTMENT_TABLE_MENU.get(), containerId, playerInventory);
        this.access = access;
        this.tablePos = access.evaluate((level, pos) -> pos).orElse(BlockPos.ZERO);
        DevLogger.logMethodEntry(this, "EnchantmentTableMenu", 
            "containerId=" + containerId + 
            ", tablePos=" + DevLogger.formatPos(tablePos) + " (server-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 84);
    }
    
    @Override
    protected void addCustomSlots() {
        // Item slot for item to enchant
        this.addSlot(new Slot(getTableInventory(), 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Allow any item to be enchanted
            }
        });
    }
    
    /**
     * Gets the enchantment table inventory from the BlockEntity.
     */
    private net.minecraft.world.Container getTableInventory() {
        return access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof EnchantmentTableBlockEntity blockEntity) {
                return blockEntity.getItemContainer();
            }
            // Fallback: return empty container if BlockEntity not found
            return new net.minecraft.world.SimpleContainer(1);
        }).orElse(new net.minecraft.world.SimpleContainer(1));
    }
    
    @Override
    protected boolean isValid(Player player) {
        DevLogger.logMethodEntry(this, "isValid", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", tablePos=" + DevLogger.formatPos(tablePos));
        // Check if player is still near the enchantment table
        if (tablePos == null) {
            DevLogger.logMethodExit(this, "isValid", false);
            return false;
        }
        boolean result = MathUtils.distanceSquared(player.position(), PositionUtils.toVec3(tablePos)) <= 64.0;
        DevLogger.logReturnValue(this, "isValid", result);
        return result;
    }
    
    @Override
    protected int getContainerStartSlot() {
        return 0;
    }
    
    @Override
    protected int getContainerEndSlot() {
        return 1;
    }
    
    @Override
    protected int getPlayerInventoryStartSlot() {
        return 1;
    }
    
    @Override
    protected int getPlayerInventoryEndSlot() {
        return 37;
    }
    
    public BlockPos getTablePos() {
        return tablePos;
    }
}

