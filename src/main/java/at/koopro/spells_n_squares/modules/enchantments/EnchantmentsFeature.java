package at.koopro.spells_n_squares.modules.enchantments;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the enchantments system.
 */
public class EnchantmentsFeature implements IFeature {
    public String getFeatureId() {
        return "enchantments";
    }
    
    @Override
    public String getFeatureName() {
        return "Enchantments System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        EnchantmentsRegistry.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Enchantments system initialization
    }
}


