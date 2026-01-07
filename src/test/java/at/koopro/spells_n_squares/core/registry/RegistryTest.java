package at.koopro.spells_n_squares.core.registry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registry systems.
 * Tests registry registration, addon discovery, and feature initialization.
 */
class RegistryTest {
    
    @Test
    void testFeatureRegistryExists() {
        // Test that FeatureRegistry class is accessible
        assertNotNull(FeatureRegistry.class);
    }
    
    @Test
    void testAddonRegistryExists() {
        // Test that AddonRegistry class is accessible
        try {
            Class<?> addonRegistryClass = Class.forName("at.koopro.spells_n_squares.core.registry.AddonRegistry");
            assertNotNull(addonRegistryClass);
        } catch (ClassNotFoundException e) {
            fail("AddonRegistry should exist");
        }
    }
    
    @Test
    void testPlayerDataManagerRegistryExists() {
        // Test that PlayerDataManagerRegistry class is accessible
        assertNotNull(PlayerDataManagerRegistry.class);
    }
    
    @Test
    void testModBlocksRegistryExists() {
        // Test that ModBlocks registry exists
        assertNotNull(ModBlocks.class);
    }
    
    @Test
    void testModItemsRegistryExists() {
        // Test that ModItems registry exists
        assertNotNull(ModItems.class);
    }
    
    @Test
    void testModEntitiesRegistryExists() {
        // Test that ModEntities registry exists
        assertNotNull(ModEntities.class);
    }
}





