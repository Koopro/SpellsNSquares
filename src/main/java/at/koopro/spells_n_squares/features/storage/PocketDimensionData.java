package at.koopro.spells_n_squares.features.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing pocket dimension information.
 */
public final class PocketDimensionData {
    private PocketDimensionData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PocketDimensionComponent>> POCKET_DIMENSION =
        DATA_COMPONENTS.register("pocket_dimension", () -> DataComponentType.<PocketDimensionComponent>builder()
            .persistent(PocketDimensionComponent.CODEC)
            .build());
    
    /**
     * Component storing pocket dimension reference.
     */
    public record PocketDimensionComponent(
        ResourceKey<Level> dimensionKey,
        int size
    ) {
        public static final Codec<PocketDimensionComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PocketDimensionComponent::dimensionKey),
                Codec.INT.fieldOf("size").forGetter(PocketDimensionComponent::size)
            ).apply(instance, PocketDimensionComponent::new)
        );
        
        public static PocketDimensionComponent createDefault(int size) {
            // In full implementation, would create a custom dimension
            // For now, placeholder
            return new PocketDimensionComponent(Level.OVERWORLD, size);
        }
    }
}

