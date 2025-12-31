# Data Migration Guide

This guide explains how to create and manage data migrations for player data in the Spells 'n Squares mod.

## Overview

Data migrations allow you to safely update player data structures when the mod is updated. The migration system automatically applies migrations when players load their data, ensuring backward compatibility.

## Migration System Architecture

```
Player Data Load
    ↓
Check Data Version
    ↓
Apply Migrations Sequentially (if needed)
    ↓
Update Data Version
    ↓
Load Data
```

## Creating a Migration

### Step 1: Implement DataMigration Interface

Create a new class implementing `DataMigration`:

```java
package at.koopro.spells_n_squares.core.data.migration;

import net.minecraft.nbt.CompoundTag;

public class MigrationV1ToV2 implements DataMigration {
    @Override
    public int getSourceVersion() {
        return 1;
    }
    
    @Override
    public int getTargetVersion() {
        return 2;
    }
    
    @Override
    public String getDescription() {
        return "Adds new field 'house' to player data";
    }
    
    @Override
    public boolean migrate(CompoundTag data) {
        if (data == null) {
            return false;
        }
        
        try {
            // Add new field with default value
            if (!data.contains("house")) {
                data.putString("house", "None");
            }
            
            // Rename old field if needed
            if (data.contains("oldFieldName")) {
                String value = data.getString("oldFieldName");
                data.remove("oldFieldName");
                data.putString("newFieldName", value);
            }
            
            // Update nested structures
            var spellsTagOpt = data.getCompound("spells");
            if (spellsTagOpt.isPresent()) {
                CompoundTag spellsTag = spellsTagOpt.get();
                // Migrate nested data
            }
            
            return true;
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error(
                "Error during migration v1 to v2: {}", e.getMessage(), e);
            return false;
        }
    }
}
```

### Step 2: Register the Migration

Register your migration during mod initialization:

```java
// In ModInitialization.java or similar
DataMigrationSystem.registerMigration(new MigrationV1ToV2());
```

Or in `DataMigrationSystem.initializeDefaultMigrations()`:

```java
public static void initializeDefaultMigrations() {
    registerMigration(new MigrationV0ToV1());
    registerMigration(new MigrationV1ToV2());  // Add your migration here
}
```

### Step 3: Update Current Version

Increment the current data version in `DataMigrationSystem.java`:

```java
private static final int CURRENT_DATA_VERSION = 2; // Increment from 1 to 2
```

## Migration Patterns

### Pattern 1: Adding New Fields

```java
if (!data.contains("newField")) {
    data.putString("newField", "defaultValue");
}
```

### Pattern 2: Renaming Fields

```java
if (data.contains("oldField")) {
    String value = data.getString("oldField");
    data.remove("oldField");
    data.putString("newField", value);
}
```

### Pattern 3: Type Conversion

```java
if (data.contains("intField")) {
    int oldValue = data.getInt("intField").orElse(0);
    data.remove("intField");
    data.putFloat("floatField", (float) oldValue);
}
```

### Pattern 4: Structure Changes

```java
// Move data from root to nested structure
if (data.contains("rootField")) {
    var nestedTagOpt = data.getCompound("nested");
    CompoundTag nestedTag = nestedTagOpt.orElse(new CompoundTag());
    nestedTag.put("field", data.get("rootField"));
    data.put("nested", nestedTag);
    data.remove("rootField");
}
```

### Pattern 5: Removing Deprecated Fields

```java
// Remove fields that are no longer used
if (data.contains("deprecatedField")) {
    data.remove("deprecatedField");
}
```

## Versioning Strategy

### Version Numbering

- Start at version 0 for unversioned/legacy data
- Increment by 1 for each migration
- Never skip versions (must be sequential: 0→1→2→3, not 0→3)

### When to Create a Migration

Create a migration when:
- Adding new required fields
- Renaming fields
- Changing data structure
- Removing fields (optional, but recommended for cleanup)
- Changing data types

Do NOT create a migration for:
- Adding optional fields (can be handled with defaults)
- Bug fixes that don't change structure
- Performance optimizations

## Testing Migrations

### Manual Testing

1. Create test data with old version
2. Load data and verify migration applies
3. Verify migrated data is correct
4. Test rollback if implemented

### Using Migration Validator

```java
// Validate migration before applying
List<String> errors = MigrationValidator.validateMigration(migration, testData);
if (!errors.isEmpty()) {
    // Handle validation errors
}

// Validate migration chain
List<Integer> missing = MigrationValidator.validateMigrationChain(0, 2);
if (!missing.isEmpty()) {
    // Missing migrations for versions: missing
}
```

## Rollback Support

The migration system includes rollback support for safety:

```java
// Create backup before migration
CompoundTag backup = DataMigrationSystem.createBackup(data);

// Apply migration
if (!DataMigrationSystem.migrateIfNeeded(data, playerName)) {
    // Migration failed - rollback
    DataMigrationSystem.rollback(data, backup);
}
```

## Troubleshooting

### Migration Not Applied

- Check that migration is registered
- Verify version numbers are sequential
- Check that `CURRENT_DATA_VERSION` is updated
- Ensure migration is called during initialization

### Migration Fails

- Check logs for specific error messages
- Verify data structure matches expectations
- Test with sample data
- Use rollback to restore previous state

### Data Corruption

- Always create backups before migration
- Validate data after migration
- Test migrations thoroughly before release
- Use migration validator to catch issues early

## Best Practices

1. **Always test migrations** with real player data before release
2. **Create backups** before applying migrations
3. **Log migration actions** for debugging
4. **Keep migrations simple** - one logical change per migration
5. **Document what changed** in the migration description
6. **Validate migrated data** after migration completes
7. **Never modify migrations** after release - create new ones instead

## Example: Complete Migration

```java
public class MigrationV1ToV2 implements DataMigration {
    @Override
    public int getSourceVersion() {
        return 1;
    }
    
    @Override
    public int getTargetVersion() {
        return 2;
    }
    
    @Override
    public String getDescription() {
        return "Migrates spell slots from array to map format";
    }
    
    @Override
    public boolean migrate(CompoundTag data) {
        if (data == null) {
            return false;
        }
        
        try {
            // Get old spell slots array
            var spellsTagOpt = data.getCompound("spells");
            if (spellsTagOpt.isEmpty()) {
                return true; // No spells data, nothing to migrate
            }
            
            CompoundTag spellsTag = spellsTagOpt.get();
            var oldSlotsOpt = spellsTag.getCompound("slots");
            
            if (oldSlotsOpt.isPresent()) {
                CompoundTag oldSlots = oldSlotsOpt.get();
                
                // Create new map format
                CompoundTag newSlots = new CompoundTag();
                for (String key : oldSlots.keySet()) {
                    var spellIdOpt = oldSlots.getString(key);
                    if (spellIdOpt.isPresent()) {
                        newSlots.putString(key, spellIdOpt.get());
                    }
                }
                
                // Replace old format with new
                spellsTag.put("slots", newSlots);
                data.put("spells", spellsTag);
            }
            
            return true;
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error(
                "Error during migration v1 to v2: {}", e.getMessage(), e);
            return false;
        }
    }
}
```

## Version Registry

For systems with independent versioning, use `MigrationVersionRegistry`:

```java
// Register system version
MigrationVersionRegistry.registerSystemVersion("currency", 2);
MigrationVersionRegistry.registerSystemVersion("combat_stats", 1);

// Get system version
int currencyVersion = MigrationVersionRegistry.getSystemVersion("currency");
```

This allows different data systems to have independent migration paths.

