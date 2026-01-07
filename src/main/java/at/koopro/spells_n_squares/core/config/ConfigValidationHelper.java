package at.koopro.spells_n_squares.core.config;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.function.Predicate;

/**
 * Helper class for configuration validation.
 * Provides methods to validate config values and ensure they're within acceptable ranges.
 */
public final class ConfigValidationHelper {
    
    private static final Map<String, List<ValidationRule>> VALIDATION_RULES = new HashMap<>();
    
    private ConfigValidationHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a validation rule.
     */
    public record ValidationRule(
        String configKey,
        Predicate<Object> validator,
        String errorMessage
    ) {}
    
    /**
     * Validates a config value.
     * 
     * @param key The config key
     * @param value The value to validate
     * @return Validation result
     */
    public static ValidationResult validate(String key, Object value) {
        if (key == null || value == null) {
            return new ValidationResult(false, "Key or value is null");
        }
        
        List<ValidationRule> rules = VALIDATION_RULES.get(key);
        if (rules == null || rules.isEmpty()) {
            return new ValidationResult(true, null);
        }
        
        for (ValidationRule rule : rules) {
            if (!rule.validator().test(value)) {
                DevLogger.logWarn(ConfigValidationHelper.class, "validate",
                    "Config validation failed for " + key + ": " + rule.errorMessage());
                return new ValidationResult(false, rule.errorMessage());
            }
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Registers a validation rule for a config key.
     * 
     * @param key The config key
     * @param validator The validation predicate
     * @param errorMessage The error message if validation fails
     */
    public static void registerRule(String key, Predicate<Object> validator, String errorMessage) {
        if (key == null || validator == null) {
            return;
        }
        
        List<ValidationRule> rules = VALIDATION_RULES.computeIfAbsent(key, k -> new ArrayList<>());
        rules.add(new ValidationRule(key, validator, errorMessage));
    }
    
    /**
     * Validates all config values.
     * 
     * @param configValues Map of config keys to values
     * @return Map of keys to validation results
     */
    public static Map<String, ValidationResult> validateAll(Map<String, Object> configValues) {
        if (configValues == null) {
            return Collections.emptyMap();
        }
        
        Map<String, ValidationResult> results = new HashMap<>();
        for (Map.Entry<String, Object> entry : configValues.entrySet()) {
            results.put(entry.getKey(), validate(entry.getKey(), entry.getValue()));
        }
        
        return results;
    }
    
    /**
     * Represents a validation result.
     */
    public record ValidationResult(boolean valid, String errorMessage) {}
}

