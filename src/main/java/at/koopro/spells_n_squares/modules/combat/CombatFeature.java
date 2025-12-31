package at.koopro.spells_n_squares.modules.combat;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.combat.CombatRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the combat system.
 */
public class CombatFeature implements IFeature {
    public String getFeatureId() {
        return "combat";
    }
    
    @Override
    public String getFeatureName() {
        return "Combat System";
    }
    
    public Set<String> getDependencies() {
        return Set.of("spell"); // Combat depends on spells
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        CombatRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Combat system initialization
    }
}


