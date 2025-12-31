package at.koopro.spells_n_squares.features.transportation.system;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Manages the Floo Network of connected fireplaces.
 */
public final class FlooNetworkManager {
    private FlooNetworkManager() {
    }
    
    // Map of fireplace positions to their network connections
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<FlooLocation, Set<FlooLocation>> network = new HashMap<>();
    
    /**
     * Represents a fireplace location in the network.
     */
    public record FlooLocation(ResourceKey<Level> dimension, BlockPos pos, String name) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FlooLocation that = (FlooLocation) o;
            return Objects.equals(dimension, that.dimension) && Objects.equals(pos, that.pos);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(dimension, pos);
        }
    }
    
    /**
     * Registers a fireplace in the network.
     */
    public static void registerFireplace(ResourceKey<Level> dimension, BlockPos pos, String name) {
        FlooLocation location = new FlooLocation(dimension, pos, name);
        network.putIfAbsent(location, new HashSet<>());
    }
    
    /**
     * Connects two fireplaces in the network.
     */
    public static void connectFireplaces(FlooLocation loc1, FlooLocation loc2) {
        network.computeIfAbsent(loc1, k -> new HashSet<>()).add(loc2);
        network.computeIfAbsent(loc2, k -> new HashSet<>()).add(loc1);
    }
    
    /**
     * Gets all connected fireplaces from a location.
     */
    public static Set<FlooLocation> getConnectedFireplaces(FlooLocation location) {
        return network.getOrDefault(location, Collections.emptySet());
    }
    
    /**
     * Gets all fireplaces in the network.
     */
    public static Set<FlooLocation> getAllFireplaces() {
        return network.keySet();
    }
    
    /**
     * Finds a fireplace by name.
     */
    public static Optional<FlooLocation> findFireplaceByName(String name) {
        return network.keySet().stream()
            .filter(loc -> loc.name().equalsIgnoreCase(name))
            .findFirst();
    }
    
    /**
     * Removes a fireplace from the network.
     */
    public static void removeFireplace(FlooLocation location) {
        // Remove from all connections
        for (Set<FlooLocation> connections : network.values()) {
            connections.remove(location);
        }
        network.remove(location);
    }
}

