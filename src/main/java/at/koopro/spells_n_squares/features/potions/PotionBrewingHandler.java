package at.koopro.spells_n_squares.features.potions;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Handles potion brewing system updates.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class PotionBrewingHandler {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            PotionBrewingManager.tickBrewingSessions(serverLevel);
        }
    }
}
