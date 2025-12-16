package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing Philosopher's Stone usage information.
 */
public final class PhilosophersStoneData {
    private PhilosophersStoneData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PhilosophersStoneComponent>> PHILOSOPHERS_STONE_DATA =
        DATA_COMPONENTS.register(
            "philosophers_stone_data",
            () -> DataComponentType.<PhilosophersStoneComponent>builder()
                .persistent(PhilosophersStoneComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing Philosopher's Stone state.
     */
    public record PhilosophersStoneComponent(
        long lastUseTick,
        int usesRemaining
    ) {
        public static final Codec<PhilosophersStoneComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.LONG.fieldOf("lastUse").forGetter(PhilosophersStoneComponent::lastUseTick),
                Codec.INT.fieldOf("usesRemaining").forGetter(PhilosophersStoneComponent::usesRemaining)
            ).apply(instance, PhilosophersStoneComponent::new)
        );
        
        public PhilosophersStoneComponent() {
            this(0L, 100); // 100 uses by default
        }
        
        public PhilosophersStoneComponent withUse(long tick) {
            return new PhilosophersStoneComponent(tick, Math.max(0, usesRemaining - 1));
        }
        
        public boolean canUse() {
            return usesRemaining > 0;
        }
    }
}
