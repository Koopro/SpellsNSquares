package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

/**
 * Data component for storing captured light in the Deluminator.
 */
public final class DeluminatorData {
    private DeluminatorData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DeluminatorComponent>> DELUMINATOR_DATA =
        DATA_COMPONENTS.register(
            "deluminator_data",
            () -> DataComponentType.<DeluminatorComponent>builder()
                .persistent(DeluminatorComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing captured light block state.
     */
    public record DeluminatorComponent(Optional<BlockState> capturedLight) {
        public static final Codec<DeluminatorComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                BlockState.CODEC.optionalFieldOf("capturedLight").forGetter(DeluminatorComponent::capturedLight)
            ).apply(instance, DeluminatorComponent::new)
        );
        
        public DeluminatorComponent() {
            this(Optional.empty());
        }
        
        public boolean hasCapturedLight() {
            return capturedLight.isPresent();
        }
        
        public BlockState getCapturedLight() {
            return capturedLight.orElse(null);
        }
        
        public DeluminatorComponent withCapturedLight(BlockState lightState) {
            return new DeluminatorComponent(Optional.of(lightState));
        }
        
        public DeluminatorComponent clearCapturedLight() {
            return new DeluminatorComponent(Optional.empty());
        }
    }
}











