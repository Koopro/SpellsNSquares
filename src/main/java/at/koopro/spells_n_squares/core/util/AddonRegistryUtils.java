package at.koopro.spells_n_squares.core.util;

import net.minecraft.resources.Identifier;

/**
 * Utility helpers for addon registries.
 * Centralizes identifier creation and namespace validation for addon-provided content.
 */
public final class AddonRegistryUtils {
    private AddonRegistryUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates an {@link Identifier} within the given addon's namespace.
     *
     * @param addonId The addon ID / namespace
     * @param path    The path part of the identifier
     * @return The namespaced identifier
     */
    public static Identifier addonId(String addonId, String path) {
        return Identifier.fromNamespaceAndPath(addonId, path);
    }

    /**
     * Validates that the given identifier uses the expected addon namespace.
     *
     * @param addonId   The expected addon ID / namespace
     * @param id        The identifier to validate
     * @param entryType A short description of the entry type for error messages (e.g. "spell", "item")
     * @throws IllegalArgumentException if the identifier uses a different namespace
     */
    public static void validateNamespace(String addonId, Identifier id, String entryType) {
        if (!id.getNamespace().equals(addonId)) {
            throw new IllegalArgumentException(
                "Addon '" + addonId + "' tried to register " + entryType +
                " with ID namespace '" + id.getNamespace() + "' (expected '" + addonId + "')"
            );
        }
    }
}










