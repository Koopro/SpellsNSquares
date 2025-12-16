package at.koopro.spells_n_squares.features.communication;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles delivery of Patronus messages.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class PatronusMessageHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        if (player.level() instanceof ServerLevel serverLevel) {
            PatronusMessagingSystem.deliverMessages(serverLevel, player);
        }
    }
}

