package at.koopro.spells_n_squares.features.building.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.building.system.WardSystem;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Handles ward system updates.
 * Optimized: Only updates every 10 ticks (0.5 seconds) to reduce overhead.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class WardHandler {
    
    private static final int UPDATE_INTERVAL = 10; // Update every 0.5 seconds
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Only update periodically
            if (event.getLevel().getGameTime() % UPDATE_INTERVAL != 0) {
                return;
            }
            
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                WardSystem.updateWards(serverLevel);
            }
        }, "updating wards");
    }
}

