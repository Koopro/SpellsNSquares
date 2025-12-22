package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Container menu for the bestiary GUI.
 * This is a simple menu with no inventory slots - just for GUI display.
 */
public class BestiaryMenu extends AbstractContainerMenu {
    private final Player player;
    
    // Client-side constructor
    public BestiaryMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.BESTIARY_MENU.get(), containerId);
        this.player = playerInventory.player;
    }
    
    // Server-side constructor
    public BestiaryMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.BESTIARY_MENU.get(), containerId);
        this.player = playerInventory.player;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true; // Bestiary can always be opened
    }
    
    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY; // No slots to move items
    }
    
    public Player getPlayer() {
        return player;
    }
}






