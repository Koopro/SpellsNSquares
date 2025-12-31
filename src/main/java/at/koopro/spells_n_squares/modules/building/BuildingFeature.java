package at.koopro.spells_n_squares.modules.building;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.building.BuildingRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the building system.
 */
public class BuildingFeature implements IFeature {
    public String getFeatureId() {
        return "building";
    }
    
    @Override
    public String getFeatureName() {
        return "Building System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        BuildingRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Building system initialization
    }
}


