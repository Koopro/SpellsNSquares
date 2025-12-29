package at.koopro.spells_n_squares.features.education;

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
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:house_points";
    
    /**
     * Gets house points data for a player from their persistent data component.
     */
    public static HousePointsData getHousePoints(Player player) {
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return new HousePointsData();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new HousePointsData();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new HousePointsData();
        }
        
        try {
            return HousePointsData.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new HousePointsData());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load house points data for player {}, using default", player.getName().getString(), e);
            return new HousePointsData();
        }
    }
    
    /**
     * Sets house points data for a player in their persistent data component.
     */
    private static void setHousePoints(Player player, HousePointsData data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = HousePointsData.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save house points data for player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Adds points to a house.
     */
    public static void addPoints(Player player, String house, int amount) {
        if (!player.level().isClientSide()) {
            HousePointsData current = getHousePoints(player);
            HousePointsData updated = current.addPoints(house, amount);
            setHousePoints(player, updated);
        }
    }
    
    /**
     * Removes points from a house.
     */
    public static void removePoints(Player player, String house, int amount) {
        if (!player.level().isClientSide()) {
            HousePointsData current = getHousePoints(player);
            HousePointsData updated = current.removePoints(house, amount);
            setHousePoints(player, updated);
        }
    }
    
    /**
     * Gets points for a specific house.
     */
    public static int getPoints(Player player, String house) {
        HousePointsData data = getHousePoints(player);
        return data.getPoints(house);
    }
}
















