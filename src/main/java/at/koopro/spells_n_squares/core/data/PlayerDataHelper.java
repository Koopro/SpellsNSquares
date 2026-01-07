package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.core.data.migration.DataMigrationSystem;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

/**
 * Helper for accessing player data.
 * Provides type-safe access to player data stored in persistent data (NBT).
 * 
 * Note: Currently uses persistent data storage. In the future, this may migrate
 * to entity data components when they are fully supported.
 */
public final class PlayerDataHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:player_data";
    
    /**
     * Gets the complete player data.
     * Automatically migrates data if it's from an older version.
     */
    public static PlayerDataComponent.PlayerData get(Player player) {
        DevLogger.logDataOperation(PlayerDataHelper.class, "get", "LOAD", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        DevLogger.logMethodEntry(PlayerDataHelper.class, "get", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        
        if (player == null) {
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", "empty (null player)");
            return PlayerDataComponent.PlayerData.empty();
        }
        if (at.koopro.spells_n_squares.core.util.player.PlayerValidationUtils.isClientSide(player)) {
            // On client, return default (data syncs from server)
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", "empty (client-side)");
            return PlayerDataComponent.PlayerData.empty();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", "empty (no data)");
            return PlayerDataComponent.PlayerData.empty();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", "empty (empty tag)");
            return PlayerDataComponent.PlayerData.empty();
        }
        
        // Migrate data if needed (before parsing)
        CompoundTag dataToParse = tag;
        if (!DataMigrationSystem.migrateIfNeeded(dataToParse, player.getName().getString())) {
            LOGGER.warn("Data migration failed for player {}, attempting to load anyway", player.getName().getString());
            DevLogger.logWarn(PlayerDataHelper.class, "get", "Data migration failed");
        } else if (dataToParse != tag) {
            // Migration modified the data, save it back
            persistentData.put(PERSISTENT_DATA_KEY, dataToParse);
            DevLogger.logStateChange(PlayerDataHelper.class, "get", "Data migrated and saved");
        }
        
        try {
            PlayerDataComponent.PlayerData result = PlayerDataComponent.PlayerData.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                dataToParse
            ).result().orElse(PlayerDataComponent.PlayerData.empty());
            
            // Validate loaded data
            if (result != null) {
                // Basic validation: ensure result is not corrupted
                // Additional validation can be added here if needed
                // For now, the Codec parsing already validates the structure
            }
            
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", result != null ? "data" : "empty");
            return result;
        } catch (Exception e) {
            LOGGER.warn("Failed to load player data for player {}, using default", 
                player.getName().getString(), e);
            DevLogger.logError(PlayerDataHelper.class, "get", "Failed to load player data", e);
            DevLogger.logMethodExit(PlayerDataHelper.class, "get", "empty (error)");
            return PlayerDataComponent.PlayerData.empty();
        }
    }
    
    /**
     * Updates player data.
     * Ensures the data version is set to the current version.
     */
    public static void set(Player player, PlayerDataComponent.PlayerData data) {
        DevLogger.logDataOperation(PlayerDataHelper.class, "set", "SAVE", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        DevLogger.logMethodEntry(PlayerDataHelper.class, "set", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        
        if (player == null) {
            DevLogger.logMethodExit(PlayerDataHelper.class, "set");
            return;
        }
        if (at.koopro.spells_n_squares.core.util.player.PlayerValidationUtils.isClientSide(player)) {
            DevLogger.logMethodExit(PlayerDataHelper.class, "set");
            return; // Only set on server
        }
        
        try {
            var result = PlayerDataComponent.PlayerData.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                // Ensure version is set to current version
                if (tag instanceof CompoundTag compoundTag) {
                    DataMigrationSystem.setDataVersion(compoundTag, DataMigrationSystem.getCurrentDataVersion());
                    player.getPersistentData().put(PERSISTENT_DATA_KEY, compoundTag);
                    DevLogger.logStateChange(PlayerDataHelper.class, "set", "Player data saved");
                } else {
                    // If it's not a CompoundTag, wrap it or log a warning
                    LOGGER.warn("Player data encoded to non-CompoundTag type: {}", tag.getClass().getName());
                    DevLogger.logWarn(PlayerDataHelper.class, "set", 
                        "Non-CompoundTag type: " + tag.getClass().getName());
                    player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to save player data for player {}", 
                player.getName().getString(), e);
            DevLogger.logError(PlayerDataHelper.class, "set", "Failed to save player data", e);
        }
        
        DevLogger.logMethodExit(PlayerDataHelper.class, "set");
    }
    
    /**
     * Updates a specific part of player data.
     */
    public static <T> void update(Player player, 
                                  java.util.function.Function<PlayerDataComponent.PlayerData, T> getter,
                                  java.util.function.BiFunction<PlayerDataComponent.PlayerData, T, PlayerDataComponent.PlayerData> setter,
                                  T newValue) {
        PlayerDataComponent.PlayerData current = get(player);
        PlayerDataComponent.PlayerData updated = setter.apply(current, newValue);
        set(player, updated);
    }
    
    /**
     * Gets spell data for a player.
     */
    public static SpellData getSpellData(Player player) {
        return get(player).spells();
    }
    
    /**
     * Updates spell data for a player.
     */
    public static void setSpellData(Player player, SpellData spellData) {
        update(player, 
            PlayerDataComponent.PlayerData::spells,
            (data, spells) -> new PlayerDataComponent.PlayerData(
                spells, data.wandData(), data.identity()
            ),
            spellData);
    }
    
    /**
     * Gets wand data for a player.
     */
    public static WandData.WandDataComponent getWandData(Player player) {
        return get(player).wandData();
    }
    
    /**
     * Updates wand data for a player.
     */
    public static void setWandData(Player player, WandData.WandDataComponent wandData) {
        update(player, 
            PlayerDataComponent.PlayerData::wandData,
            (data, wand) -> new PlayerDataComponent.PlayerData(
                data.spells(), wand, data.identity()
            ),
            wandData);
    }
    
    /**
     * Gets identity data for a player.
     */
    public static PlayerIdentityData.IdentityData getIdentityData(Player player) {
        return get(player).identity();
    }
    
    /**
     * Updates identity data for a player.
     */
    public static void setIdentityData(Player player, PlayerIdentityData.IdentityData identity) {
        update(player, 
            PlayerDataComponent.PlayerData::identity,
            (data, ident) -> new PlayerDataComponent.PlayerData(
                data.spells(), data.wandData(), ident
            ),
            identity);
    }
    
    /**
     * Gets blood status for a player.
     */
    public static PlayerIdentityData.BloodStatus getBloodStatus(Player player) {
        return getIdentityData(player).bloodStatus();
    }
    
    /**
     * Gets magical type for a player.
     */
    public static PlayerIdentityData.MagicalType getMagicalType(Player player) {
        return getIdentityData(player).magicalType();
    }
    
    /**
     * Sets blood status for a player.
     */
    public static void setBloodStatus(Player player, PlayerIdentityData.BloodStatus bloodStatus) {
        PlayerIdentityData.IdentityData current = getIdentityData(player);
        PlayerIdentityData.IdentityData updated = current.withBloodStatus(bloodStatus);
        setIdentityData(player, updated);
    }
    
    /**
     * Sets magical type for a player.
     */
    public static void setMagicalType(Player player, PlayerIdentityData.MagicalType magicalType) {
        PlayerIdentityData.IdentityData current = getIdentityData(player);
        PlayerIdentityData.IdentityData updated = current.withMagicalType(magicalType);
        setIdentityData(player, updated);
    }
}

