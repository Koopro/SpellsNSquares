package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Handles performance optimizations for FX system (particle pooling, batching).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class FXPerformanceHandler {
    
    /**
     * Flushes particle pool at the end of each level tick.
     */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // Flush particle pools for this level
            ParticlePool.flush(serverLevel);
        }
    }
}
