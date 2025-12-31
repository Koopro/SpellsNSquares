package at.koopro.spells_n_squares.modules.education;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.education.EducationRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the education system.
 */
public class EducationFeature implements IFeature {
    public String getFeatureId() {
        return "education";
    }
    
    @Override
    public String getFeatureName() {
        return "Education System";
    }
    
    public Set<String> getDependencies() {
        return Set.of("playerclass"); // Education depends on player classes
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        EducationRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Education system initialization
    }
}


