package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for all spells in the mod.
 * Provides static methods for spell registration and retrieval.
 */
public class SpellRegistry {
    private static final Map<Identifier, Spell> spells = new ConcurrentHashMap<>();
    
    /**
     * Registers a spell in the registry.
     * @param spell The spell to register
     * @throws IllegalArgumentException if the spell is null or already registered
     */
    public static void register(Spell spell) {
        if (spell == null) {
            throw new IllegalArgumentException("Spell cannot be null");
        }
        
        Identifier id = spell.getId();
        if (spells.containsKey(id)) {
            throw new IllegalArgumentException("Spell with ID " + id + " is already registered");
        }
        
        spells.put(id, spell);
    }
    
    /**
     * Gets a spell by its ID.
     * @param id The spell ID
     * @return The spell, or null if not found
     */
    public static Spell get(Identifier id) {
        return spells.get(id);
    }
    
    /**
     * Gets a spell by its ID string (namespace:path format).
     * Uses cached identifier parsing for better performance.
     * @param idString The spell ID as a string
     * @return The spell, or null if not found
     */
    public static Spell get(String idString) {
        Identifier id = at.koopro.spells_n_squares.core.util.registry.RegistryHelper.parseIdentifierCached(idString);
        if (id == null) {
            return null;
        }
        return get(id);
    }
    
    /**
     * Gets all registered spell IDs.
     * @return Set of all spell IDs
     */
    public static Set<Identifier> getAllIds() {
        return Collections.unmodifiableSet(spells.keySet());
    }
    
    /**
     * Gets all registered spells.
     * @return Unmodifiable map of all spells
     */
    public static Map<Identifier, Spell> getAll() {
        return Collections.unmodifiableMap(spells);
    }
    
    /**
     * Checks if a spell is registered.
     * @param id The spell ID
     * @return true if registered
     */
    public static boolean isRegistered(Identifier id) {
        return spells.containsKey(id);
    }
    
    /**
     * Creates an Identifier for a spell ID within this mod's namespace.
     * @param path The path part of the ID
     * @return The Identifier
     */
    public static Identifier spellId(String path) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, path);
    }
}













