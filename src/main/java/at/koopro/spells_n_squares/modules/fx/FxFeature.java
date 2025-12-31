package at.koopro.spells_n_squares.modules.fx;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.fx.FxRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the FX (visual effects) system.
 */
public class FxFeature implements IFeature {
    public String getFeatureId() {
        return "fx";
    }
    
    @Override
    public String getFeatureName() {
        return "FX System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        FxRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // FX system initialization
    }
}


