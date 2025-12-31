package at.koopro.spells_n_squares.core.data.migration;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MigrationValidatorTest {
    
    @Test
    void validateMigration_DetectsNullMigration() {
        CompoundTag data = new CompoundTag();
        var errors = MigrationValidator.validateMigration(null, data);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("null"));
    }
    
    @Test
    void validateMigration_DetectsNullData() {
        DataMigration migration = new TestMigration(0, 1);
        var errors = MigrationValidator.validateMigration(migration, null);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("null"));
    }
    
    @Test
    void validateMigration_DetectsVersionMismatch() {
        CompoundTag data = new CompoundTag();
        DataMigrationSystem.setDataVersion(data, 2);
        DataMigration migration = new TestMigration(1, 2);
        
        var errors = MigrationValidator.validateMigration(migration, data);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void validateMigration_DetectsNonSequentialMigration() {
        CompoundTag data = new CompoundTag();
        DataMigration migration = new TestMigration(0, 5); // Non-sequential
        
        var errors = MigrationValidator.validateMigration(migration, data);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("sequential"));
    }
    
    @Test
    void validateMigratedVersion_ValidatesCorrectly() {
        CompoundTag data = new CompoundTag();
        DataMigrationSystem.setDataVersion(data, 3);
        
        assertTrue(MigrationValidator.validateMigratedVersion(data, 3));
        assertFalse(MigrationValidator.validateMigratedVersion(data, 2));
        assertFalse(MigrationValidator.validateMigratedVersion(null, 3));
    }
    
    // Helper class for testing
    private static class TestMigration implements DataMigration {
        private final int source;
        private final int target;
        
        TestMigration(int source, int target) {
            this.source = source;
            this.target = target;
        }
        
        @Override
        public int getSourceVersion() {
            return source;
        }
        
        @Override
        public int getTargetVersion() {
            return target;
        }
        
        @Override
        public String getDescription() {
            return "Test migration";
        }
        
        @Override
        public boolean migrate(CompoundTag data) {
            DataMigrationSystem.setDataVersion(data, target);
            return true;
        }
    }
}

