package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for all spells in the mod.
 * Use this to register and retrieve spells.
 */
public class SpellRegistry {
    private static final Map<Identifier, Spell> SPELLS = new HashMap<>();
    
    /**
     * Registers a spell in the registry.
     * @param spell The spell to register
     */
    public static void register(Spell spell) {
        Identifier id = spell.getId();
        if (SPELLS.containsKey(id)) {
            throw new IllegalArgumentException("Spell with id " + id + " is already registered!");
        }
        SPELLS.put(id, spell);
    }
    
    /**
     * Gets a spell by its ID.
     * @param id The spell ID
     * @return The spell, or null if not found
     */
    public static Spell get(Identifier id) {
        return SPELLS.get(id);
    }
    
    /**
     * Gets a spell by its ID string (namespace:path format).
     * @param idString The spell ID as a string
     * @return The spell, or null if not found
     */
    public static Spell get(String idString) {
        return get(Identifier.parse(idString));
    }
    
    /**
     * Gets all registered spell IDs.
     * @return Set of all spell IDs
     */
    public static Set<Identifier> getAllIds() {
        return SPELLS.keySet();
    }
    
    /**
     * Gets all registered spells.
     * @return Collection of all spells
     */
    public static Map<Identifier, Spell> getAll() {
        return new HashMap<>(SPELLS);
    }
    
    /**
     * Checks if a spell is registered.
     * @param id The spell ID
     * @return true if registered
     */
    public static boolean isRegistered(Identifier id) {
        return SPELLS.containsKey(id);
    }
    
    /**
     * Creates an Identifier for a spell ID within this mod's namespace.
     * @param path The path part of the ID
     * @return The Identifier
     */
    public static Identifier spellId(String path) {
        return ModIdentifierHelper.modId(path);
    }
}
