package at.koopro.spells_n_squares.core.data;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class DataValidationUtilsTest {
    
    @Test
    void validateRange_ClampsOutOfRangeValues() {
        // Test float range validation
        float value1 = DataValidationUtils.validateRange(1.5f, 0.0f, 1.0f, "test");
        assertEquals(1.0f, value1, 0.001f);
        
        float value2 = DataValidationUtils.validateRange(-0.5f, 0.0f, 1.0f, "test");
        assertEquals(0.0f, value2, 0.001f);
        
        float value3 = DataValidationUtils.validateRange(0.5f, 0.0f, 1.0f, "test");
        assertEquals(0.5f, value3, 0.001f);
        
        // Test int range validation
        int value4 = DataValidationUtils.validateRange(150, 0, 100, "test");
        assertEquals(100, value4);
        
        int value5 = DataValidationUtils.validateRange(-10, 0, 100, "test");
        assertEquals(0, value5);
        
        int value6 = DataValidationUtils.validateRange(50, 0, 100, "test");
        assertEquals(50, value6);
    }
    
    @Test
    void validateNonNegative_ClampsNegativeValues() {
        float value1 = DataValidationUtils.validateNonNegative(-5.0f, "test");
        assertEquals(0.0f, value1, 0.001f);
        
        float value2 = DataValidationUtils.validateNonNegative(5.0f, "test");
        assertEquals(5.0f, value2, 0.001f);
        
        int value3 = DataValidationUtils.validateNonNegative(-10, "test");
        assertEquals(0, value3);
        
        int value4 = DataValidationUtils.validateNonNegative(10, "test");
        assertEquals(10, value4);
    }
    
    @Test
    void validatePercentage_ClampsToValidRange() {
        float value1 = DataValidationUtils.validatePercentage(1.5f, "test");
        assertEquals(1.0f, value1, 0.001f);
        
        float value2 = DataValidationUtils.validatePercentage(-0.5f, "test");
        assertEquals(0.0f, value2, 0.001f);
        
        float value3 = DataValidationUtils.validatePercentage(0.75f, "test");
        assertEquals(0.75f, value3, 0.001f);
    }
    
    @Test
    void validateIdentifier_ValidatesCorrectly() {
        Identifier validId = Identifier.parse("spells_n_squares:test");
        Predicate<Identifier> validator = id -> id != null && id.getNamespace().equals("spells_n_squares");
        
        assertTrue(DataValidationUtils.validateIdentifier(validId, validator, "test"));
        assertFalse(DataValidationUtils.validateIdentifier(null, validator, "test"));
        
        Identifier invalidId = Identifier.parse("other:test");
        assertFalse(DataValidationUtils.validateIdentifier(invalidId, validator, "test"));
    }
    
    @Test
    void validateString_ReturnsDefaultForInvalid() {
        String result1 = DataValidationUtils.validateString(null, "test", "default");
        assertEquals("default", result1);
        
        String result2 = DataValidationUtils.validateString("", "test", "default");
        assertEquals("default", result2);
        
        String result3 = DataValidationUtils.validateString("valid", "test", "default");
        assertEquals("valid", result3);
    }
    
    @Test
    void validateListSize_ValidatesCorrectly() {
        assertTrue(DataValidationUtils.validateListSize(5, 0, 10, "test"));
        assertFalse(DataValidationUtils.validateListSize(15, 0, 10, "test"));
        assertFalse(DataValidationUtils.validateListSize(-1, 0, 10, "test"));
    }
}

