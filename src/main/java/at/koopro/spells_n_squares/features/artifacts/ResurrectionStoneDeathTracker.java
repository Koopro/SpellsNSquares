package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.*;

/**
 * Tracks deaths for Resurrection Stone functionality.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ResurrectionStoneDeathTracker {
    
    // Track recent deaths (entity UUID -> death info)
    private static final Map<UUID, DeathRecord> recentDeaths = new HashMap<>();
    private static final long DEATH_RETENTION_TICKS = 12000; // 10 minutes
    
    public record DeathRecord(
        UUID entityId,
        String entityName,
        double x, double y, double z,
        long deathTick
    ) {}
    
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
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
        }
    }
    
    /**
     * Gets recent deaths within range of a position.
     */
    public static List<DeathRecord> getRecentDeaths(ServerLevel level, double x, double y, double z, double range) {
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
    }
    
    /**
     * Removes a death record (e.g., after summoning a shade).
     */
    public static void removeDeathRecord(UUID entityId) {
        recentDeaths.remove(entityId);
    }
}







