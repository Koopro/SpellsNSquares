package at.koopro.spells_n_squares.core.client.gui;

import at.koopro.spells_n_squares.core.util.text.StringUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for text wrapping and rendering in GUI screens.
 * Provides reusable methods for wrapping text to fit within a maximum width.
 */
public final class TextWrappingHelper {
    private TextWrappingHelper() {
        // Utility class
    }
    
    /**
     * Wraps text to fit within a maximum width using the given font.
     *
     * @param font The font to use for width calculations
     * @param text The text to wrap
     * @param maxWidth The maximum width in pixels
     * @return A list of wrapped text lines
     */
    public static List<String> wrapText(Font font, String text, int maxWidth) {
        if (StringUtils.isEmpty(text)) {
            return List.of("");
        }
        
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            // Use StringBuilder for test line construction to avoid temporary String objects
            StringBuilder testLine = new StringBuilder(currentLine);
            if (testLine.length() > 0) {
                testLine.append(" ");
            }
            testLine.append(word);
            int testWidth = font.width(testLine.toString());
            
            if (testWidth > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.isEmpty() ? List.of(text) : lines;
    }
    
    /**
     * Calculates the height needed to render wrapped text.
     *
     * @param font The font to use
     * @param text The text to measure
     * @param maxWidth The maximum width in pixels
     * @return The height in pixels needed to render the wrapped text
     */
    public static int getWrappedTextHeight(Font font, String text, int maxWidth) {
        return wrapText(font, text, maxWidth).size() * font.lineHeight;
    }
    
    /**
     * Draws wrapped text at the specified position.
     *
     * @param guiGraphics The graphics context
     * @param font The font to use
     * @param text The text to draw
     * @param x The x coordinate
     * @param y The y coordinate
     * @param maxWidth The maximum width in pixels
     * @param color The text color
     * @param shadow Whether to draw with shadow
     */
    public static void drawWrappedText(GuiGraphics guiGraphics, Font font, String text, 
                                      int x, int y, int maxWidth, int color, boolean shadow) {
        List<String> lines = wrapText(font, text, maxWidth);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), x, y + i * font.lineHeight, color, shadow);
        }
    }
}

