package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing Sorting Hat house assignments.
 */
public final class SortingHatData {
    private SortingHatData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SortingHatComponent>> SORTING_HAT_DATA =
        DATA_COMPONENTS.register(
            "sorting_hat_data",
            () -> DataComponentType.<SortingHatComponent>builder()
                .persistent(SortingHatComponent.CODEC)
                .build()
        );
    
    /**
     * Hogwarts house enum.
     */
    public enum House {
        GRYFFINDOR("Gryffindor"),
        HUFFLEPUFF("Hufflepuff"),
        RAVENCLAW("Ravenclaw"),
        SLYTHERIN("Slytherin");
        
        private final String name;
        
        House(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Component storing house assignments.
     */
    public record SortingHatComponent(
        Map<UUID, House> playerHouses
    ) {
        public static final Codec<SortingHatComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.unboundedMap(UUIDUtil.CODEC, Codec.STRING.xmap(
                    s -> House.valueOf(s.toUpperCase()),
                    h -> h.name()
                )).optionalFieldOf("playerHouses", new HashMap<>()).forGetter(SortingHatComponent::playerHouses)
            ).apply(instance, SortingHatComponent::new)
        );
        
        public SortingHatComponent() {
            this(new HashMap<>());
        }
        
        public Optional<House> getHouse(UUID playerId) {
            return Optional.ofNullable(playerHouses.get(playerId));
        }
        
        public SortingHatComponent assignHouse(UUID playerId, House house) {
            Map<UUID, House> newMap = new HashMap<>(playerHouses);
            newMap.put(playerId, house);
            return new SortingHatComponent(newMap);
        }
        
        public boolean isSorted(UUID playerId) {
            return playerHouses.containsKey(playerId);
        }
    }
}
