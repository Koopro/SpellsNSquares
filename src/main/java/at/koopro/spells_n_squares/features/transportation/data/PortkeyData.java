package at.koopro.spells_n_squares.features.transportation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing portkey location data.
 */
public final class PortkeyData {
    private PortkeyData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PortkeyDataComponent>> PORTKEY_DATA =
        DATA_COMPONENTS.register("portkey_data", () -> DataComponentType.<PortkeyDataComponent>builder()
            .persistent(PortkeyDataComponent.CODEC)
            .build());
    
    /**
     * Component storing portkey destination information.
     */
    public record PortkeyDataComponent(
        ResourceKey<Level> dimension,
        double x, double y, double z,
        long lastUseTick,
        boolean reusable
    ) {
        public static final Codec<PortkeyDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PortkeyDataComponent::dimension),
                Codec.DOUBLE.fieldOf("x").forGetter(PortkeyDataComponent::x),
                Codec.DOUBLE.fieldOf("y").forGetter(PortkeyDataComponent::y),
                Codec.DOUBLE.fieldOf("z").forGetter(PortkeyDataComponent::z),
                Codec.LONG.fieldOf("lastUse").forGetter(PortkeyDataComponent::lastUseTick),
                Codec.BOOL.fieldOf("reusable").forGetter(PortkeyDataComponent::reusable)
            ).apply(instance, PortkeyDataComponent::new)
        );
        
        public BlockPos getBlockPos() {
            return new BlockPos((int) x, (int) y, (int) z);
        }
        
        public static PortkeyDataComponent createUnset() {
            return new PortkeyDataComponent(Level.OVERWORLD, 0, 0, 0, 0, false);
        }
    }
}

