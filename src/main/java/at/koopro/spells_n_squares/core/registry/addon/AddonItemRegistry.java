package at.koopro.spells_n_squares.core.registry.addon;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Helper class for addons to register items.
 * Provides a DeferredRegister for the addon's namespace.
 */
public final class AddonItemRegistry {
    private final String addonId;
    private final DeferredRegister.Items items;
    
    public AddonItemRegistry(String addonId) {
        this.addonId = addonId;
        this.items = DeferredRegister.createItems(addonId);
    }
    
    /**
     * Gets the DeferredRegister for items.
     * Register this with the mod event bus during registry registration phase.
     * @return The DeferredRegister
     */
    public DeferredRegister.Items getDeferredRegister() {
        return items;
    }
    
    /**
     * Registers an item.
     * @param name The item name (will be used as the registry path)
     * @param itemSupplier The supplier that creates the item
     * @return The DeferredHolder for the registered item
     */
    public net.neoforged.neoforge.registries.DeferredHolder<Item, Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return items.register(name, id -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
            return itemSupplier.get();
        });
    }
    
    /**
     * Registers an item with custom properties factory.
     * @param name The item name
     * @param itemFactory The factory that creates the item from an Identifier
     * @return The DeferredHolder for the registered item
     */
    public net.neoforged.neoforge.registries.DeferredHolder<Item, Item> registerItem(String name, java.util.function.Function<Identifier, Item> itemFactory) {
        return items.register(name, itemFactory);
    }
    
    /**
     * Creates an Identifier for an item within the addon's namespace.
     * @param path The path part of the item ID
     * @return The Identifier
     */
    public Identifier itemId(String path) {
        return Identifier.fromNamespaceAndPath(addonId, path);
    }
    
    /**
     * Gets the addon ID.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
}






