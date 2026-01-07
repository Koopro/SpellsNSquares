package at.koopro.spells_n_squares.features.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing Philosopher's Stone state (materia and entropy).
 */
public final class PhilosophersStoneData {
    private PhilosophersStoneData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StoneComponent>> STONE_DATA =
        DATA_COMPONENTS.register("philosophers_stone_data", () -> DataComponentType.<StoneComponent>builder()
            .persistent(StoneComponent.CODEC)
            .build());
    
    /**
     * Component storing stone state.
     */
    public record StoneComponent(
        long materia,
        int entropy
    ) {
        public static final Codec<StoneComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.LONG.optionalFieldOf("materia", 0L).forGetter(StoneComponent::materia),
                Codec.INT.optionalFieldOf("entropy", 0).forGetter(StoneComponent::entropy)
            ).apply(instance, StoneComponent::new)
        );
        
        public static StoneComponent createDefault() {
            return new StoneComponent(0L, 0);
        }
        
        public StoneComponent withMateria(long materia) {
            return new StoneComponent(materia, entropy);
        }
        
        public StoneComponent withEntropy(int entropy) {
            // Clamp entropy between 0 and 100
            int clamped = Math.max(0, Math.min(100, entropy));
            return new StoneComponent(materia, clamped);
        }
        
        public StoneComponent addMateria(long amount) {
            return withMateria(materia + amount);
        }
        
        public StoneComponent addEntropy(int amount) {
            return withEntropy(entropy + amount);
        }
        
        public boolean isAtCriticalMass() {
            return entropy >= 100;
        }
        
        public boolean isInDangerZone() {
            return entropy >= 50;
        }
    }
}


