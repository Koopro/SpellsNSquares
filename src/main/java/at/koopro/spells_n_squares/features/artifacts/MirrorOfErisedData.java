package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing Mirror of Erised desire information.
 */
public final class MirrorOfErisedData {
    private MirrorOfErisedData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MirrorOfErisedComponent>> MIRROR_OF_ERISED_DATA =
        DATA_COMPONENTS.register(
            "mirror_of_erised_data",
            () -> DataComponentType.<MirrorOfErisedComponent>builder()
                .persistent(MirrorOfErisedComponent.CODEC)
                .build()
        );
    
    /**
     * Represents a player desire.
     */
    public record Desire(
        String description,
        DesireType type,
        long timestamp
    ) {
        public static final Codec<Desire> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("description").forGetter(Desire::description),
                Codec.STRING.xmap(DesireType::valueOf, Enum::name).fieldOf("type").forGetter(Desire::type),
                Codec.LONG.fieldOf("timestamp").forGetter(Desire::timestamp)
            ).apply(instance, Desire::new)
        );
    }
    
    public enum DesireType {
        ITEM,
        ACHIEVEMENT,
        LOCATION,
        POWER,
        KNOWLEDGE,
        RELATIONSHIP,
        OTHER
    }
    
    /**
     * Component storing player desires.
     */
    public record MirrorOfErisedComponent(
        List<Desire> desires
    ) {
        public static final Codec<MirrorOfErisedComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Desire.CODEC).fieldOf("desires").forGetter(MirrorOfErisedComponent::desires)
            ).apply(instance, MirrorOfErisedComponent::new)
        );
        
        public MirrorOfErisedComponent() {
            this(new ArrayList<>());
        }
        
        public MirrorOfErisedComponent addDesire(String description, DesireType type, long timestamp) {
            List<Desire> newDesires = new ArrayList<>(desires);
            newDesires.add(new Desire(description, type, timestamp));
            // Keep only last 10 desires
            if (newDesires.size() > 10) {
                newDesires.remove(0);
            }
            return new MirrorOfErisedComponent(newDesires);
        }
        
        public Desire getPrimaryDesire() {
            if (desires.isEmpty()) {
                return null;
            }
            // Return the most recent desire
            return desires.get(desires.size() - 1);
        }
    }
}











