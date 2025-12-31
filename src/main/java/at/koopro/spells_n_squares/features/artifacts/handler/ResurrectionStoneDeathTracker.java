package at.koopro.spells_n_squares.features.artifacts.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.*;

/**
 * Tracks deaths for Resurrection Stone functionality.
 * 
 * <p><b>Thread Safety:</b> The static collection {@code recentDeaths} is accessed only from the main server thread 
 * during game ticks. This collection is not thread-safe, but thread safety is not required as Minecraft's game logic 
 * runs on a single thread. If this collection is ever accessed from multiple threads in the future, 
 * it should be converted to a thread-safe collection (e.g., {@code ConcurrentHashMap}).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ResurrectionStoneDeathTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Track recent deaths (entity UUID -> death info)
    // Thread safety: Accessed only from main server thread
    private static final Map<UUID, DeathRecord> recentDeaths = new HashMap<>();
    
    /** How long to retain death records before cleanup (in ticks). 12000 ticks = 10 minutes at 20 TPS. */
    private static final long DEATH_RETENTION_TICKS = 12000;
    
    /** Interval between periodic cleanup runs (in ticks). 6000 ticks = 5 minutes at 20 TPS. */
    private static final long CLEANUP_INTERVAL_TICKS = 6000;
    
    /** Last tick when cleanup was performed. Used to throttle cleanup frequency. */
    private static long lastCleanupTick = 0;
    
    public record DeathRecord(
        UUID entityId,
        String entityName,
        double x, double y, double z,
        long deathTick
    ) {}
    
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level() == null) {
            return;
        }
        if (entity.level().isClientSide()) {
            return;
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
            try {
                UUID entityId = entity.getUUID();
                String entityName = entity instanceof Player player 
                    ? player.getName().getString() 
                    : entity.getDisplayName().getString();
                
                recentDeaths.put(entityId, new DeathRecord(
                    entityId,
                    entityName,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    serverLevel.getGameTime()
                ));
            } catch (Exception e) {
                LOGGER.error("Error tracking death for entity {}: {}", 
                    entity.getUUID(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Periodic cleanup of old death records.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer() == null || event.getServer().overworld() == null) {
            return;
        }
        
        long currentTick = event.getServer().overworld().getGameTime();
        if (currentTick - lastCleanupTick < CLEANUP_INTERVAL_TICKS) {
            return;
        }
        
        lastCleanupTick = currentTick;
        cleanupOldDeaths(currentTick);
    }
    
    /**
     * Cleans up old death records.
     */
    private static void cleanupOldDeaths(long currentTick) {
        try {
            int removed = 0;
            Iterator<Map.Entry<UUID, DeathRecord>> iterator = recentDeaths.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, DeathRecord> entry = iterator.next();
                if (currentTick - entry.getValue().deathTick() > DEATH_RETENTION_TICKS) {
                    iterator.remove();
                    removed++;
                }
            }
            if (removed > 0) {
                LOGGER.debug("Cleaned up {} old death records", removed);
            }
        } catch (Exception e) {
            LOGGER.error("Error cleaning up death records: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Cleans up death records for a specific player (called on disconnect).
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        try {
            UUID playerId = event.getEntity().getUUID();
            recentDeaths.remove(playerId);
        } catch (Exception e) {
            LOGGER.error("Error cleaning up death records for player {}: {}", 
                event.getEntity().getUUID(), e.getMessage(), e);
        }
    }
    
    /**
     * Gets recent deaths within range of a position.
     */
    public static List<DeathRecord> getRecentDeaths(ServerLevel level, double x, double y, double z, double range) {
        if (level == null) {
            return new ArrayList<>();
        }
        try {
            long currentTick = level.getGameTime();
            List<DeathRecord> nearbyDeaths = new ArrayList<>();
            
            // Clean up old deaths
            recentDeaths.entrySet().removeIf(entry -> 
                currentTick - entry.getValue().deathTick() > DEATH_RETENTION_TICKS
            );
            
            // Find nearby deaths
            for (DeathRecord death : recentDeaths.values()) {
                double dx = death.x() - x;
                double dy = death.y() - y;
                double dz = death.z() - z;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (distance <= range && (currentTick - death.deathTick()) <= DEATH_RETENTION_TICKS) {
                    nearbyDeaths.add(death);
                }
            }
            
            return nearbyDeaths;
        } catch (Exception e) {
            LOGGER.error("Error getting recent deaths: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Removes a death record (e.g., after summoning a shade).
     */
    public static void removeDeathRecord(UUID entityId) {
        if (entityId == null) {
            return;
        }
        try {
            recentDeaths.remove(entityId);
        } catch (Exception e) {
            LOGGER.error("Error removing death record for entity {}: {}", entityId, e.getMessage(), e);
        }
    }
}












