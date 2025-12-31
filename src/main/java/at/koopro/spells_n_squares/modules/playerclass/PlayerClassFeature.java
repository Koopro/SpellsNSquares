package at.koopro.spells_n_squares.modules.playerclass;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.features.playerclass.data.PlayerClassData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.Set;

/**
 * Feature implementation for the player class system.
 */
public class PlayerClassFeature implements IFeature {
    public String getFeatureId() {
        return "playerclass";
    }
    
    @Override
    public String getFeatureName() {
        return "Player Class System";
    }
    
    public Set<String> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        PlayerClassData.DATA_COMPONENTS.register(modEventBus);
    }
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Player class system initialization
    }
}

