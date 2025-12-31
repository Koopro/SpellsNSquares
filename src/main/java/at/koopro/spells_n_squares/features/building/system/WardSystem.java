package at.koopro.spells_n_squares.features.building.system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * System for managing protective wards around player bases.
 */
public final class WardSystem {
    private WardSystem() {
    }
    
    // Map of ward locations to their properties
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<WardLocation, WardProperties> activeWards = new HashMap<>();
    
    /**
     * Represents a ward location.
     */
    public record WardLocation(Level level, BlockPos center, UUID ownerId) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WardLocation that = (WardLocation) o;
            return Objects.equals(level, that.level) && Objects.equals(center, that.center);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(level, center);
        }
    }
    
    /**
     * Properties of a ward.
     */
    public record WardProperties(
        int radius,
        boolean preventEntry,
        boolean preventDamage,
        long expirationTick
    ) {
    }
    
    /**
     * Creates a ward at the given location.
     */
    public static void createWard(Level level, BlockPos center, Player owner, int radius, int durationTicks) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        WardLocation location = new WardLocation(level, center, owner.getUUID());
        long expirationTick = serverLevel.getGameTime() + durationTicks;
        
        WardProperties properties = new WardProperties(radius, true, true, expirationTick);
        activeWards.put(location, properties);
        
        // Visual effect
        Vec3 centerVec = Vec3.atCenterOf(center);
        serverLevel.sendParticles(ParticleTypes.ENCHANT, centerVec.x, centerVec.y + radius, centerVec.z,
            50, radius * 0.5, radius * 0.5, radius * 0.5, 0.1);
    }
    
    /**
     * Checks if an entity can enter a warded area.
     */
    public static boolean canEnter(Level level, BlockPos pos, LivingEntity entity) {
        for (Map.Entry<WardLocation, WardProperties> entry : activeWards.entrySet()) {
            WardLocation ward = entry.getKey();
            if (ward.level() != level) {
                continue;
            }
            
            WardProperties props = entry.getValue();
            double distance = pos.distSqr(ward.center());
            
            if (distance <= props.radius() * props.radius()) {
                // Check if entity is the owner
                if (entity instanceof Player player && player.getUUID().equals(ward.ownerId())) {
                    continue; // Owner can always enter
                }
                
                if (props.preventEntry()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Updates wards and removes expired ones.
     */
    public static void updateWards(ServerLevel level) {
        long currentTick = level.getGameTime();
        
        activeWards.entrySet().removeIf(entry -> {
            WardLocation ward = entry.getKey();
            if (ward.level() != level) {
                return false; // Don't remove wards from other levels
            }
            
            WardProperties props = entry.getValue();
            if (currentTick >= props.expirationTick()) {
                // Ward expired
                return true;
            }
            
            return false;
        });
    }
    
    /**
     * Gets all active wards in a level.
     */
    public static Set<WardLocation> getWardsInLevel(Level level) {
        Set<WardLocation> result = new HashSet<>();
        for (WardLocation ward : activeWards.keySet()) {
            if (ward.level() == level) {
                result.add(ward);
            }
        }
        return result;
    }
}

