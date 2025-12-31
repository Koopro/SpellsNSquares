package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.core.data.migration.DataMigrationSystem;
import at.koopro.spells_n_squares.features.playerclass.data.PlayerClassData;
import at.koopro.spells_n_squares.features.spell.SpellSlotData;
import at.koopro.spells_n_squares.features.wand.WandData;
import at.koopro.spells_n_squares.modules.magic.internal.AnimagusData;
import at.koopro.spells_n_squares.modules.magic.internal.PatronusData;
import at.koopro.spells_n_squares.modules.spell.internal.SpellData;
import at.koopro.spells_n_squares.modules.tutorial.internal.TutorialData;
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
        if (player == null) {
            return PlayerDataComponent.PlayerData.empty();
        }
        if (at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isClientSide(player)) {
            // On client, return default (data syncs from server)
            return PlayerDataComponent.PlayerData.empty();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return PlayerDataComponent.PlayerData.empty();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return PlayerDataComponent.PlayerData.empty();
        }
        
        // Migrate data if needed (before parsing)
        CompoundTag dataToParse = tag;
        if (!DataMigrationSystem.migrateIfNeeded(dataToParse, player.getName().getString())) {
            LOGGER.warn("Data migration failed for player {}, attempting to load anyway", player.getName().getString());
        } else if (dataToParse != tag) {
            // Migration modified the data, save it back
            persistentData.put(PERSISTENT_DATA_KEY, dataToParse);
        }
        
        try {
            return PlayerDataComponent.PlayerData.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                dataToParse
            ).result().orElse(PlayerDataComponent.PlayerData.empty());
        } catch (Exception e) {
            LOGGER.warn("Failed to load player data for player {}, using default", 
                player.getName().getString(), e);
            return PlayerDataComponent.PlayerData.empty();
        }
    }
    
    /**
     * Updates player data.
     * Ensures the data version is set to the current version.
     */
    public static void set(Player player, PlayerDataComponent.PlayerData data) {
        if (player == null) {
            return;
        }
        if (at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isClientSide(player)) {
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
                } else {
                    // If it's not a CompoundTag, wrap it or log a warning
                    LOGGER.warn("Player data encoded to non-CompoundTag type: {}", tag.getClass().getName());
                    player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to save player data for player {}", 
                player.getName().getString(), e);
        }
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
                spells, data.classes(), data.wandData(), data.tutorial(), data.animagus(), data.patronus()
            ),
            spellData);
    }
    
    /**
     * Gets spell slot data for a player (backward compatibility).
     * 
     * @deprecated Use {@link #getSpellData(Player)} and access {@link SpellData#slots()} instead.
     * This method is kept for backward compatibility with legacy code during migration.
     * <p>
     * <b>Note:</b> This method is still in use by {@code SpellSlotData} and {@code ServerEventHandler}.
     * Do not remove until all callers have been migrated to use {@link #getSpellData(Player)}.
     * 
     * @param player The player
     * @return The spell slot component
     */
    @Deprecated(forRemoval = true)
    public static SpellSlotData.SpellSlotComponent getSpellSlotData(Player player) {
        return get(player).spells().slots();
    }
    
    /**
     * Updates spell slot data for a player (backward compatibility).
     * 
     * @deprecated Use {@link #setSpellData(Player, SpellData)} with {@link SpellData#withSlots(SpellSlotData.SpellSlotComponent)} instead.
     * This method is kept for backward compatibility with legacy code during migration.
     * <p>
     * <b>Note:</b> This method is still in use by {@code SpellSlotData}.
     * Do not remove until all callers have been migrated to use {@link #setSpellData(Player, SpellData)}.
     * 
     * @param player The player
     * @param spellSlotData The spell slot data to set
     */
    @Deprecated(forRemoval = true)
    public static void setSpellSlotData(Player player, SpellSlotData.SpellSlotComponent spellSlotData) {
        SpellData current = getSpellData(player);
        SpellData updated = current.withSlots(spellSlotData);
        setSpellData(player, updated);
    }
    
    /**
     * Gets player class data for a player.
     */
    public static PlayerClassData.PlayerClassComponent getPlayerClassData(Player player) {
        return get(player).classes();
    }
    
    /**
     * Updates player class data for a player.
     */
    public static void setPlayerClassData(Player player, PlayerClassData.PlayerClassComponent classData) {
        update(player, 
            PlayerDataComponent.PlayerData::classes,
            (data, classes) -> new PlayerDataComponent.PlayerData(
                data.spells(), classes, data.wandData(), data.tutorial(), data.animagus(), data.patronus()
            ),
            classData);
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
                data.spells(), data.classes(), wand, data.tutorial(), data.animagus(), data.patronus()
            ),
            wandData);
    }
    
    /**
     * Gets tutorial data for a player.
     */
    public static TutorialData getTutorialData(Player player) {
        return get(player).tutorial();
    }
    
    /**
     * Updates tutorial data for a player.
     */
    public static void setTutorialData(Player player, TutorialData tutorialData) {
        update(player, 
            PlayerDataComponent.PlayerData::tutorial,
            (data, tutorial) -> new PlayerDataComponent.PlayerData(
                data.spells(), data.classes(), data.wandData(), tutorial, data.animagus(), data.patronus()
            ),
            tutorialData);
    }
    
    /**
     * Gets Animagus data for a player.
     */
    public static AnimagusData getAnimagusData(Player player) {
        return get(player).animagus();
    }
    
    /**
     * Updates Animagus data for a player.
     */
    public static void setAnimagusData(Player player, AnimagusData animagusData) {
        update(player, 
            PlayerDataComponent.PlayerData::animagus,
            (data, animagus) -> new PlayerDataComponent.PlayerData(
                data.spells(), data.classes(), data.wandData(), data.tutorial(), animagus, data.patronus()
            ),
            animagusData);
    }
    
    /**
     * Gets Patronus data for a player.
     */
    public static PatronusData getPatronusData(Player player) {
        return get(player).patronus();
    }
    
    /**
     * Updates Patronus data for a player.
     */
    public static void setPatronusData(Player player, PatronusData patronusData) {
        update(player, 
            PlayerDataComponent.PlayerData::patronus,
            (data, patronus) -> new PlayerDataComponent.PlayerData(
                data.spells(), data.classes(), data.wandData(), data.tutorial(), data.animagus(), patronus
            ),
            patronusData);
    }
}

