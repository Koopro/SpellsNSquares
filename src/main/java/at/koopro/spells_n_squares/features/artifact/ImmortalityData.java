package at.koopro.spells_n_squares.features.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing player immortality state.
 */
public final class ImmortalityData {
    private ImmortalityData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ImmortalityComponent>> IMMORTALITY_DATA =
        DATA_COMPONENTS.register("immortality_data", () -> DataComponentType.<ImmortalityComponent>builder()
            .persistent(ImmortalityComponent.CODEC)
            .build());
    
    /**
     * Component storing immortality state.
     */
    public record ImmortalityComponent(
        int ticksRemaining,
        boolean hasEverDrunk
    ) {
        public static final Codec<ImmortalityComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.optionalFieldOf("ticksRemaining", 0).forGetter(ImmortalityComponent::ticksRemaining),
                Codec.BOOL.optionalFieldOf("hasEverDrunk", false).forGetter(ImmortalityComponent::hasEverDrunk)
            ).apply(instance, ImmortalityComponent::new)
        );
        
        public static ImmortalityComponent createDefault() {
            return new ImmortalityComponent(0, false);
        }
        
        public ImmortalityComponent withTicksRemaining(int ticksRemaining) {
            return new ImmortalityComponent(Math.max(0, ticksRemaining), hasEverDrunk);
        }
        
        public ImmortalityComponent withHasEverDrunk(boolean hasEverDrunk) {
            return new ImmortalityComponent(ticksRemaining, hasEverDrunk);
        }
        
        public ImmortalityComponent tick() {
            if (ticksRemaining > 0) {
                return withTicksRemaining(ticksRemaining - 1);
            }
            return this;
        }
        
        public boolean isImmortal() {
            return hasEverDrunk && ticksRemaining > 0;
        }
        
        public boolean isWithered() {
            return hasEverDrunk && ticksRemaining == 0;
        }
    }
}

