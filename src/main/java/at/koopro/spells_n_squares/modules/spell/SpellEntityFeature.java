package at.koopro.spells_n_squares.modules.spell;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.spell.SpellEntityRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for spell entities.
 */
public class SpellEntityFeature implements IFeature {
    public String getFeatureId() {
        return "spellentity";
    }
    
    @Override
    public String getFeatureName() {
        return "Spell Entity System";
    }
    
    public Set<String> getDependencies() {
        return Set.of("spell"); // Spell entities depend on spells
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        SpellEntityRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Spell entity system initialization
    }
}

