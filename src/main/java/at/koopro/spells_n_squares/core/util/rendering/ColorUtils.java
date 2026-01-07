package at.koopro.spells_n_squares.core.util.rendering;

import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.text.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * Utility class for RGB text coloring and formatting.
 * Provides methods for creating colors, applying colors to text, and color manipulation.
 */
public final class ColorUtils {
    private ColorUtils() {
        // Utility class - prevent instantiation
    }
    
    // House colors (RGB values)
    public static final int GRYFFINDOR_RED = rgb(174, 0, 0);
    public static final int GRYFFINDOR_GOLD = rgb(238, 203, 0);
    public static final int HUFFLEPUFF_YELLOW = rgb(236, 185, 57);
    public static final int HUFFLEPUFF_BLACK = rgb(0, 0, 0);
    public static final int RAVENCLAW_BLUE = rgb(34, 47, 91);
    public static final int RAVENCLAW_BRONZE = rgb(148, 107, 45);
    public static final int SLYTHERIN_GREEN = rgb(26, 71, 42);
    public static final int SLYTHERIN_SILVER = rgb(170, 170, 170);
    
    // Spell colors
    public static final int SPELL_RED = rgb(255, 0, 0);
    public static final int SPELL_BLUE = rgb(0, 100, 255);
    public static final int SPELL_GREEN = rgb(0, 255, 0);
    public static final int SPELL_PURPLE = rgb(128, 0, 255);
    public static final int SPELL_GOLD = rgb(255, 215, 0);
    public static final int SPELL_WHITE = rgb(255, 255, 255);
    
    /**
     * Creates a color from RGB values (0-255).
     * 
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @return The color as an integer (0xAARRGGBB format)
     */
    public static int rgb(int r, int g, int b) {
        r = MathUtils.clamp(r, 0, 255);
        g = MathUtils.clamp(g, 0, 255);
        b = MathUtils.clamp(b, 0, 255);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Creates a color from RGB values (0.0-1.0).
     * 
     * @param r Red component (0.0-1.0)
     * @param g Green component (0.0-1.0)
     * @param b Blue component (0.0-1.0)
     * @return The color as an integer (0xAARRGGBB format)
     */
    public static int rgb(float r, float g, float b) {
        r = MathUtils.clamp(r, 0.0f, 1.0f);
        g = MathUtils.clamp(g, 0.0f, 1.0f);
        b = MathUtils.clamp(b, 0.0f, 1.0f);
        return rgb(
            Math.round(r * 255),
            Math.round(g * 255),
            Math.round(b * 255)
        );
    }
    
    /**
     * Parses a hex color code.
     * Supports formats: #RRGGBB or #AARRGGBB
     * 
     * @param hexCode The hex color code (e.g., "#FF0000" or "#FFFF0000")
     * @return The color as an integer, or 0xFFFFFFFF (white) if invalid
     */
    public static int hex(String hexCode) {
        if (StringUtils.isEmpty(hexCode)) {
            return 0xFFFFFFFF;
        }
        
        // Remove # if present
        String hex = hexCode.startsWith("#") ? hexCode.substring(1) : hexCode;
        
        try {
            if (hex.length() == 6) {
                // RRGGBB format
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                return rgb(r, g, b);
            } else if (hex.length() == 8) {
                // AARRGGBB format
                int a = Integer.parseInt(hex.substring(0, 2), 16);
                int r = Integer.parseInt(hex.substring(2, 4), 16);
                int g = Integer.parseInt(hex.substring(4, 6), 16);
                int b = Integer.parseInt(hex.substring(6, 8), 16);
                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        } catch (NumberFormatException e) {
            // Invalid hex code
        }
        
        return 0xFFFFFFFF; // Default to white
    }
    
    /**
     * Creates a colored text component.
     * 
     * @param text The text to color
     * @param color The color (0xAARRGGBB format)
     * @return A Component with the specified color
     */
    public static Component coloredText(String text, int color) {
        if (text == null) {
            text = "";
        }
        return Component.literal(text).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
    }
    
    /**
     * Creates gradient-colored text.
     * Interpolates between start and end colors across the text.
     * 
     * @param text The text to color
     * @param startColor The starting color (0xAARRGGBB format)
     * @param endColor The ending color (0xAARRGGBB format)
     * @return A Component with gradient coloring (each character gets interpolated color)
     */
    public static Component gradientText(String text, int startColor, int endColor) {
        if (StringUtils.isEmpty(text)) {
            return Component.literal("");
        }
        
        if (text.length() == 1) {
            return coloredText(text, startColor);
        }
        
        net.minecraft.network.chat.MutableComponent result = Component.literal("");
        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / (text.length() - 1);
            int color = blendColors(startColor, endColor, ratio);
            result.append(coloredText(String.valueOf(text.charAt(i)), color));
        }
        
        return result;
    }
    
    /**
     * Blends two colors together.
     * 
     * @param color1 First color (0xAARRGGBB format)
     * @param color2 Second color (0xAARRGGBB format)
     * @param ratio Blend ratio (0.0 = color1, 1.0 = color2)
     * @return The blended color
     */
    public static int blendColors(int color1, int color2, float ratio) {
        ratio = MathUtils.clamp(ratio, 0.0f, 1.0f);
        
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = Math.round(MathUtils.lerp(r1, r2, ratio));
        int g = Math.round(MathUtils.lerp(g1, g2, ratio));
        int b = Math.round(MathUtils.lerp(b1, b2, ratio));
        
        return rgb(r, g, b);
    }
    
    /**
     * Gets a house-specific color.
     * 
     * @param houseName The house name (case-insensitive)
     * @return The primary color for the house, or white if unknown
     */
    public static int getHouseColor(String houseName) {
        if (StringUtils.isEmpty(houseName)) {
            return SPELL_WHITE;
        }
        
        String house = houseName.toLowerCase();
        return switch (house) {
            case "gryffindor" -> GRYFFINDOR_RED;
            case "hufflepuff" -> HUFFLEPUFF_YELLOW;
            case "ravenclaw" -> RAVENCLAW_BLUE;
            case "slytherin" -> SLYTHERIN_GREEN;
            default -> SPELL_WHITE;
        };
    }
    
    /**
     * Gets a house's secondary color.
     * 
     * @param houseName The house name (case-insensitive)
     * @return The secondary color for the house, or white if unknown
     */
    public static int getHouseSecondaryColor(String houseName) {
        if (StringUtils.isEmpty(houseName)) {
            return SPELL_WHITE;
        }
        
        String house = houseName.toLowerCase();
        return switch (house) {
            case "gryffindor" -> GRYFFINDOR_GOLD;
            case "hufflepuff" -> HUFFLEPUFF_BLACK;
            case "ravenclaw" -> RAVENCLAW_BRONZE;
            case "slytherin" -> SLYTHERIN_SILVER;
            default -> SPELL_WHITE;
        };
    }
    
    /**
     * Extracts the red component from a color.
     * 
     * @param color The color (0xAARRGGBB format)
     * @return The red component (0-255)
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Extracts the green component from a color.
     * 
     * @param color The color (0xAARRGGBB format)
     * @return The green component (0-255)
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Extracts the blue component from a color.
     * 
     * @param color The color (0xAARRGGBB format)
     * @return The blue component (0-255)
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    /**
     * Extracts the alpha component from a color.
     * 
     * @param color The color (0xAARRGGBB format)
     * @return The alpha component (0-255)
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
}


