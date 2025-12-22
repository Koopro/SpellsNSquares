package at.koopro.spells_n_squares.features.storage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Menu provider for the enchanted bag.
 */
public class BagMenuProvider implements MenuProvider {
    private final ItemStack bagStack;
    
    public BagMenuProvider(ItemStack bagStack) {
        this.bagStack = bagStack;
    }
    
    @Override
    public Component getDisplayName() {
        if (bagStack.getItem() instanceof EnchantedBagItem bagItem) {
            return Component.translatable("container.spells_n_squares.bag", bagItem.getTier().getName());
        }
        return Component.translatable("container.spells_n_squares.bag");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BagMenu(containerId, playerInventory, bagStack);
    }
}








