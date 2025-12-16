package at.koopro.spells_n_squares.features.spell.client;

/**
 * Shared UI constants for spell-related UI components.
 * Contains colors, sizes, and other constants used across spell UI elements.
 */
public final class SpellUIConstants {
    private SpellUIConstants() {
        // Utility class - prevent instantiation
    }
    
    // Text colors
    public static final int TEXT_COLOR_NORMAL = 0xFFFFFF00; // Bright yellow
    public static final int TEXT_COLOR_COOLDOWN = 0xFF808080; // Gray for cooldown
    public static final int TEXT_COLOR_WHITE = 0xFFFFFF;
    public static final int TEXT_COLOR_SELECTED = 0xFFFFFF00; // Yellow for selected items
    
    // Icon sizes
    public static final int ICON_SIZE_HUD = 16; // Icon size in HUD
    public static final int ICON_SIZE_SELECTION = 32; // Icon size in selection screen
    
    // Background colors
    public static final int BG_COLOR_DARK = 0xFF202020;
    public static final int BG_COLOR_DARKER = 0xFF303030;
    public static final int BG_COLOR_TOOLTIP = 0xFF1C1C1C;
    public static final int BG_COLOR_SEMI_TRANSPARENT = 0x80000000; // Semi-transparent black
    public static final int BG_COLOR_SCREEN = 0xC0101010; // Screen background (semi-transparent dark)
    
    // Border colors
    public static final int BORDER_COLOR_DEFAULT = 0xFF808080;
    public static final int BORDER_COLOR_HOVER = 0xFFFFFF00;
    public static final int BORDER_COLOR_SELECTED = 0xFFFF00FF;
    public static final int BORDER_COLOR_TOOLTIP = 0xFF505050;
    public static final int BORDER_COLOR_KEYBIND = 0xCCFFFFAA; // Soft yellow border
    public static final int BORDER_COLOR_KEYBIND_WHITE = 0xCCFFFFFF; // Soft white border
    public static final int BORDER_COLOR_SPELL_AVAILABLE = 0xFF606060; // Gray border for available spells
    
    // Tint colors
    public static final int TINT_COOLDOWN = 0xFF808080; // Gray tint for cooldown
    public static final int TINT_NORMAL = 0xFFFFFFFF; // White tint (no tint)
    public static final int TINT_SEMI_TRANSPARENT = 0xCCFFFFFF; // Semi-transparent white
    
    // Text colors (additional)
    public static final int TEXT_COLOR_GRAY = 0x808080; // Gray text
    public static final int TEXT_COLOR_EMPTY_SLOT = 0x808080; // Gray for empty slots
    
    // UI layout constants
    public static final int TITLE_Y_OFFSET = 20; // Y position for screen title
    public static final int LABEL_OFFSET_Y = 12; // Y offset for slot labels above slots
    public static final int SELECTION_TEXT_OFFSET_Y = 20; // Y offset for selection indicator text
}

