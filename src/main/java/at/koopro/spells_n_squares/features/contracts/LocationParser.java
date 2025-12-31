package at.koopro.spells_n_squares.features.contracts;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Utility class for parsing and validating location strings for contract requirements.
 * Location format: "x,y,z" or "x,y,z,tolerance"
 */
public final class LocationParser {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private LocationParser() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Parsed location data.
     */
    public record ParsedLocation(double x, double y, double z, double tolerance) {
        /**
         * Checks if the parsed location is valid.
         */
        public boolean isValid() {
            return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) && tolerance >= 0;
        }
    }
    
    /**
     * Parses a location string.
     * Format: "x,y,z" or "x,y,z,tolerance"
     * 
     * @param locationStr The location string to parse
     * @return ParsedLocation with parsed coordinates and tolerance, or null if parsing failed
     */
    public static ParsedLocation parse(String locationStr) {
        if (locationStr == null || locationStr.isEmpty()) {
            return null;
        }
        
        String[] parts = locationStr.split(",");
        if (parts.length < 3) {
            LOGGER.warn("Invalid location format: '{}' (expected at least 3 comma-separated values)", locationStr);
            return null;
        }
        
        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            
            // Validate coordinate ranges (reasonable Minecraft world bounds)
            if (Math.abs(x) > 30000000 || Math.abs(y) > 320 || Math.abs(z) > 30000000) {
                LOGGER.warn("Location coordinates out of reasonable range: x={}, y={}, z={}", x, y, z);
                return null;
            }
            
            double tolerance = parts.length > 3 ? Double.parseDouble(parts[3].trim()) : ContractConstants.DEFAULT_LOCATION_TOLERANCE;
            
            // Validate tolerance
            if (tolerance < 0 || tolerance > 1000) {
                LOGGER.warn("Location tolerance out of reasonable range: {} (expected 0-1000)", tolerance);
                tolerance = ContractConstants.DEFAULT_LOCATION_TOLERANCE;
            }
            
            ParsedLocation parsed = new ParsedLocation(x, y, z, tolerance);
            if (!parsed.isValid()) {
                LOGGER.warn("Parsed location contains invalid values: {}", parsed);
                return null;
            }
            
            return parsed;
        } catch (NumberFormatException e) {
            LOGGER.warn("Failed to parse location string '{}': {}", locationStr, e.getMessage());
            return null;
        }
    }
    
    /**
     * Validates if a location string is in the correct format.
     * 
     * @param locationStr The location string to validate
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidFormat(String locationStr) {
        return parse(locationStr) != null;
    }
}


