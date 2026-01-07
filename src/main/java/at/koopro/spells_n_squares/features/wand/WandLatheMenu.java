package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.core.menu.BaseModMenu;
import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.math.PositionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Container menu for the wand lathe GUI.
 * Handles wand crafting by combining wood type and core.
 */
public class WandLatheMenu extends BaseModMenu {
    private final BlockPos lathePos;
    
    // Client-side constructor
    public WandLatheMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.WAND_LATHE_MENU.get(), containerId, playerInventory);
        this.lathePos = buffer.readBlockPos();
        DevLogger.logMethodEntry(this, "WandLatheMenu", 
            "containerId=" + containerId + 
            ", lathePos=" + DevLogger.formatPos(lathePos) + " (client-side)");
    }
    
    // Server-side constructor
    public WandLatheMenu(int containerId, Inventory playerInventory, BlockPos lathePos) {
        super(ModMenus.WAND_LATHE_MENU.get(), containerId, playerInventory);
        this.lathePos = lathePos;
        DevLogger.logMethodEntry(this, "WandLatheMenu", 
            "containerId=" + containerId + 
            ", lathePos=" + DevLogger.formatPos(lathePos) + " (server-side)");
    }
    
    @Override
    protected boolean isValid(Player player) {
        DevLogger.logMethodEntry(this, "isValid", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", lathePos=" + DevLogger.formatPos(lathePos));
        // Check if player is still near the wand lathe
        if (lathePos == null) {
            DevLogger.logMethodExit(this, "isValid", false);
            return false;
        }
        boolean result = MathUtils.distanceSquared(player.position(), PositionUtils.toVec3(lathePos)) <= 64.0;
        DevLogger.logReturnValue(this, "isValid", result);
        return result;
    }
    
    public BlockPos getLathePos() {
        return lathePos;
    }
    
    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        // Wand lathe menu doesn't have item slots, so no quick move needed
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}







