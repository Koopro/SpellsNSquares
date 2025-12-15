package at.koopro.spells_n_squares.init;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import at.koopro.spells_n_squares.core.registry.ModCreativeTabs;
import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModEntities;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import at.koopro.spells_n_squares.features.spell.ModSpells;
import at.koopro.spells_n_squares.features.spell.SpellRegistry;
import at.koopro.spells_n_squares.handlers.ServerEventHandler;
import net.neoforged.neoforge.common.NeoForge;
import at.koopro.spells_n_squares.network.ModNetwork;

/**
 * Central initialization module for the mod.
 * Handles registration of all registries and common setup.
 */
public class ModInitialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Registers all deferred registries with the mod event bus.
     */
    public static void registerRegistries(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        FlashlightItem.DATA_COMPONENTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
    }
    
    /**
     * Registers event handlers and network.
     */
    public static void registerEventHandlers(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModInitialization::commonSetup);
        modEventBus.addListener(ModNetwork::registerPayloadHandlers);
        // Game event bus listeners
        NeoForge.EVENT_BUS.addListener(ServerEventHandler::onRegisterCommands);
        // Note: spells_n_squares.class is not registered to event bus as it has no @SubscribeEvent methods
        modContainer.registerConfig(ModConfig.Type.COMMON, at.koopro.spells_n_squares.Config.SPEC);
    }
    
    /**
     * Common setup phase - registers spells and other initialization.
     */
    private static void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Spells_n_Squares mod");
        LOGGER.info("Registered sound: {}", ModSounds.RUBBER_DUCK_SQUEAK.getId());
        
        // Register all spells
        event.enqueueWork(() -> {
            ModSpells.register();
            LOGGER.info("Registered {} spells", SpellRegistry.getAllIds().size());
        });
    }

}

