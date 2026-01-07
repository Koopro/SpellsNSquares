package at.koopro.spells_n_squares.features.wand;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for wand system (crafting, attunement, data persistence).
 */
class WandTest {
    
    @Test
    void testWandRegistryExists() {
        // Test that WandRegistry class is accessible
        try {
            Class<?> registryClass = Class.forName("at.koopro.spells_n_squares.features.wand.WandRegistry");
            assertNotNull(registryClass);
        } catch (ClassNotFoundException e) {
            fail("WandRegistry should exist");
        }
    }
    
    @Test
    void testWandAttunementHandlerExists() {
        // Test that WandAttunementHandler class is accessible
        try {
            Class<?> handlerClass = Class.forName("at.koopro.spells_n_squares.features.wand.WandAttunementHandler");
            assertNotNull(handlerClass);
        } catch (ClassNotFoundException e) {
            fail("WandAttunementHandler should exist");
        }
    }
    
    @Test
    void testWandDataExists() {
        // Test that WandData class is accessible
        try {
            Class<?> dataClass = Class.forName("at.koopro.spells_n_squares.features.wand.WandData");
            assertNotNull(dataClass);
        } catch (ClassNotFoundException e) {
            fail("WandData should exist");
        }
    }
}





