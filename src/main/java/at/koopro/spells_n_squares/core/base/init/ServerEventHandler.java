package at.koopro.spells_n_squares.core.base.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerRegistry;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.wand.system.WandAttunementHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

/**
 * Handles server-side events.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ServerEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        SafeEventHandler.execute(() -> {
            DevLogger.logMethodEntry(ServerEventHandler.class, "onServerStarting");
            LOGGER.info("Server starting - Spells_n_Squares mod ready");
            // Initialize pocket dimension system
            at.koopro.spells_n_squares.features.storage.PocketDimensionManager.initialize(event.getServer());
            DevLogger.logStateChange(ServerEventHandler.class, "onServerStarting", "Server initialized");
            DevLogger.logMethodExit(ServerEventHandler.class, "onServerStarting");
        }, "server starting");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SafeEventHandler.execute(() -> {
            DevLogger.logMethodEntry(ServerEventHandler.class, "onRegisterCommands");
            // Register unified debug commands (replaces DebugCommands and UtilityDebugCommands)
            at.koopro.spells_n_squares.core.commands.UnifiedDebugCommands.register(event.getDispatcher());
            DevLogger.logStateChange(ServerEventHandler.class, "onRegisterCommands", "Commands registered");
            DevLogger.logMethodExit(ServerEventHandler.class, "onRegisterCommands");
        }, "registering commands");
    }
    
    /**
     * Handles player login: loads saved spell slots or sets up default spells for new players.
     * Uses the PlayerDataManagerRegistry for centralized synchronization.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        SafeEventHandler.execute(() -> {
            DevLogger.logMethodEntry(ServerEventHandler.class, "onPlayerLoggedIn", 
                "player=" + (player != null ? player.getName().getString() : "null"));
            
            // Load saved spell slots if they exist
            if (at.koopro.spells_n_squares.features.spell.manager.SpellSlotData.hasSavedSlots(player)) {
                at.koopro.spells_n_squares.features.spell.manager.SpellSlotData.SpellSlotComponent savedData = 
                    at.koopro.spells_n_squares.features.spell.manager.SpellSlotData.getSpellSlotData(player);
                net.minecraft.resources.Identifier[] slots = savedData.toArray();
                
                // Restore spell slots to SpellManager
                for (int i = 0; i < slots.length; i++) {
                    if (slots[i] != null) {
                        // Only restore if spell is still registered
                        if (SpellRegistry.isRegistered(slots[i])) {
                            SpellManager.setSpellInSlot(player, i, slots[i]);
                        }
                    }
                }
            } else {
                // Set up default spell assignments for new players (using demo spells)
                SpellManager.setSpellInSlot(player, SpellManager.SLOT_TOP, 
                    SpellRegistry.spellId("heal"));
                SpellManager.setSpellInSlot(player, SpellManager.SLOT_BOTTOM, 
                    SpellRegistry.spellId("teleport"));
                SpellManager.setSpellInSlot(player, SpellManager.SLOT_LEFT, 
                    SpellRegistry.spellId("fireball"));
                SpellManager.setSpellInSlot(player, SpellManager.SLOT_RIGHT, 
                    SpellRegistry.spellId("periculum"));
            }
            
            // Sync all player data to the client
            ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.player.PlayerValidationUtils.asServerPlayer(player);
            if (serverPlayer != null) {
                DevLogger.logStateChange(ServerEventHandler.class, "onPlayerLoggedIn", 
                    "Syncing player data to client");
                PlayerDataManagerRegistry.syncAllToClient(serverPlayer);
                
                // Apply race-based size scaling on login (if identity is set)
                at.koopro.spells_n_squares.core.data.PlayerIdentityHelper.applyRaceScaling(serverPlayer);
                
                // Sync player model data on login
                at.koopro.spells_n_squares.core.util.player.PlayerModelUtils.syncModelDataToClient(serverPlayer);
                // Immortality is now handled via MobEffect, which syncs automatically
            }
            DevLogger.logMethodExit(ServerEventHandler.class, "onPlayerLoggedIn");
        }, "player logged in", player);
    }
    
    /**
     * Cleans up all player data when they disconnect.
     * Uses the PlayerDataManagerRegistry for centralized cleanup.
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        SafeEventHandler.execute(() -> {
            DevLogger.logMethodEntry(ServerEventHandler.class, "onPlayerLoggedOut", 
                "player=" + (player != null ? player.getName().getString() : "null"));
            PlayerDataManagerRegistry.clearAllPlayerData(player);
            // SpellHandler removed - cleanup handled by PlayerDataManagerRegistry
            // Clear attunement progress
            WandAttunementHandler.clearPlayerData(player);
            DevLogger.logStateChange(ServerEventHandler.class, "onPlayerLoggedOut", 
                "Player data cleared");
            DevLogger.logMethodExit(ServerEventHandler.class, "onPlayerLoggedOut");
        }, "player logged out", player);
    }
    
    /**
     * Ticks spell cooldowns for all players every server tick.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        SafeEventHandler.execute(() -> {
            DevLogger.logMethodEntry(ServerEventHandler.class, "onPlayerTick", 
                "player=" + (player != null ? player.getName().getString() : "null"));
            // Only tick on server side
            if (!player.level().isClientSide()) {
                SpellManager.tickCooldowns(player);
                SpellManager.tickHoldSpells(player.level());
            }
            DevLogger.logMethodExit(ServerEventHandler.class, "onPlayerTick");
        }, "ticking player", player);
    }
    
    /**
     * Handles level tick events for spell clash detection.
     */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                // Check for projectile collisions
                at.koopro.spells_n_squares.features.spell.clash.SpellClashDetector.checkProjectileCollisions(serverLevel);
                
                // Check for duel clashes
                at.koopro.spells_n_squares.features.spell.clash.SpellClashDetector.checkDuelClashes(serverLevel);
                
                // Update ongoing clash effects
                at.koopro.spells_n_squares.features.spell.clash.SpellClashDetector.updateClashEffects(serverLevel);
                
                // Clean up expired casts
                at.koopro.spells_n_squares.features.spell.clash.SpellClashDetector.cleanupExpiredCasts(serverLevel);
            }
        }, "level tick");
    }
}

