package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.features.artifacts.TimeTurnerItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles snapshot storage for Time-Turner items.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class TimeTurnerSnapshotHandler {
    
    // Store snapshot every 20 ticks (1 second)
    private static final int SNAPSHOT_INTERVAL = 20;
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        
        // Only store snapshot every SNAPSHOT_INTERVAL ticks
        if (player.tickCount % SNAPSHOT_INTERVAL != 0) {
            return;
        }
        
        // Check if player has Time-Turner in inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof TimeTurnerItem) {
                int currentTick = (int) player.level().getGameTime();
                TimeTurnerItem.storeSnapshot(stack, player, currentTick);
                break; // Only one Time-Turner per player
            }
        }
    }
}

