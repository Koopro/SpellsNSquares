package at.koopro.spells_n_squares.features.convenience;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles automatic item repair for players.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class AutoRepairHandler {
    
    private static final int REPAIR_INTERVAL = 200; // 10 seconds
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        
        // Only repair every REPAIR_INTERVAL ticks
        if (player.tickCount % REPAIR_INTERVAL != 0) {
            return;
        }
        
        // Check and repair items in inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            
            if (stack.isEmpty() || !stack.isDamaged()) {
                continue;
            }
            
            // Repair a small amount
            int damage = stack.getDamageValue();
            if (damage > 0) {
                stack.setDamageValue(Math.max(0, damage - 1));
            }
        }
    }
}

