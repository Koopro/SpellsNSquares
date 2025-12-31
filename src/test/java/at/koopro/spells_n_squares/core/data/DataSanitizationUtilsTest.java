package at.koopro.spells_n_squares.core.data;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class DataSanitizationUtilsTest {
    
    @Test
    void sanitizeIdentifiers_RemovesInvalidOnes() {
        Set<Identifier> identifiers = new HashSet<>();
        identifiers.add(Identifier.parse("spells_n_squares:valid"));
        identifiers.add(Identifier.parse("spells_n_squares:invalid"));
        identifiers.add(null);
        
        Predicate<Identifier> validator = id -> id != null && 
            id.getNamespace().equals("spells_n_squares") && 
            id.getPath().equals("valid");
        
        Set<Identifier> sanitized = DataSanitizationUtils.sanitizeIdentifiers(identifiers, validator, "test");
        
        assertEquals(1, sanitized.size());
        assertTrue(sanitized.contains(Identifier.parse("spells_n_squares:valid")));
    }
    
    @Test
    void sanitizeCurrency_ClampsToMaxTotal() {
        int[] result = DataSanitizationUtils.sanitizeCurrency(100, 50, 30, 1000);
        assertTrue(result[0] >= 0);
        assertTrue(result[1] >= 0);
        assertTrue(result[2] >= 0);
        
        // Test that negative values are clamped
        int[] result2 = DataSanitizationUtils.sanitizeCurrency(-10, -5, -3, 1000);
        assertEquals(0, result2[0]);
        assertEquals(0, result2[1]);
        assertEquals(0, result2[2]);
    }
    
    @Test
    void sanitizeMap_RemovesNullEntries() {
        Map<String, Integer> map = new HashMap<>();
        map.put("valid1", 1);
        map.put("valid2", 2);
        map.put(null, 3);
        map.put("key", null);
        
        int removed = DataSanitizationUtils.sanitizeMap(map, "test");
        
        assertTrue(removed > 0);
        assertFalse(map.containsKey(null));
        assertTrue(map.containsKey("valid1"));
        assertTrue(map.containsKey("valid2"));
    }
    
    @Test
    void sanitizeList_RemovesNullEntries() {
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("valid1");
        list.add(null);
        list.add("valid2");
        list.add(null);
        
        int removed = DataSanitizationUtils.sanitizeList(list, "test");
        
        assertEquals(2, removed);
        assertEquals(2, list.size());
        assertTrue(list.contains("valid1"));
        assertTrue(list.contains("valid2"));
        assertFalse(list.contains(null));
    }
    
    @Test
    void sanitizeCombatStats_ClampsToValidRanges() {
        float[] result = DataSanitizationUtils.sanitizeCombatStats(
            1.5f,  // accuracy (should clamp to 1.0)
            -0.1f, // dodgeChance (should clamp to 0.0)
            0.5f,  // criticalHitChance (valid)
            0.95f  // spellResistance (should clamp to 0.9)
        );
        
        assertEquals(1.0f, result[0], 0.001f); // accuracy
        assertEquals(0.0f, result[1], 0.001f); // dodgeChance
        assertEquals(0.5f, result[2], 0.001f); // criticalHitChance
        assertEquals(0.9f, result[3], 0.001f); // spellResistance
    }
}

