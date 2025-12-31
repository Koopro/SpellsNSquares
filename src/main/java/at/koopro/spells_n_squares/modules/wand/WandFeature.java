package at.koopro.spells_n_squares.modules.wand;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.core.services.ModServices;
import at.koopro.spells_n_squares.features.wand.WandAttunementHandler;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import at.koopro.spells_n_squares.modules.wand.api.IWandService;
import at.koopro.spells_n_squares.modules.wand.internal.WandService;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the wand system.
 */
public class WandFeature implements IFeature {
    public String getFeatureId() {
        return "wand";
    }
    
    @Override
    public String getFeatureName() {
        return "Wand System";
    }
    
    public Set<String> getDependencies() {
        // Wand system doesn't depend on other features
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        WandRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Register wand service
        ModServices.register(IWandService.class, new WandService());
        
        // Initialize wand attunement handler (legacy - will be migrated to service)
        WandAttunementHandler.initialize();
    }
    
    public void registerConfig(ModContainer modContainer) {
        // Wand config is now merged into the main Config.SPEC to avoid file conflicts
        // No separate config registration needed
    }
}

