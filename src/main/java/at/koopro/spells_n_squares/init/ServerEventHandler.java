package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerRegistry;
import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
import at.koopro.spells_n_squares.features.communication.OwlEntity;
import at.koopro.spells_n_squares.features.convenience.WaypointCommands;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.wand.WandAttunementHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

/**
 * Handles server-side events.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ServerEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(CommunicationRegistry.OWL.get(), OwlEntity.createAttributes().build());
    }
    
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting - Spells_n_Squares mod ready");
        // Initialize pocket dimension system
        at.koopro.spells_n_squares.features.storage.PocketDimensionManager.initialize(event.getServer());
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
        // Set up default spell assignments for new players (using demo spells)
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_TOP, 
            SpellRegistry.spellId("heal"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_BOTTOM, 
            SpellRegistry.spellId("teleport"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_LEFT, 
            SpellRegistry.spellId("fireball"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_RIGHT, 
            SpellRegistry.spellId("periculum"));
        
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
        // SpellHandler removed - cleanup handled by PlayerDataManagerRegistry
        // Clear attunement progress
        WandAttunementHandler.clearPlayerData(event.getEntity());
    }
    
    /**
     * Ticks spell cooldowns for all players every server tick.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // Only tick on server side
        if (!event.getEntity().level().isClientSide()) {
            SpellManager.tickCooldowns(event.getEntity());
            SpellManager.tickHoldSpells(event.getEntity().level());
        }
    }
}

