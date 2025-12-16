package at.koopro.spells_n_squares.features.building;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Handles ward system updates.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class WardHandler {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            WardSystem.updateWards(serverLevel);
        }
    }
}

