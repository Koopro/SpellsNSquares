package at.koopro.spells_n_squares.core.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Utility class for generating translation keys.
 * Centralizes translation key generation patterns for consistency.
 */
public final class TranslationUtils {
    private TranslationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Generates a translation key for a spell name.
     * Pattern: "spell.{namespace}.{path}.name"
     * @param spellId The spell identifier
     * @return The translation key string
     */
    public static String spellTranslationKey(Identifier spellId) {
        return "spell." + spellId.getNamespace() + "." + spellId.getPath() + ".name";
    }
    
    /**
     * Generates a translatable Component for a spell name.
     * @param spellId The spell identifier
     * @return The translatable Component
     */
    public static Component spellTranslatableName(Identifier spellId) {
        return Component.translatable(spellTranslationKey(spellId));
    }
}

















