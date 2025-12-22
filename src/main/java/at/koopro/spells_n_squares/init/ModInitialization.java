package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.core.api.SpellFeature;
import at.koopro.spells_n_squares.core.registry.AddonRegistry;
import at.koopro.spells_n_squares.core.registry.FeatureRegistry;
import at.koopro.spells_n_squares.core.registry.ModBlocks;
import at.koopro.spells_n_squares.core.registry.ModCreativeTabs;
import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModEntities;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerAdapters;
import at.koopro.spells_n_squares.core.registry.PlayerDataManagerRegistry;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.network.ModNetwork;
import at.koopro.spells_n_squares.features.gear.SocketData;
import at.koopro.spells_n_squares.features.wand.WandAttunementHandler;
import at.koopro.spells_n_squares.features.artifacts.ArtifactsRegistry;
import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
import at.koopro.spells_n_squares.features.building.BuildingRegistry;
import at.koopro.spells_n_squares.features.cloak.CloakRegistry;
import at.koopro.spells_n_squares.features.combat.CombatRegistry;
import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
import at.koopro.spells_n_squares.features.education.EducationRegistry;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry;
import at.koopro.spells_n_squares.features.navigation.NavigationRegistry;
import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.quidditch.QuidditchRegistry;
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import at.koopro.spells_n_squares.features.spell.SpellEntityRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
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
        // Register core registries first
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        
        // Register generic data components
        SocketData.DATA_COMPONENTS.register(modEventBus);
        
        // Register all feature registries
        FlashlightRegistry.register(modEventBus);
        WandRegistry.register(modEventBus);
        CloakRegistry.register(modEventBus);
        ArtifactsRegistry.register(modEventBus);
        StorageRegistry.register(modEventBus);
        TransportationRegistry.register(modEventBus);
        CommunicationRegistry.register(modEventBus);
        AutomationRegistry.register(modEventBus);
        BuildingRegistry.register(modEventBus);
        NavigationRegistry.register(modEventBus);
        RobesRegistry.register(modEventBus);
        PotionsRegistry.register(modEventBus);
        QuidditchRegistry.register(modEventBus);
        EconomyRegistry.register(modEventBus);
        EducationRegistry.register(modEventBus);
        CombatRegistry.register(modEventBus);
        EnchantmentsRegistry.register(modEventBus);
        SpellEntityRegistry.register(modEventBus);
        
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
        // Note: ServerEventHandler is automatically registered via @EventBusSubscriber annotation
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

