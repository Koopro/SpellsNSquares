package at.koopro.spells_n_squares.features.communication.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing two-way mirror pairing information.
 */
public final class MirrorData {
    private MirrorData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MirrorDataComponent>> MIRROR_DATA =
        DATA_COMPONENTS.register("mirror_data", () -> DataComponentType.<MirrorDataComponent>builder()
            .persistent(MirrorDataComponent.CODEC)
            .build());
    
    /**
     * Component storing mirror pairing information.
     */
    public record MirrorDataComponent(
        Optional<UUID> pairedMirrorId,
        String mirrorName
    ) {
        public static final Codec<MirrorDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.optionalFieldOf("pairedId").forGetter(MirrorDataComponent::pairedMirrorId),
                Codec.STRING.fieldOf("name").forGetter(MirrorDataComponent::mirrorName)
            ).apply(instance, MirrorDataComponent::new)
        );
        
        public static MirrorDataComponent createDefault(String name) {
            return new MirrorDataComponent(Optional.empty(), name);
        }
        
        public boolean isPaired() {
            return pairedMirrorId.isPresent();
        }
    }
}

