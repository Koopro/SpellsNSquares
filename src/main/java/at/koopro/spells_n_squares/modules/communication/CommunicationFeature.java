package at.koopro.spells_n_squares.modules.communication;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the communication system.
 */
public class CommunicationFeature implements IFeature {
    public String getFeatureId() {
        return "communication";
    }
    
    @Override
    public String getFeatureName() {
        return "Communication System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        CommunicationRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Communication system initialization
    }
}


