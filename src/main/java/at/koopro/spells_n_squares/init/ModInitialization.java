package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.core.api.SpellFeature;
import at.koopro.spells_n_squares.core.registry.AddonRegistry;
import at.koopro.spells_n_squares.core.registry.FeatureRegistry;
import at.koopro.spells_n_squares.core.registry.ModBlocks;
import at.koopro.spells_n_squares.core.registry.ModCreativeTabs;
import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModEntities;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerAdapters;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerRegistry;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.network.ModNetwork;
import at.koopro.spells_n_squares.features.cloak.CloakChargeData;
import at.koopro.spells_n_squares.features.gear.SocketData;
import at.koopro.spells_n_squares.features.wand.WandAttunementHandler;
import at.koopro.spells_n_squares.features.wand.WandData;
import at.koopro.spells_n_squares.features.artifacts.TimeTurnerItem;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Central initialization module for the mod.
 * Handles registration of all registries and common setup.
 */
public class ModInitialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Registers all deferred registries with the mod event bus.
     */
    public static void registerRegistries(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        FlashlightItem.DATA_COMPONENTS.register(modEventBus);
        WandData.DATA_COMPONENTS.register(modEventBus);
        CloakChargeData.DATA_COMPONENTS.register(modEventBus);
        TimeTurnerItem.DATA_COMPONENTS.register(modEventBus);
        SocketData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.storage.BagInventoryData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.storage.TrunkInventoryData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.storage.PocketDimensionData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.transportation.PortkeyData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.transportation.BroomstickData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.communication.MirrorData.DATA_COMPONENTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        
        // Register all player data managers
        registerPlayerDataManagers();
        
        // Discover and register addons
        AddonRegistry.discoverAndRegisterAddons(modEventBus, modContainer);
        
        // Register addon registries
        AddonRegistry.registerAllAddonRegistries(modEventBus);
        
        // Register features and let them register their own registries
        registerFeatures();
        FeatureRegistry.registerAllRegistries(modEventBus);
    }
    
    /**
     * Registers all features with the feature registry.
     */
    private static void registerFeatures() {
        FeatureRegistry.register(new SpellFeature());
        // Additional features can be registered here
    }
    
    /**
     * Registers all player data managers with the registry.
     */
    private static void registerPlayerDataManagers() {
        PlayerDataManagerRegistry.register(new PlayerDataManagerAdapters.SpellManagerAdapter());
        PlayerDataManagerRegistry.register(new PlayerDataManagerAdapters.PlayerClassManagerAdapter());
        PlayerDataManagerRegistry.register(new PlayerDataManagerAdapters.LumosManagerAdapter());
    }
    
    /**
     * Registers event handlers and network.
     */
    public static void registerEventHandlers(IEventBus modEventBus, ModContainer modContainer) {
        // Initialize all registered features
        FeatureRegistry.initializeAll(modEventBus, modContainer);
        
        // Initialize all registered addons
        AddonRegistry.initializeAllAddons(modEventBus, modContainer);
        
        modEventBus.addListener(ModNetwork::registerPayloadHandlers);
        // Game event bus listeners
        // TODO: Re-enable when ServerEventHandler is implemented
        // NeoForge.EVENT_BUS.addListener(ServerEventHandler::onRegisterCommands);
        // Initialize wand attunement handler
        WandAttunementHandler.initialize();
        // Initialize particle effect registry
        at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry.initializeDefaults();
        // Initialize sound-visual sync
        at.koopro.spells_n_squares.features.fx.SoundVisualSync.initialize();
        // Note: spells_n_squares.class is not registered to event bus as it has no @SubscribeEvent methods
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}

