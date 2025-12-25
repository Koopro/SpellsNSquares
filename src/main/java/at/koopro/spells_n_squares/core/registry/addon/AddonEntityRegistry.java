package at.koopro.spells_n_squares.core.registry.addon;

import at.koopro.spells_n_squares.core.util.AddonRegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Helper class for addons to register entities.
 * Provides a DeferredRegister for the addon's namespace.
 */
public final class AddonEntityRegistry {
    private final String addonId;
    private final DeferredRegister<EntityType<?>> entities;
    
    public AddonEntityRegistry(String addonId) {
        this.addonId = addonId;
        this.entities = DeferredRegister.create(Registries.ENTITY_TYPE, addonId);
    }
    
    /**
     * Gets the DeferredRegister for entities.
     * Register this with the mod event bus during registry registration phase.
     * @return The DeferredRegister
     */
    public DeferredRegister<EntityType<?>> getDeferredRegister() {
        return entities;
    }
    
    /**
     * Registers an entity type.
     * @param name The entity name (will be used as the registry path)
     * @param entityTypeSupplier The supplier that creates the EntityType
     * @return The DeferredHolder for the registered entity type
     */
    public net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<?>> registerEntity(
            String name,
            Supplier<EntityType<?>> entityTypeSupplier
    ) {
        return entities.register(name, () -> entityTypeSupplier.get());
    }
    
    /**
     * Registers an entity type with a builder.
     * @param name The entity name
     * @param builder The EntityType.Builder
     * @param <T> The entity type
     * @return The DeferredHolder for the registered entity type
     */
    public <T extends Entity> net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(
            String name,
            EntityType.Builder<T> builder
    ) {
        return entities.register(name, () -> {
            Identifier id = AddonRegistryUtils.addonId(addonId, name);
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
            return builder.build(key);
        });
    }
    
    /**
     * Creates an Identifier for an entity within the addon's namespace.
     * @param path The path part of the entity ID
     * @return The Identifier
     */
    public Identifier entityId(String path) {
        return AddonRegistryUtils.addonId(addonId, path);
    }
    
    /**
     * Gets the addon ID.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
}







