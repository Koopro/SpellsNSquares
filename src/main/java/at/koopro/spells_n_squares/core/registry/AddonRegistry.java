package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.api.IPlayerClassManager;
import at.koopro.spells_n_squares.core.api.ISpellManager;
import at.koopro.spells_n_squares.core.api.ISpellRegistry;
import at.koopro.spells_n_squares.core.api.ModContext;
import at.koopro.spells_n_squares.core.api.addon.AddonContext;
import at.koopro.spells_n_squares.core.api.addon.AddonMetadata;
import at.koopro.spells_n_squares.core.api.addon.AddonMod;
import at.koopro.spells_n_squares.core.api.addon.IAddon;
import at.koopro.spells_n_squares.core.api.addon.dependency.DependencyChecker;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry for managing addons.
 * Handles discovery, dependency resolution, and lifecycle management.
 */
public final class AddonRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String API_VERSION = "1.0.0";
    
    // Using LinkedHashMap - insertion order matters for initialization order
    private static final Map<String, AddonEntry> registeredAddons = new LinkedHashMap<>();
    private static final Map<String, AddonMetadata> addonMetadata = new HashMap<>();
    
    /**
     * Represents a registered addon entry.
     */
    private static final class AddonEntry {
        final IAddon addon;
        final AddonMetadata metadata;
        final AddonContext context;
        
        AddonEntry(IAddon addon, AddonMetadata metadata, AddonContext context) {
            this.addon = addon;
            this.metadata = metadata;
            this.context = context;
        }
    }
    
    /**
     * Discovers and registers all addons.
     * Scans for classes annotated with @AddonMod and implements IAddon.
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    public static void discoverAndRegisterAddons(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Discovering addons for Spells_n_Squares...");
        
        // Discover addons from all loaded mods
        List<IAddon> discoveredAddons = discoverAddons();
        
        if (discoveredAddons.isEmpty()) {
            LOGGER.info("No addons discovered");
            return;
        }
        
        LOGGER.info("Found {} potential addon(s)", discoveredAddons.size());
        
        // Validate and register addons
        for (IAddon addon : discoveredAddons) {
            try {
                registerAddon(addon, modEventBus, modContainer);
            } catch (Exception e) {
                LOGGER.error("Failed to register addon '{}': {}", addon.getAddonId(), e.getMessage(), e);
            }
        }
        
        LOGGER.info("Successfully registered {} addon(s)", registeredAddons.size());
    }
    
    /**
     * Manually registers an addon.
     * Useful for testing or programmatic registration.
     * @param addon The addon to register
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    public static void registerAddon(IAddon addon, IEventBus modEventBus, ModContainer modContainer) {
        if (addon == null) {
            throw new IllegalArgumentException("Addon cannot be null");
        }
        
        String addonId = addon.getAddonId();
        
        if (registeredAddons.containsKey(addonId)) {
            LOGGER.warn("Addon '{}' is already registered, skipping", addonId);
            return;
        }
        
        // Extract metadata from annotation or addon instance
        AddonMetadata metadata = extractMetadata(addon);
        
        // Validate dependencies
        List<String> dependencyErrors = DependencyChecker.validateDependencies(metadata.getDependencies(), addonId);
        if (!dependencyErrors.isEmpty()) {
            throw new IllegalStateException("Addon '" + addonId + "' has unsatisfied dependencies: " + 
                                         String.join(", ", dependencyErrors));
        }
        
        // Check API version
        if (!DependencyChecker.checkApiVersion(metadata.getMinApiVersion())) {
            throw new IllegalStateException("Addon '" + addonId + "' requires API version " + 
                                         metadata.getMinApiVersion() + " but current version is " + API_VERSION);
        }
        
        // Create addon context
        AddonContext context = createAddonContext(addonId, modEventBus, modContainer);
        
        // Register the addon
        AddonEntry entry = new AddonEntry(addon, metadata, context);
        registeredAddons.put(addonId, entry);
        addonMetadata.put(addonId, metadata);
        
        LOGGER.info("Registered addon: {} v{}", metadata.getAddonName(), metadata.getAddonVersion());
    }
    
    /**
     * Initializes all registered addons.
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    public static void initializeAllAddons(IEventBus modEventBus, ModContainer modContainer) {
        for (AddonEntry entry : registeredAddons.values()) {
            try {
                LOGGER.debug("Initializing addon: {}", entry.metadata.getAddonName());
                entry.addon.initialize(entry.context);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize addon '{}': {}", entry.metadata.getAddonId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Registers all registries for all registered addons.
     * @param modEventBus The mod event bus
     */
    public static void registerAllAddonRegistries(IEventBus modEventBus) {
        for (AddonEntry entry : registeredAddons.values()) {
            try {
                entry.addon.registerRegistries(modEventBus);
            } catch (Exception e) {
                LOGGER.error("Failed to register registries for addon '{}': {}", 
                           entry.metadata.getAddonId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Initializes all addons on the client side.
     */
    public static void initializeAllAddonsClient() {
        for (AddonEntry entry : registeredAddons.values()) {
            try {
                entry.addon.clientInit(entry.context);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize client side for addon '{}': {}", 
                           entry.metadata.getAddonId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Registers all network payloads from all registered addons.
     * @param registrar The payload registrar
     */
    public static void registerAllAddonNetworkPayloads(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar) {
        for (AddonEntry entry : registeredAddons.values()) {
            try {
                entry.context.getNetworkRegistryHelper().applyRegistrations(registrar);
            } catch (Exception e) {
                LOGGER.error("Failed to register network payloads for addon '{}': {}", 
                           entry.metadata.getAddonId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Gets all registered addon IDs.
     * @return Set of addon IDs
     */
    public static Set<String> getRegisteredAddonIds() {
        return Collections.unmodifiableSet(registeredAddons.keySet());
    }
    
    /**
     * Gets metadata for a specific addon.
     * @param addonId The addon ID
     * @return The metadata, or null if not found
     */
    public static AddonMetadata getAddonMetadata(String addonId) {
        return addonMetadata.get(addonId);
    }
    
    /**
     * Checks if an addon is registered.
     * @param addonId The addon ID
     * @return true if registered
     */
    public static boolean isAddonRegistered(String addonId) {
        return registeredAddons.containsKey(addonId);
    }
    
    /**
     * Discovers addons by scanning loaded mods for @AddonMod annotations.
     * @return List of discovered addons
     */
    private static List<IAddon> discoverAddons() {
        List<IAddon> addons = new ArrayList<>();
        
        // Preferred discovery: ServiceLoader via META-INF/services entries provided by addons
        try {
            ServiceLoader<IAddon> loader = ServiceLoader.load(IAddon.class, AddonRegistry.class.getClassLoader());
            for (IAddon addon : loader) {
                addons.add(addon);
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading addons via ServiceLoader: {}", e.getMessage());
        }
        
        // Fallback: manual registration can still be used by calling registerAddon directly
        return addons;
    }
    
    /**
     * Extracts metadata from an addon instance.
     * Checks for @AddonMod annotation first, then falls back to addon methods.
     * @param addon The addon instance
     * @return The metadata
     */
    private static AddonMetadata extractMetadata(IAddon addon) {
        Class<?> addonClass = addon.getClass();
        AddonMod annotation = addonClass.getAnnotation(AddonMod.class);
        
        if (annotation != null) {
            return new AddonMetadata(
                annotation.modId(),
                annotation.name(),
                annotation.version(),
                annotation.minApiVersion(),
                Arrays.asList(annotation.dependencies())
            );
        }
        
        // Fallback to addon methods if no annotation
        return new AddonMetadata(
            addon.getAddonId(),
            addon.getAddonName(),
            addon.getAddonVersion(),
            "1.0.0", // Default min API version
            Collections.emptyList()
        );
    }
    
    /**
     * Creates an AddonContext for an addon.
     * @param addonId The addon ID
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     * @return The addon context
     */
    private static AddonContext createAddonContext(String addonId, IEventBus modEventBus, ModContainer modContainer) {
        // Get API instances from ModContext
        ISpellManager spellManager = ModContext.getSpellManager();
        IPlayerClassManager playerClassManager = ModContext.getPlayerClassManager();
        ISpellRegistry spellRegistry = ModContext.getSpellRegistry();
        
        // Create event bus for addon events (will be created in Phase 2)
        at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus eventBus = 
            at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus.getInstance();
        
        return new AddonContext(
            addonId,
            spellManager,
            playerClassManager,
            spellRegistry,
            eventBus,
            modEventBus,
            modContainer
        );
    }
}

