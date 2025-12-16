package at.koopro.spells_n_squares.features.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing socketed charms/runes in gear items.
 */
public final class SocketData {
    private SocketData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SocketDataComponent>> SOCKET_DATA =
        DATA_COMPONENTS.register(
            "socket_data",
            () -> DataComponentType.<SocketDataComponent>builder()
                .persistent(SocketDataComponent.CODEC)
                .build()
        );
    
    /**
     * Component data for sockets.
     */
    public record SocketDataComponent(
        List<Identifier> socketedItems,
        int maxSockets
    ) {
        public static final Codec<SocketDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Identifier.CODEC).fieldOf("items").forGetter(SocketDataComponent::socketedItems),
                Codec.INT.fieldOf("max").forGetter(SocketDataComponent::maxSockets)
            ).apply(instance, SocketDataComponent::new)
        );
        
        public static SocketDataComponent createDefault(int maxSockets) {
            return new SocketDataComponent(new ArrayList<>(), maxSockets);
        }
    }
}

