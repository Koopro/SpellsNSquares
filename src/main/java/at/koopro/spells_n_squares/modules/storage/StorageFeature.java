package at.koopro.spells_n_squares.modules.storage;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the storage system.
 */
public class StorageFeature implements IFeature {
    public String getFeatureId() {
        return "storage";
    }
    
    @Override
    public String getFeatureName() {
        return "Storage System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        StorageRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Storage system initialization
    }
}


