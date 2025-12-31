package at.koopro.spells_n_squares.core.data.migration;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating data migrations.
 * Provides methods to verify migration correctness and detect potential issues.
 */
public final class MigrationValidator {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private MigrationValidator() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates that a migration can be applied to the given data.
     * 
     * @param migration The migration to validate
     * @param data The data to validate against
     * @return A list of validation errors (empty if valid)
     */
    public static List<String> validateMigration(DataMigration migration, CompoundTag data) {
        List<String> errors = new ArrayList<>();
        
        if (migration == null) {
            errors.add("Migration is null");
            return errors;
        }
        
        if (data == null) {
            errors.add("Data is null");
            return errors;
        }
        
        // Check version consistency
        int dataVersion = DataMigrationSystem.getDataVersion(data);
        if (dataVersion != migration.getSourceVersion()) {
            errors.add(String.format("Data version %d does not match migration source version %d", 
                dataVersion, migration.getSourceVersion()));
        }
        
        // Check that migration is sequential
        if (migration.getTargetVersion() != migration.getSourceVersion() + 1) {
            errors.add(String.format("Migration is not sequential: %d -> %d", 
                migration.getSourceVersion(), migration.getTargetVersion()));
        }
        
        return errors;
    }
    
    /**
     * Validates that migrated data has the expected version.
     * 
     * @param data The data to validate
     * @param expectedVersion The expected version after migration
     * @return true if version matches, false otherwise
     */
    public static boolean validateMigratedVersion(CompoundTag data, int expectedVersion) {
        if (data == null) {
            return false;
        }
        
        int actualVersion = DataMigrationSystem.getDataVersion(data);
        if (actualVersion != expectedVersion) {
            LOGGER.warn("Migrated data version {} does not match expected version {}", 
                actualVersion, expectedVersion);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates that all required migrations are registered for a version range.
     * 
     * @param fromVersion The starting version
     * @param toVersion The target version
     * @return A list of missing migration versions (empty if all present)
     */
    public static List<Integer> validateMigrationChain(int fromVersion, int toVersion) {
        List<Integer> missing = new ArrayList<>();
        
        if (fromVersion >= toVersion) {
            return missing; // No migrations needed
        }
        
        for (int version = fromVersion; version < toVersion; version++) {
            DataMigration migration = DataMigrationSystem.getMigrationForVersion(version);
            if (migration == null) {
                missing.add(version);
            }
        }
        
        return missing;
    }
}

