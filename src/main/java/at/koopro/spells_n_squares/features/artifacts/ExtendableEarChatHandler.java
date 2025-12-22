package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.List;

/**
 * Handles chat interception for Extendable Ear items.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ExtendableEarChatHandler {
    
    private static final double LISTEN_RANGE = 32.0;
    
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer sender = event.getPlayer();
        if (sender == null || !(sender.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        Component message = event.getMessage();
        
        // Find all players with Extendable Ear in range
        List<ServerPlayer> nearbyPlayers = serverLevel.getPlayers(p -> 
            p != sender && 
            p.isAlive() && 
            sender.distanceToSqr(p) <= LISTEN_RANGE * LISTEN_RANGE &&
            hasExtendableEar(p)
        );
        
        // Send intercepted message to players with Extendable Ear
        for (ServerPlayer listener : nearbyPlayers) {
            Component interceptedMessage = Component.translatable(
                "message.spells_n_squares.extendable_ear.intercepted",
                sender.getDisplayName(),
                message
            );
            listener.sendSystemMessage(interceptedMessage);
        }
    }
    
    /**
     * Checks if a player has an Extendable Ear in their inventory.
     */
    private static boolean hasExtendableEar(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ExtendableEarItem) {
                return true;
            }
        }
        return false;
    }
}


