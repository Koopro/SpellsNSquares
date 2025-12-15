package at.koopro.spells_n_squares.handlers;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import at.koopro.spells_n_squares.features.spell.DebugCommands;

/**
 * Handles server-side events.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ServerEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting - Spells_n_Squares mod ready");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DebugCommands.register(event.getDispatcher());
    }
}
