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
    public static final int TEXT_COLOR_COOLDOWN_TIME = 0xFFFF4444; // Light red for cooldown time text
    public static final int TEXT_COLOR_SELECTED = 0xFFFFFF88; // Yellow-white for selected slot
    public static final int TEXT_COLOR_ASSIGNED = 0xFF88FF88; // Light green for assigned spells
    
    // Icon sizes
    public static final int ICON_SIZE_HUD = 20; // 20x20 pixels for HUD display (increased from 16)
    public static final int ICON_SIZE_SCREEN = 16; // 16x16 pixels for selection screen
    
    // Tint colors (ARGB format)
    public static final int TINT_NORMAL = -1; // No tint (0xFFFFFFFF)
    public static final int TINT_COOLDOWN = 0xFF808080; // Gray tint for cooldown
    public static final int TINT_SELECTED = 0xFFFFFF88; // Yellow-white tint for selected slot
    
    // Background colors (ARGB format)
    public static final int BG_COLOR_SEMI_TRANSPARENT = 0xCC000000; // More opaque black background
    public static final int BG_COLOR_COOLDOWN_OVERLAY = 0xAA000000; // Dark overlay for cooldown
    public static final int BG_COLOR_SELECTED = 0x66FFFFFF; // White glow for selected slot
    public static final int BG_COLOR_SCREEN = 0xE0101010; // Dark background for selection screen
    public static final int BG_COLOR_BUTTON = 0xFF2C2C2C; // Dark gray for buttons
    public static final int BG_COLOR_BUTTON_HOVER = 0xFF3C3C3C; // Lighter gray for button hover
    public static final int BG_COLOR_BUTTON_SELECTED = 0xFF4A4A2C; // Yellow-tinted for selected button
    
    // Enhanced visual design colors
    public static final int BG_COLOR_GRADIENT_START = 0xF0151515; // Slightly lighter for gradient
    public static final int BG_COLOR_GRADIENT_END = 0xF0050505; // Darker for gradient
    public static final int SHADOW_COLOR = 0x80000000; // Black shadow
    public static final int GLOW_COLOR_SELECTED = 0x40FFFFFF; // White glow
    public static final int GLOW_COLOR_HOVER = 0x30FFFFFF; // Subtle white glow on hover
    public static final int BORDER_COLOR_ENHANCED = 0xFF505050; // Enhanced border color
    public static final int BORDER_COLOR_HOVER = 0xFF707070; // Border color on hover
    
    // Border colors (ARGB format)
    public static final int BORDER_COLOR_KEYBIND = 0xFF606060; // Medium gray border
    public static final int BORDER_COLOR_KEYBIND_WHITE = 0xFFFFFFFF; // White border
    public static final int BORDER_COLOR_SELECTED = 0xFFFFFF88; // Yellow-white border for selected
    public static final int BORDER_COLOR_ICON = 0xFF404040; // Dark gray border for icons
    
    // Cooldown display
    public static final int COOLDOWN_TEXT_SIZE = 8; // Font size for cooldown text (will use scaled font)
    public static final float COOLDOWN_OVERLAY_ALPHA = 0.7f; // Alpha for cooldown overlay (0.0-1.0)
    
    // Spacing and sizing
    public static final int ICON_BORDER_WIDTH = 2; // Border width around icons
    public static final int KEYBIND_CHIP_PADDING_X = 3; // Horizontal padding for keybind chips (reduced from 6)
    public static final int KEYBIND_CHIP_PADDING_Y = 1; // Vertical padding for keybind chips (reduced from 3)
    public static final int SELECTION_GLOW_SIZE = 4; // Size of selection glow effect
    
    // Enhanced visual effects
    public static final int SHADOW_OFFSET = 2; // Shadow offset for depth
    public static final float GRADIENT_INTENSITY = 0.3f; // Gradient intensity (0.0-1.0)
    public static final int BUTTON_ROUNDING = 2; // Button corner rounding (pixels)
}














