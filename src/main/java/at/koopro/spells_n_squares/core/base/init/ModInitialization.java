package at.koopro.spells_n_squares.core.base.init;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features_registry.SpellFeature;
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
import at.koopro.spells_n_squares.features.wand.system.WandAttunementHandler;
import at.koopro.spells_n_squares.features.spell.manager.SpellEntityRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.wand.registry.WandRegistry;
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
        DevLogger.logMethodEntry(ModInitialization.class, "registerRegistries");
        // Register core registries first
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        at.koopro.spells_n_squares.core.registry.ModParticles.PARTICLE_TYPES.register(modEventBus);
        DevLogger.logStateChange(ModInitialization.class, "registerRegistries", "Core registries registered");
        
        // Register all feature registries
        at.koopro.spells_n_squares.features.fx.FxRegistry.register(modEventBus);
        WandRegistry.register(modEventBus);
        StorageRegistry.register(modEventBus);
        at.koopro.spells_n_squares.features.economy.EconomyRegistry.register(modEventBus);
        at.koopro.spells_n_squares.features.enchantments.EnchantmentRegistry.register(modEventBus);
        at.koopro.spells_n_squares.features.mail.MailRegistry.register(modEventBus);
        SpellEntityRegistry.register(modEventBus);
        at.koopro.spells_n_squares.features.artifact.ArtifactRegistry.register(modEventBus);
        DevLogger.logStateChange(ModInitialization.class, "registerRegistries", "Feature registries registered");
        
        // Register all player data managers
        registerPlayerDataManagers();
        
        // Register features and let them register their own registries
        registerFeatures();
        FeatureRegistry.registerAllRegistries(modEventBus);
        DevLogger.logStateChange(ModInitialization.class, "registerRegistries", "All registries registered");
        DevLogger.logMethodExit(ModInitialization.class, "registerRegistries");
    }
    
    /**
     * Registers all features with the feature registry.
     */
    private static void registerFeatures() {
        DevLogger.logMethodEntry(ModInitialization.class, "registerFeatures");
        FeatureRegistry.register(new SpellFeature());
        DevLogger.logStateChange(ModInitialization.class, "registerFeatures", "Features registered");
        // Additional features can be registered here
        DevLogger.logMethodExit(ModInitialization.class, "registerFeatures");
    }
    
    /**
     * Registers all player data managers with the registry.
     */
    private static void registerPlayerDataManagers() {
        DevLogger.logMethodEntry(ModInitialization.class, "registerPlayerDataManagers");
        PlayerDataManagerRegistry.register(new PlayerDataManagerAdapters.SpellManagerAdapter());
        PlayerDataManagerRegistry.register(new PlayerDataManagerAdapters.LumosManagerAdapter());
        DevLogger.logStateChange(ModInitialization.class, "registerPlayerDataManagers", "Player data managers registered");
        DevLogger.logMethodExit(ModInitialization.class, "registerPlayerDataManagers");
    }
    
    /**
     * Registers event handlers and network.
     */
    public static void registerEventHandlers(IEventBus modEventBus, ModContainer modContainer) {
        DevLogger.logMethodEntry(ModInitialization.class, "registerEventHandlers");
        // Initialize all registered features
        FeatureRegistry.initializeAll(modEventBus, modContainer);
        DevLogger.logStateChange(ModInitialization.class, "registerEventHandlers", "Features initialized");
        
        modEventBus.addListener(ModNetwork::registerPayloadHandlers);
        // Note: ServerEventHandler is automatically registered via @EventBusSubscriber annotation
        // Initialize wand attunement handler
        WandAttunementHandler.initialize();
        // Initialize particle effect registry
        at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry.initializeDefaults();
        // Initialize sound-visual sync
        at.koopro.spells_n_squares.features.fx.sync.SoundVisualSync.initialize();
        // Note: spells_n_squares.class is not registered to event bus as it has no @SubscribeEvent methods
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        DevLogger.logStateChange(ModInitialization.class, "registerEventHandlers", "Event handlers registered");
        DevLogger.logMethodExit(ModInitialization.class, "registerEventHandlers");
    }

}

