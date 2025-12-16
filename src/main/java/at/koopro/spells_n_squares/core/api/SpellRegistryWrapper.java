package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Set;

/**
 * Wrapper class that implements ISpellRegistry by delegating to SpellRegistry static methods.
 * This provides the interface abstraction while maintaining backward compatibility with existing static methods.
 */
public final class SpellRegistryWrapper implements ISpellRegistry {
    public static final SpellRegistryWrapper INSTANCE = new SpellRegistryWrapper();
    
    private SpellRegistryWrapper() {
        // Singleton instance
    }
    
    @Override
    public void register(Spell spell) {
        SpellRegistry.register(spell);
    }
    
    @Override
    public Spell get(Identifier id) {
        return SpellRegistry.get(id);
    }
    
    @Override
    public Spell get(String idString) {
        return SpellRegistry.get(idString);
    }
    
    @Override
    public Set<Identifier> getAllIds() {
        return SpellRegistry.getAllIds();
    }
    
    @Override
    public Map<Identifier, Spell> getAll() {
        return SpellRegistry.getAll();
    }
    
    @Override
    public boolean isRegistered(Identifier id) {
        return SpellRegistry.isRegistered(id);
    }
    
    @Override
    public Identifier spellId(String path) {
        return SpellRegistry.spellId(path);
    }
}









