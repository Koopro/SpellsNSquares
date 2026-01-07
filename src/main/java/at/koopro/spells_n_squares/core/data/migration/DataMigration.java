package at.koopro.spells_n_squares.core.data.migration;

import net.minecraft.nbt.CompoundTag;

/**
 * Interface for data migrations.
 * Each migration handles converting data from one version to the next.
 */
public interface DataMigration {
    /**
     * Gets the source version this migration converts from.
     *
     * @return The source version (e.g., 1)
     */
    int getSourceVersion();

    /**
     * Gets the target version this migration converts to.
     *
     * @return The target version (e.g., 2)
     */
    int getTargetVersion();

    /**
     * Gets a description of what this migration does.
     *
     * @return A human-readable description
     */
    String getDescription();

    /**
     * Migrates player data from the source version to the target version.
     *
     * @param data The NBT data to migrate (will be modified in-place)
     * @return True if migration was successful, false otherwise
     */
    boolean migrate(CompoundTag data);
}









