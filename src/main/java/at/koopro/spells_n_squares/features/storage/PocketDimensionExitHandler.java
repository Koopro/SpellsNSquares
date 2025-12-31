package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles exit platform detection for pocket dimensions.
 * Checks if players have reached the exit platform and teleports them back.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class PocketDimensionExitHandler {
    
    private static final int CHECK_INTERVAL = 5; // Check every 5 ticks
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                // Only check periodically to reduce overhead
                if (serverPlayer.tickCount % CHECK_INTERVAL == 0) {
                    PocketDimensionManager.checkExitPlatform(serverPlayer, serverLevel);
                }
            }
        }
    }
}












