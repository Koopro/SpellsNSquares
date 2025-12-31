package at.koopro.spells_n_squares.modules.navigation;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.navigation.NavigationRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the navigation system.
 */
public class NavigationFeature implements IFeature {
    public String getFeatureId() {
        return "navigation";
    }
    
    @Override
    public String getFeatureName() {
        return "Navigation System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        NavigationRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Navigation system initialization
    }
}


