package at.koopro.spells_n_squares.modules.robes;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the robes system.
 */
public class RobesFeature implements IFeature {
    public String getFeatureId() {
        return "robes";
    }
    
    @Override
    public String getFeatureName() {
        return "Robes System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        RobesRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Robes system initialization
    }
}

