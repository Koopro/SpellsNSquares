package at.koopro.spells_n_squares.features.contracts;

/**
 * Constants for contract system.
 * Centralizes magic numbers used across ContractHandler.
 */
public final class ContractConstants {
    private ContractConstants() {
        // Utility class - prevent instantiation
    }
    
    // Location requirement constants
    /** Default tolerance for location requirement checks (in blocks) */
    public static final double DEFAULT_LOCATION_TOLERANCE = 5.0;
    
    // Contract violation constants
    /** Unbreakable vow damage multiplier (percentage of max health) */
    public static final float UNBREAKABLE_VOW_DAMAGE_MULTIPLIER = 0.5f;
    /** Health threshold below which vow violation is considered fatal */
    public static final float VOW_FATAL_HEALTH_THRESHOLD = 1.0f;
    
    // Reputation penalty constants
    /** Reputation penalty for regular contract violation */
    public static final int REGULAR_CONTRACT_REPUTATION_PENALTY = -10;
}


