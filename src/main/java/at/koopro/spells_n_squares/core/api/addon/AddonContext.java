package at.koopro.spells_n_squares.core.api.addon;

import at.koopro.spells_n_squares.core.api.IPlayerClassManager;
import at.koopro.spells_n_squares.core.api.ISpellManager;
import at.koopro.spells_n_squares.core.api.ISpellRegistry;
import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.registry.addon.AddonEntityRegistry;
import at.koopro.spells_n_squares.core.registry.addon.AddonItemRegistry;
import at.koopro.spells_n_squares.core.registry.addon.AddonNetworkRegistry;
import at.koopro.spells_n_squares.core.registry.addon.AddonPlayerClassRegistry;
import at.koopro.spells_n_squares.core.registry.addon.AddonSpellRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

/**
 * Context providing API access to addons.
 * This is the main entry point for addons to interact with Spells_n_Squares.
 */
public final class AddonContext {
    private final String addonId;
    private final ISpellManager spellManager;
    private final IPlayerClassManager playerClassManager;
    private final ISpellRegistry spellRegistry;
    private final AddonEventBus eventBus;
    private final IEventBus modEventBus;
    private final ModContainer modContainer;
    private final AddonSpellRegistry spellRegistryHelper;
    private final AddonItemRegistry itemRegistryHelper;
    private final AddonEntityRegistry entityRegistryHelper;
    private final AddonNetworkRegistry networkRegistryHelper;
    private final AddonPlayerClassRegistry playerClassRegistryHelper;
    
    public AddonContext(
            String addonId,
            ISpellManager spellManager,
            IPlayerClassManager playerClassManager,
            ISpellRegistry spellRegistry,
            AddonEventBus eventBus,
            IEventBus modEventBus,
            ModContainer modContainer
    ) {
        this.addonId = addonId;
        this.spellManager = spellManager;
        this.playerClassManager = playerClassManager;
        this.spellRegistry = spellRegistry;
        this.eventBus = eventBus;
        this.modEventBus = modEventBus;
        this.modContainer = modContainer;
        this.spellRegistryHelper = new AddonSpellRegistry(addonId, spellRegistry);
        this.itemRegistryHelper = new AddonItemRegistry(addonId);
        this.entityRegistryHelper = new AddonEntityRegistry(addonId);
        this.networkRegistryHelper = new AddonNetworkRegistry();
        this.playerClassRegistryHelper = new AddonPlayerClassRegistry(addonId);
    }
    
    /**
     * Gets the addon ID for this context.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
    
    /**
     * Gets the spell manager API.
     * @return The spell manager
     */
    public ISpellManager getSpellManager() {
        return spellManager;
    }
    
    /**
     * Gets the player class manager API.
     * @return The player class manager
     */
    public IPlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }
    
    /**
     * Gets the spell registry API.
     * @return The spell registry
     */
    public ISpellRegistry getSpellRegistry() {
        return spellRegistry;
    }
    
    /**
     * Gets the addon event bus for subscribing to addon events.
     * @return The addon event bus
     */
    public AddonEventBus getEventBus() {
        return eventBus;
    }
    
    /**
     * Gets the mod event bus for subscribing to Minecraft/NeoForge events.
     * @return The mod event bus
     */
    public IEventBus getModEventBus() {
        return modEventBus;
    }
    
    /**
     * Gets the mod container.
     * @return The mod container
     */
    public ModContainer getModContainer() {
        return modContainer;
    }
    
    /**
     * Gets the spell registry helper for easy spell registration.
     * @return The spell registry helper
     */
    public AddonSpellRegistry getSpellRegistryHelper() {
        return spellRegistryHelper;
    }
    
    /**
     * Gets the item registry helper for easy item registration.
     * @return The item registry helper
     */
    public AddonItemRegistry getItemRegistryHelper() {
        return itemRegistryHelper;
    }
    
    /**
     * Gets the entity registry helper for easy entity registration.
     * @return The entity registry helper
     */
    public AddonEntityRegistry getEntityRegistryHelper() {
        return entityRegistryHelper;
    }
    
    /**
     * Gets the network registry helper for easy network payload registration.
     * @return The network registry helper
     */
    public AddonNetworkRegistry getNetworkRegistryHelper() {
        return networkRegistryHelper;
    }
    
    /**
     * Gets the player class registry helper for easy player class registration.
     * @return The player class registry helper
     */
    public AddonPlayerClassRegistry getPlayerClassRegistryHelper() {
        return playerClassRegistryHelper;
    }
}








