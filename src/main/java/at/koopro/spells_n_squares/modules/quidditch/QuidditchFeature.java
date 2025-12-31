package at.koopro.spells_n_squares.modules.quidditch;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.quidditch.QuidditchRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the quidditch system.
 */
public class QuidditchFeature implements IFeature {
    public String getFeatureId() {
        return "quidditch";
    }
    
    @Override
    public String getFeatureName() {
        return "Quidditch System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        QuidditchRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Quidditch system initialization
    }
}


