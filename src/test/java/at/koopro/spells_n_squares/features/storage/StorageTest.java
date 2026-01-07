package at.koopro.spells_n_squares.features.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for storage system (pocket dimensions, Newt's Case).
 */
class StorageTest {
    
    @Test
    void testPocketDimensionManagerExists() {
        // Test that PocketDimensionManager class is accessible
        assertNotNull(PocketDimensionManager.class);
    }
    
    @Test
    void testStorageRegistryExists() {
        // Test that StorageRegistry class is accessible
        try {
            Class<?> registryClass = Class.forName("at.koopro.spells_n_squares.features.storage.StorageRegistry");
            assertNotNull(registryClass);
        } catch (ClassNotFoundException e) {
            fail("StorageRegistry should exist");
        }
    }
    
    @Test
    void testPocketDimensionDataExists() {
        // Test that PocketDimensionData class is accessible
        try {
            Class<?> dataClass = Class.forName("at.koopro.spells_n_squares.features.storage.PocketDimensionData");
            assertNotNull(dataClass);
        } catch (ClassNotFoundException e) {
            fail("PocketDimensionData should exist");
        }
    }
}





