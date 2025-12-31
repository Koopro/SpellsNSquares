package at.koopro.spells_n_squares.modules.automation;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the automation system.
 */
public class AutomationFeature implements IFeature {
    public String getFeatureId() {
        return "automation";
    }
    
    @Override
    public String getFeatureName() {
        return "Automation System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        AutomationRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Automation system initialization
    }
}


