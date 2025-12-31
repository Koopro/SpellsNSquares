package at.koopro.spells_n_squares.modules.economy;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the economy system.
 */
public class EconomyFeature implements IFeature {
    public String getFeatureId() {
        return "economy";
    }
    
    @Override
    public String getFeatureName() {
        return "Economy System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        EconomyRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Economy system initialization
    }
}


