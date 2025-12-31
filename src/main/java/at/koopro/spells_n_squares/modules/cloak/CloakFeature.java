package at.koopro.spells_n_squares.modules.cloak;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.cloak.CloakRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the cloak system.
 */
public class CloakFeature implements IFeature {
    public String getFeatureId() {
        return "cloak";
    }
    
    @Override
    public String getFeatureName() {
        return "Cloak System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        CloakRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Cloak system initialization
    }
}

