package at.koopro.spells_n_squares.features.spell.client;

/**
 * Constants for spell UI rendering.
 * Centralizes color values, sizes, and other UI-related constants.
 */
public final class SpellUIConstants {
    private SpellUIConstants() {
        // Utility class - prevent instantiation
    }
    
    // Text colors (ARGB format)
    public static final int TEXT_COLOR_NORMAL = 0xFFFFFFFF; // White
    public static final int TEXT_COLOR_COOLDOWN = 0xFF808080; // Gray
    
    // Icon sizes
    public static final int ICON_SIZE_HUD = 16; // 16x16 pixels for HUD display
    
    // Tint colors (ARGB format)
    public static final int TINT_NORMAL = -1; // No tint (0xFFFFFFFF)
    public static final int TINT_COOLDOWN = 0xFF808080; // Gray tint for cooldown
    
    // Background colors (ARGB format)
    public static final int BG_COLOR_SEMI_TRANSPARENT = 0x80000000; // Semi-transparent black
    
    // Border colors (ARGB format)
    public static final int BORDER_COLOR_KEYBIND = 0xFF404040; // Dark gray border
    public static final int BORDER_COLOR_KEYBIND_WHITE = 0xFFFFFFFF; // White border
}

