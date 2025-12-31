package at.koopro.spells_n_squares.modules.artifacts;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.artifacts.ArtifactsRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the artifacts system.
 */
public class ArtifactsFeature implements IFeature {
    public String getFeatureId() {
        return "artifacts";
    }
    
    @Override
    public String getFeatureName() {
        return "Artifacts System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        ArtifactsRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Artifacts system initialization
    }
}


