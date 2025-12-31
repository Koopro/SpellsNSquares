package at.koopro.spells_n_squares.features.artifacts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data component for storing Marauder's Map player tracking information.
 */
public final class MaraudersMapData {
    private MaraudersMapData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MaraudersMapComponent>> MARAUDERS_MAP_DATA =
        DATA_COMPONENTS.register(
            "marauders_map_data",
            () -> DataComponentType.<MaraudersMapComponent>builder()
                .persistent(MaraudersMapComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing tracked player information.
     */
    public record PlayerLocation(
        UUID playerId,
        String playerName,
        double x, double y, double z,
        long lastUpdateTick
    ) {
        public static final Codec<PlayerLocation> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("playerId").forGetter(PlayerLocation::playerId),
                Codec.STRING.fieldOf("playerName").forGetter(PlayerLocation::playerName),
                Codec.DOUBLE.fieldOf("x").forGetter(PlayerLocation::x),
                Codec.DOUBLE.fieldOf("y").forGetter(PlayerLocation::y),
                Codec.DOUBLE.fieldOf("z").forGetter(PlayerLocation::z),
                Codec.LONG.fieldOf("lastUpdate").forGetter(PlayerLocation::lastUpdateTick)
            ).apply(instance, PlayerLocation::new)
        );
    }
    
    /**
     * Component storing Marauder's Map state.
     */
    public record MaraudersMapComponent(
        List<PlayerLocation> trackedPlayers,
        boolean isActive
    ) {
        public static final Codec<MaraudersMapComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(PlayerLocation.CODEC).fieldOf("trackedPlayers").forGetter(MaraudersMapComponent::trackedPlayers),
                Codec.BOOL.fieldOf("isActive").forGetter(MaraudersMapComponent::isActive)
            ).apply(instance, MaraudersMapComponent::new)
        );
        
        public MaraudersMapComponent() {
            this(new ArrayList<>(), false);
        }
        
        public MaraudersMapComponent toggle() {
            return new MaraudersMapComponent(trackedPlayers, !isActive);
        }
        
        public MaraudersMapComponent updatePlayer(UUID playerId, String playerName, double x, double y, double z, long tick) {
            List<PlayerLocation> updated = new ArrayList<>(trackedPlayers);
            updated.removeIf(loc -> loc.playerId().equals(playerId));
            updated.add(new PlayerLocation(playerId, playerName, x, y, z, tick));
            return new MaraudersMapComponent(updated, isActive);
        }
    }
}
