package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles periodic visual effects for wands (charged glow).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class WandGlowTickHandler {
    
    // Spawn glow particles every 20 ticks (1 second)
    private static final int GLOW_INTERVAL = 20;
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        
        SafeEventHandler.execute(() -> {
            // Only spawn glow every GLOW_INTERVAL ticks
            if (player.tickCount % GLOW_INTERVAL != 0) {
                return;
            }
            
            ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
            if (!wand.isEmpty() && WandDataHelper.isAttuned(wand)) {
                // Spawn charged glow particles
                WandVisualEffects.spawnChargedGlow(player.level(), player, wand);
            }
        }, "ticking wand glow", player);
    }
}

