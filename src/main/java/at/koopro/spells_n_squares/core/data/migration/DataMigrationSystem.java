package at.koopro.spells_n_squares.core.data.migration;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages data migrations for player data.
 * Automatically applies migrations when data is loaded from an older version.
 */
public final class DataMigrationSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String VERSION_KEY = "dataVersion";
    private static final int CURRENT_DATA_VERSION = 1; // Increment when adding new migrations

    private static final List<DataMigration> migrations = new ArrayList<>();
    private static final Map<Integer, DataMigration> migrationMap = new HashMap<>();

    private DataMigrationSystem() {
        // Utility class
    }

    /**
     * Registers a data migration.
     * Migrations should be registered during mod initialization.
     *
     * @param migration The migration to register
     */
    public static void registerMigration(DataMigration migration) {
        if (migration == null) {
            LOGGER.warn("Attempted to register null migration");
            return;
        }

        int source = migration.getSourceVersion();
        int target = migration.getTargetVersion();

        if (target != source + 1) {
            LOGGER.error("Invalid migration: source version {} to target version {} (must be sequential)", source, target);
            return;
        }

        if (migrationMap.containsKey(source)) {
            LOGGER.warn("Migration from version {} already exists, overwriting", source);
        }

        migrations.add(migration);
        migrationMap.put(source, migration);
        LOGGER.debug("Registered migration: {} -> {} ({})", source, target, migration.getDescription());
    }

    /**
     * Gets the current data version.
     *
     * @return The current data version
     */
    public static int getCurrentDataVersion() {
        return CURRENT_DATA_VERSION;
    }

    /**
     * Gets the version of the provided data, or 0 if no version is set.
     *
     * @param data The NBT data
     * @return The data version, or 0 if not set
     */
    public static int getDataVersion(CompoundTag data) {
        if (data == null || !data.contains(VERSION_KEY)) {
            return 0; // Legacy data or new data
        }
        return data.getInt(VERSION_KEY).orElse(0);
    }

    /**
     * Sets the version of the provided data.
     *
     * @param data The NBT data
     * @param version The version to set
     */
    public static void setDataVersion(CompoundTag data, int version) {
        if (data != null) {
            data.putInt(VERSION_KEY, version);
        }
    }

    /**
     * Migrates player data to the current version if needed.
     * This method will apply all necessary migrations in sequence.
     *
     * @param data The NBT data to migrate
     * @param playerName The name of the player (for logging)
     * @return True if migration was successful, false otherwise
     */
    public static boolean migrateIfNeeded(CompoundTag data, String playerName) {
        if (data == null) {
            return false;
        }

        int currentVersion = getDataVersion(data);
        int targetVersion = getCurrentDataVersion();

        if (currentVersion == targetVersion) {
            // Already at current version, no migration needed
            return true;
        }

        if (currentVersion > targetVersion) {
            LOGGER.warn("Player {} has data version {} which is newer than current version {}. " +
                    "This may indicate a mod downgrade. Proceeding with caution.", playerName, currentVersion, targetVersion);
            // Still allow loading, but don't migrate forward
            return true;
        }

        LOGGER.info("Migrating player data for {} from version {} to version {}", playerName, currentVersion, targetVersion);

        // Apply migrations sequentially
        int version = currentVersion;
        while (version < targetVersion) {
            DataMigration migration = migrationMap.get(version);
            if (migration == null) {
                LOGGER.error("No migration found from version {} to version {} for player {}. " +
                        "Data may be corrupted or incomplete.", version, version + 1, playerName);
                return false;
            }

            LOGGER.debug("Applying migration {} -> {}: {}", version, version + 1, migration.getDescription());

            try {
                if (!migration.migrate(data)) {
                    LOGGER.error("Migration from version {} to {} failed for player {}", version, version + 1, playerName);
                    return false;
                }
                version++;
                setDataVersion(data, version);
            } catch (Exception e) {
                LOGGER.error("Exception during migration from version {} to {} for player {}: {}",
                        version, version + 1, playerName, e.getMessage(), e);
                return false;
            }
        }

        LOGGER.info("Successfully migrated player data for {} to version {}", playerName, targetVersion);
        return true;
    }

    /**
     * Initializes default migrations.
     * This should be called during mod initialization.
     */
    public static void initializeDefaultMigrations() {
        // Register migrations here
        // Example: registerMigration(new MigrationV0ToV1());
        // Example: registerMigration(new MigrationV1ToV2());

        // For now, we'll register a migration from version 0 (legacy) to version 1
        registerMigration(new MigrationV0ToV1());
    }

    /**
     * Gets all registered migrations.
     *
     * @return An unmodifiable list of all migrations
     */
    public static List<DataMigration> getAllMigrations() {
        return Collections.unmodifiableList(migrations);
    }

    /**
     * Gets the migration for a specific source version.
     *
     * @param version The source version
     * @return The migration for that version, or null if not found
     */
    public static DataMigration getMigrationForVersion(int version) {
        return migrationMap.get(version);
    }
}

