package at.koopro.spells_n_squares.features.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.UUID;

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
     * Type of pocket dimension.
     */
    public enum DimensionType {
        STANDARD,
        NEWTS_CASE
    }
    
    /**
     * Component storing pocket dimension reference.
     */
    public record PocketDimensionComponent(
        ResourceKey<Level> dimensionKey,
        int size,
        UUID dimensionId,
        DimensionType type,
        Optional<ResourceKey<Level>> entryDimension,
        Optional<BlockPos> entryPosition
    ) {
        public static final Codec<PocketDimensionComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PocketDimensionComponent::dimensionKey),
                Codec.INT.fieldOf("size").forGetter(PocketDimensionComponent::size),
                UUIDUtil.CODEC.fieldOf("dimensionId").forGetter(PocketDimensionComponent::dimensionId),
                Codec.STRING.xmap(
                    s -> DimensionType.valueOf(s.toUpperCase()),
                    t -> t.name().toLowerCase()
                ).optionalFieldOf("type", DimensionType.STANDARD).forGetter(PocketDimensionComponent::type),
                ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("entryDimension").forGetter(PocketDimensionComponent::entryDimension),
                BlockPos.CODEC.optionalFieldOf("entryPosition").forGetter(PocketDimensionComponent::entryPosition)
            ).apply(instance, PocketDimensionComponent::new)
        );
        
        public static PocketDimensionComponent createDefault(int size) {
            return createDefault(size, DimensionType.STANDARD);
        }
        
        public static PocketDimensionComponent createDefault(int size, DimensionType type) {
            // Generate unique ID for this pocket dimension
            UUID dimensionId = UUID.randomUUID();
            ResourceKey<Level> dimensionKey = PocketDimensionManager.getOrCreateDimensionKey(dimensionId, type);
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.empty(),
                Optional.empty()
            );
        }
        
        public static PocketDimensionComponent createNewtsCase(int size) {
            return createDefault(size, DimensionType.NEWTS_CASE);
        }
        
        public PocketDimensionComponent withEntry(ResourceKey<Level> entryDimension, BlockPos entryPosition) {
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.of(entryDimension),
                Optional.of(entryPosition)
            );
        }
        
        public PocketDimensionComponent clearEntry() {
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.empty(),
                Optional.empty()
            );
        }
    }
}

