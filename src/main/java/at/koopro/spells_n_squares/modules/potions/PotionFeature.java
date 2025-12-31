package at.koopro.spells_n_squares.modules.potions;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the potion system.
 */
public class PotionFeature implements IFeature {
    public String getFeatureId() {
        return "potions";
    }
    
    @Override
    public String getFeatureName() {
        return "Potion System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        PotionsRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Potion system initialization
    }
}

