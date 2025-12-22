package at.koopro.spells_n_squares.core.util;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.resources.Identifier;

/**
 * Utility class for creating identifiers within the mod's namespace.
 * Centralizes identifier creation to reduce boilerplate and make namespace changes easier.
 */
public final class ModIdentifierHelper {
    private ModIdentifierHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates an identifier within the mod's namespace.
     * @param path The path part of the identifier
     * @return The Identifier with the mod's namespace
     */
    public static Identifier modId(String path) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, path);
    }
}












