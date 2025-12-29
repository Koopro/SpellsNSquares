package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Container menu for the wand lathe GUI.
 * Handles wand crafting by combining wood type and core.
 */
public class WandLatheMenu extends AbstractContainerMenu {
    private final BlockPos lathePos;
    private final Player player;
    
    // Client-side constructor
    public WandLatheMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.WAND_LATHE_MENU.get(), containerId);
        this.lathePos = buffer.readBlockPos();
        this.player = playerInventory.player;
    }
    
    // Server-side constructor
    public WandLatheMenu(int containerId, Inventory playerInventory, BlockPos lathePos) {
        super(ModMenus.WAND_LATHE_MENU.get(), containerId);
        this.lathePos = lathePos;
        this.player = playerInventory.player;
    }
    
    @Override
    public boolean stillValid(Player player) {
        // Check if player is still near the wand lathe
        if (lathePos == null) {
            return false;
        }
        return player.distanceToSqr(lathePos.getX() + 0.5, lathePos.getY() + 0.5, lathePos.getZ() + 0.5) <= 64.0;
    }
    
    public BlockPos getLathePos() {
        return lathePos;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        // Wand lathe menu doesn't have item slots, so no quick move needed
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}




