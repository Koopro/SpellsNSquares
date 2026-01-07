package at.koopro.spells_n_squares.core.data.migration;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * Migration from version 0 (legacy/unversioned) to version 1.
 * This migration handles:
 * - Adding version field to data
 * - Migrating old spell slot format if present
 * - Ensuring all new data fields have defaults
 */
public class MigrationV0ToV1 implements DataMigration {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public int getSourceVersion() {
        return 0;
    }

    @Override
    public int getTargetVersion() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Initial version migration: adds version field and ensures data structure compatibility";
    }

    @Override
    public boolean migrate(CompoundTag data) {
        if (data == null) {
            return false;
        }

        try {
            // Set version to 1
            DataMigrationSystem.setDataVersion(data, 1);

            // If data is empty or only contains version, it's a new player - no migration needed
            if (data.size() <= 1) {
                return true;
            }

            // Check for old spell slot format and migrate if present
            // Old format: direct spell slot data in root
            // New format: nested under "spells" -> "slots"
            var oldSlotsOpt = data.getCompound("spellSlots");
            if (oldSlotsOpt.isPresent()) {
                CompoundTag oldSlots = oldSlotsOpt.get();
                if (!oldSlots.isEmpty()) {
                    // Migrate to new structure
                    var spellsTagOpt = data.getCompound("spells");
                    CompoundTag spellsTag;
                    if (spellsTagOpt.isEmpty()) {
                        spellsTag = new CompoundTag();
                        data.put("spells", spellsTag);
                    } else {
                        spellsTag = spellsTagOpt.get();
                    }

                    // Copy old slots to new location
                    spellsTag.put("slots", oldSlots.copy());
                    LOGGER.debug("Migrated spell slots from old format to new format");

                    // Optionally remove old format (or keep for backward compatibility during transition)
                    // data.remove("spellSlots");
                }
            }

            // Ensure all required top-level fields exist with defaults
            // This ensures the Codec can parse the data correctly
            if (!data.contains("spells")) {
                data.put("spells", new CompoundTag());
            }
            if (!data.contains("classes")) {
                data.put("classes", new CompoundTag());
            }
            if (!data.contains("wandData")) {
                data.put("wandData", new CompoundTag());
            }
            if (!data.contains("tutorial")) {
                data.put("tutorial", new CompoundTag());
            }
            if (!data.contains("animagus")) {
                data.put("animagus", new CompoundTag());
            }
            if (!data.contains("patronus")) {
                data.put("patronus", new CompoundTag());
            }
            
            // Add identity data with defaults if not present
            if (!data.contains("identity")) {
                CompoundTag identityTag = new CompoundTag();
                identityTag.putString("bloodStatus", "HALF_BLOOD");
                identityTag.putString("magicalType", "WIZARD");
                data.put("identity", identityTag);
                LOGGER.debug("Added default identity data to player data");
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("Error during migration v0 to v1: {}", e.getMessage(), e);
            return false;
        }
    }
}

