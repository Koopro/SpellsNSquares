package at.koopro.spells_n_squares.features.transportation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing broomstick properties.
 */
public final class BroomstickData {
    private BroomstickData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BroomstickDataComponent>> BROOMSTICK_DATA =
        DATA_COMPONENTS.register("broomstick_data", () -> DataComponentType.<BroomstickDataComponent>builder()
            .persistent(BroomstickDataComponent.CODEC)
            .build());
    
    /**
     * Component storing broomstick tier and stamina.
     */
    public record BroomstickDataComponent(
        String tier,
        float currentStamina,
        float maxStamina,
        float speed
    ) {
        public static final Codec<BroomstickDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("tier").forGetter(BroomstickDataComponent::tier),
                Codec.FLOAT.fieldOf("currentStamina").forGetter(BroomstickDataComponent::currentStamina),
                Codec.FLOAT.fieldOf("maxStamina").forGetter(BroomstickDataComponent::maxStamina),
                Codec.FLOAT.fieldOf("speed").forGetter(BroomstickDataComponent::speed)
            ).apply(instance, BroomstickDataComponent::new)
        );
        
        public static BroomstickDataComponent createDefault(String tier, float maxStamina, float speed) {
            return new BroomstickDataComponent(tier, maxStamina, maxStamina, speed);
        }
    }
}

