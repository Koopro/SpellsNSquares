package at.koopro.spells_n_squares.core.api.addon.registration;

import at.koopro.spells_n_squares.core.api.ISpellRegistry;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.spell.Spell;
import net.minecraft.resources.Identifier;

/**
 * Helper class for addons to register spells.
 * Provides convenience methods and namespace validation.
 */
public final class AddonSpellRegistry {
    private final String addonId;
    private final ISpellRegistry spellRegistry;
    
    public AddonSpellRegistry(String addonId, ISpellRegistry spellRegistry) {
        this.addonId = addonId;
        this.spellRegistry = spellRegistry;
    }
    
    /**
     * Registers a spell with namespace validation.
     * Ensures the spell ID uses the addon's namespace.
     * @param spell The spell to register
     * @throws IllegalArgumentException if the spell ID namespace doesn't match the addon ID
     */
    public void registerSpell(Spell spell) {
        if (spell == null) {
            throw new IllegalArgumentException("Spell cannot be null");
        }
        
        Identifier spellId = spell.getId();
        
        // Validate namespace matches addon ID
        if (!spellId.getNamespace().equals(addonId)) {
            throw new IllegalArgumentException(
                "Spell ID namespace '" + spellId.getNamespace() + "' does not match addon ID '" + addonId + "'"
            );
        }
        
        spellRegistry.register(spell);
    }
    
    /**
     * Creates a spell ID within the addon's namespace.
     * @param path The path part of the spell ID
     * @return The Identifier
     */
    public Identifier spellId(String path) {
        return Identifier.fromNamespaceAndPath(addonId, path);
    }
    
    /**
     * Gets the spell registry API.
     * @return The spell registry
     */
    public ISpellRegistry getSpellRegistry() {
        return spellRegistry;
    }
    
    /**
     * Gets the addon ID.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
}

