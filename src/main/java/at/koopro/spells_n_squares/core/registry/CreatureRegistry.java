package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.creatures.CreatureType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for all magical creatures in the mod.
 */
public final class CreatureRegistry {
    private static final Map<Identifier, CreatureType> CREATURES = new HashMap<>();
    
    private CreatureRegistry() {
    }
    
    /**
     * Registers a creature type with the registry.
     * @param id The unique identifier for the creature
     * @param creatureType The creature type to register
     */
    public static void register(Identifier id, CreatureType creatureType) {
        if (CREATURES.containsKey(id)) {
            throw new IllegalArgumentException("Creature already registered: " + id);
        }
        CREATURES.put(id, creatureType);
    }
    
    /**
     * Registers a creature type with the registry using the mod's namespace.
     * @param path The path for the creature (will be namespaced with mod ID)
     * @param creatureType The creature type to register
     */
    public static void register(String path, CreatureType creatureType) {
        register(ModIdentifierHelper.modId(path), creatureType);
    }
    
    /**
     * Gets a creature type by its ID.
     * @param id The creature ID
     * @return The creature type, or null if not found
     */
    public static CreatureType get(Identifier id) {
        return CREATURES.get(id);
    }
    
    /**
     * Gets all registered creature IDs.
     * @return A set of all creature IDs
     */
    public static Set<Identifier> getAllIds() {
        return CREATURES.keySet();
    }
    
    /**
     * Gets all registered creatures.
     * @return A map of all creatures
     */
    public static Map<Identifier, CreatureType> getAll() {
        return new HashMap<>(CREATURES);
    }
    
    /**
     * Checks if a creature is registered.
     * @param id The creature ID
     * @return True if registered, false otherwise
     */
    public static boolean isRegistered(Identifier id) {
        return CREATURES.containsKey(id);
    }
}















