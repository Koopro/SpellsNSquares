package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Set;

/**
 * Interface for spell registration and retrieval.
 * Defines the API contract for spell registry functionality.
 */
public interface ISpellRegistry {
    /**
     * Registers a spell in the registry.
     * @param spell The spell to register
     */
    void register(Spell spell);
    
    /**
     * Gets a spell by its ID.
     * @param id The spell ID
     * @return The spell, or null if not found
     */
    Spell get(Identifier id);
    
    /**
     * Gets a spell by its ID string (namespace:path format).
     * @param idString The spell ID as a string
     * @return The spell, or null if not found
     */
    Spell get(String idString);
    
    /**
     * Gets all registered spell IDs.
     * @return Set of all spell IDs
     */
    Set<Identifier> getAllIds();
    
    /**
     * Gets all registered spells.
     * @return Collection of all spells
     */
    Map<Identifier, Spell> getAll();
    
    /**
     * Checks if a spell is registered.
     * @param id The spell ID
     * @return true if registered
     */
    boolean isRegistered(Identifier id);
    
    /**
     * Creates an Identifier for a spell ID within this mod's namespace.
     * @param path The path part of the ID
     * @return The Identifier
     */
    Identifier spellId(String path);
}





























