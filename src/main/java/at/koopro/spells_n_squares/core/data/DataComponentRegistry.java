package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Utility class for standardizing data component registration patterns.
 * Provides common methods for creating DeferredRegister and registering data components.
 * 
 * <p>This utility reduces duplication across all *Data.java files by providing
 * a consistent way to register data components with persistent codecs.
 */
public final class DataComponentRegistry {
    private DataComponentRegistry() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a DeferredRegister for data components with the mod's ID.
     * This is the standard way to create a data component registry.
     * 
     * @return A new DeferredRegister for data components
     */
    public static DeferredRegister<DataComponentType<?>> createRegistry() {
        return DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SpellsNSquares.MODID);
    }
    
    /**
     * Registers a persistent data component type with the given name and codec.
     * This is the standard pattern for registering data components.
     * 
     * @param <T> The component type
     * @param registry The DeferredRegister to register with
     * @param name The name of the data component (e.g., "goblet_of_fire_data")
     * @param codec The codec for serialization
     * @return A DeferredHolder for the registered data component type
     */
    public static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerPersistent(
            DeferredRegister<DataComponentType<?>> registry,
            String name,
            Codec<T> codec) {
        return registry.register(
            name,
            () -> DataComponentType.<T>builder()
                .persistent(codec)
                .build()
        );
    }
}









