package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * System for managing house points.
 */
public final class HousePointsSystem {
    private HousePointsSystem() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HousePointsData>> HOUSE_POINTS_DATA =
        DATA_COMPONENTS.register(
            "house_points_data",
            () -> DataComponentType.<HousePointsData>builder()
                .persistent(HousePointsData.CODEC)
                .build()
        );
    
    /**
     * Data component for storing house points.
     */
    public record HousePointsData(Map<String, Integer> points) {
        public static final Codec<HousePointsData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("points").forGetter(HousePointsData::points)
            ).apply(instance, HousePointsData::new)
        );
        
        public HousePointsData() {
            this(new HashMap<>());
        }
        
        public int getPoints(String house) {
            return points.getOrDefault(house, 0);
        }
        
        public HousePointsData addPoints(String house, int amount) {
            Map<String, Integer> newPoints = new HashMap<>(points);
            newPoints.put(house, newPoints.getOrDefault(house, 0) + amount);
            return new HousePointsData(newPoints);
        }
        
        public HousePointsData removePoints(String house, int amount) {
            Map<String, Integer> newPoints = new HashMap<>(points);
            int current = newPoints.getOrDefault(house, 0);
            newPoints.put(house, Math.max(0, current - amount));
            return new HousePointsData(newPoints);
        }
        
        public String getWinningHouse() {
            return points.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        }
    }
    
    /**
     * Gets house points data for a player.
     */
    public static HousePointsData getHousePoints(Player player) {
        // TODO: Retrieve from player data component
        // - Use player.getData(HOUSE_POINTS_DATA) to get the component
        // - Return default HousePointsData if not present
        return new HousePointsData();
    }
    
    /**
     * Adds points to a house.
     */
    public static void addPoints(Player player, String house, int amount) {
        // TODO: Update player's house points data component
        // - Get current data: HousePointsData current = getHousePoints(player)
        // - Create updated data: HousePointsData updated = current.addPoints(house, amount)
        // - Save: player.setData(HOUSE_POINTS_DATA, updated)
        // - Sync to client if needed
    }
    
    /**
     * Removes points from a house.
     */
    public static void removePoints(Player player, String house, int amount) {
        // TODO: Update player's house points data component
        // - Get current data: HousePointsData current = getHousePoints(player)
        // - Create updated data: HousePointsData updated = current.removePoints(house, amount)
        // - Save: player.setData(HOUSE_POINTS_DATA, updated)
        // - Sync to client if needed
    }
    
    /**
     * Gets points for a specific house.
     */
    public static int getPoints(Player player, String house) {
        HousePointsData data = getHousePoints(player);
        return data.getPoints(house);
    }
}






