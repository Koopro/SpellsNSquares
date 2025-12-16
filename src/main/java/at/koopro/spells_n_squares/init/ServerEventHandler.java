package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerRegistry;
import at.koopro.spells_n_squares.features.convenience.WaypointCommands;
import at.koopro.spells_n_squares.features.spell.DebugCommands;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.SpellRegistry;
import at.koopro.spells_n_squares.features.wand.WandAttunementHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

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
        WaypointCommands.register(event.getDispatcher());
    }
    
    /**
     * Handles player login: sets up default spells and syncs all player data to the client.
     * Uses the PlayerDataManagerRegistry for centralized synchronization.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Set up default spell assignments for new players
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_TOP, 
            SpellRegistry.spellId("heal"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_BOTTOM, 
            SpellRegistry.spellId("protego"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_LEFT, 
            SpellRegistry.spellId("fireball"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_RIGHT, 
            SpellRegistry.spellId("lightning"));
        
        // Sync all player data to the client
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(event.getEntity());
        if (serverPlayer != null) {
            PlayerDataManagerRegistry.syncAllToClient(serverPlayer);
        }
    }
    
    /**
     * Cleans up all player data when they disconnect.
     * Uses the PlayerDataManagerRegistry for centralized cleanup.
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerDataManagerRegistry.clearAllPlayerData(event.getEntity());
        // Also clear tick counter for SpellHandler
        at.koopro.spells_n_squares.features.spell.SpellHandler.clearPlayerTickCounter(event.getEntity());
        // Clear attunement progress
        WandAttunementHandler.clearPlayerData(event.getEntity());
    }
}

