package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Handles Muffliato storage updates on level ticks.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class MuffliatoTickHandler {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                MuffliatoStorageManager.tick(serverLevel);
            }
        }, "ticking Muffliato storage");
    }
}

