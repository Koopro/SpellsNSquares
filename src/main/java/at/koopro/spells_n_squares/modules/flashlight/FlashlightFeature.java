package at.koopro.spells_n_squares.modules.flashlight;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the flashlight system.
 */
public class FlashlightFeature implements IFeature {
    public String getFeatureId() {
        return "flashlight";
    }
    
    @Override
    public String getFeatureName() {
        return "Flashlight System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        FlashlightRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Flashlight system initialization
    }
}


