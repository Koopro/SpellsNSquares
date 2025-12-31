package at.koopro.spells_n_squares.features.artifacts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing Pensieve memory snapshots.
 */
public final class PensieveData {
    private PensieveData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PensieveComponent>> PENSIEVE_DATA =
        DATA_COMPONENTS.register(
            "pensieve_data",
            () -> DataComponentType.<PensieveComponent>builder()
                .persistent(PensieveComponent.CODEC)
                .build()
        );
    
    /**
     * Represents a memory snapshot.
     */
    public record MemorySnapshot(
        String description,
        long timestamp,
        String location
    ) {
        public static final Codec<MemorySnapshot> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("description").forGetter(MemorySnapshot::description),
                Codec.LONG.fieldOf("timestamp").forGetter(MemorySnapshot::timestamp),
                Codec.STRING.fieldOf("location").forGetter(MemorySnapshot::location)
            ).apply(instance, MemorySnapshot::new)
        );
    }
    
    /**
     * Component storing Pensieve memories.
     */
    public record PensieveComponent(
        List<MemorySnapshot> memories
    ) {
        public static final Codec<PensieveComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(MemorySnapshot.CODEC).fieldOf("memories").forGetter(PensieveComponent::memories)
            ).apply(instance, PensieveComponent::new)
        );
        
        public PensieveComponent() {
            this(new ArrayList<>());
        }
        
        public PensieveComponent addMemory(String description, long timestamp, String location) {
            List<MemorySnapshot> newMemories = new ArrayList<>(memories);
            newMemories.add(new MemorySnapshot(description, timestamp, location));
            // Keep only last 50 memories
            if (newMemories.size() > 50) {
                newMemories.remove(0);
            }
            return new PensieveComponent(newMemories);
        }
    }
}
