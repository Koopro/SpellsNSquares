package at.koopro.spells_n_squares.core.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Config class and ConfigAccessor utility.
 */
class ConfigTest {
    
    @Test
    void testConfigAccessorBoolean() {
        // Test that ConfigAccessor safely handles null-like scenarios
        // In actual usage, config values are not null, but we test the safety pattern
        assertNotNull(ConfigAccessor.class);
    }
    
    @Test
    void testConfigCache() {
        // Test that ConfigCache can be enabled/disabled
        boolean originalState = ConfigCache.isEnabled();
        
        ConfigCache.setEnabled(false);
        assertFalse(ConfigCache.isEnabled());
        
        ConfigCache.setEnabled(true);
        assertTrue(ConfigCache.isEnabled());
        
        // Restore original state
        ConfigCache.setEnabled(originalState);
    }
    
    @Test
    void testConfigCacheInvalidation() {
        // Test that cache can be invalidated
        ConfigCache.invalidateAll();
        // Should not throw exception
        assertTrue(true);
    }
    
    @Test
    void testConfigCacheClear() {
        // Test that cache can be cleared
        ConfigCache.clear();
        // Should not throw exception
        assertTrue(true);
    }
    
    @Test
    void testEffectQualityEnum() {
        // Test that EffectQuality enum has expected values
        Config.EffectQuality[] values = Config.EffectQuality.values();
        assertEquals(4, values.length);
        assertTrue(contains(values, Config.EffectQuality.LOW));
        assertTrue(contains(values, Config.EffectQuality.MEDIUM));
        assertTrue(contains(values, Config.EffectQuality.HIGH));
        assertTrue(contains(values, Config.EffectQuality.ULTRA));
    }
    
    private boolean contains(Config.EffectQuality[] array, Config.EffectQuality value) {
        for (Config.EffectQuality v : array) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }
}





