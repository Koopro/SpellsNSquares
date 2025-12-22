package at.koopro.spells_n_squares.features.education;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Menu provider for the bestiary GUI.
 */
public class BestiaryMenuProvider implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.spells_n_squares.bestiary");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BestiaryMenu(containerId, playerInventory);
    }
}






