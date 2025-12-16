package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.spell.ModSpells;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * Feature wrapper for the spell system.
 * Implements IFeature to enable self-registration and lifecycle management.
 */
public final class SpellFeature implements IFeature {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            LOGGER.info("Initializing Spells_n_Squares spell feature");
            event.enqueueWork(() -> {
                ModSpells.register();
                LOGGER.info("Registered {} spells", SpellRegistry.getAllIds().size());
            });
        });
    }
    
    @Override
    public String getFeatureName() {
        return "SpellSystem";
    }
}








