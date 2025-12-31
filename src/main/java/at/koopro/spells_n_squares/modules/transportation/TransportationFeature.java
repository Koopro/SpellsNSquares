package at.koopro.spells_n_squares.modules.transportation;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the transportation system.
 */
public class TransportationFeature implements IFeature {
    public String getFeatureId() {
        return "transportation";
    }
    
    @Override
    public String getFeatureName() {
        return "Transportation System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        TransportationRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Transportation system initialization
    }
}


