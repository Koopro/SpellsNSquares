package at.koopro.spells_n_squares.features.artifacts.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.artifacts.item.RemembrallItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

/**
 * Handles Remembrall tracking of forgotten items.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class RemembrallHandler {
    
    @SubscribeEvent
    public static void onItemTossed(ItemTossEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        ItemEntity itemEntity = event.getEntity();
        ItemStack droppedStack = itemEntity.getItem();
        
        // Check if player has a Remembrall in inventory
        for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
            ItemStack stack = serverPlayer.getInventory().getItem(i);
            if (stack.getItem() instanceof RemembrallItem) {
                // Add the dropped item to forgotten items
                RemembrallItem.addForgottenItem(stack, droppedStack, serverPlayer.level().getGameTime());
                break; // Only track in one Remembrall
            }
        }
    }
}











