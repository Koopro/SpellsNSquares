package at.koopro.spells_n_squares.core.util.rendering;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified utility class for consistent tooltip rendering across the mod.
 * Provides standardized tooltip rendering using ColorUtils for consistent coloring.
 */
public final class TooltipUtils {
    private TooltipUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Renders a single-line tooltip at the specified position.
     * Uses Minecraft's standard tooltip rendering with proper positioning.
     * 
     * @param guiGraphics The graphics context
     * @param font The font to use
     * @param tooltip The tooltip text
     * @param x The X position
     * @param y The Y position
     */
    public static void renderTooltip(GuiGraphics guiGraphics, Font font, Component tooltip, int x, int y) {
        if (tooltip == null) {
            return;
        }
        
        // Convert Component to ClientTooltipComponent and render
        List<ClientTooltipComponent> components = List.of(ClientTooltipComponent.create(tooltip.getVisualOrderText()));
        guiGraphics.renderTooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);
    }
    
    /**
     * Renders a multi-line tooltip at the specified position.
     * Uses Minecraft's standard tooltip rendering with proper positioning.
     * 
     * @param guiGraphics The graphics context
     * @param font The font to use
     * @param tooltipLines List of tooltip lines
     * @param x The X position
     * @param y The Y position
     */
    public static void renderTooltip(GuiGraphics guiGraphics, Font font, List<Component> tooltipLines, int x, int y) {
        if (tooltipLines == null || tooltipLines.isEmpty()) {
            return;
        }
        
        // Convert Components to ClientTooltipComponents and render
        List<ClientTooltipComponent> components = tooltipLines.stream()
            .map(component -> ClientTooltipComponent.create(component.getVisualOrderText()))
            .collect(Collectors.toList());
        guiGraphics.renderTooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);
    }
    
    /**
     * Creates a label-value pair Component with consistent formatting.
     * Uses ColorUtils for consistent coloring.
     * 
     * @param label The label text
     * @param value The value text
     * @param labelColor The color for the label (default: gray)
     * @param valueColor The color for the value (default: white)
     * @return A Component with formatted label-value pair
     */
    public static Component createLabelValue(String label, String value, int labelColor, int valueColor) {
        return net.minecraft.network.chat.Component.literal("")
            .append(ColorUtils.coloredText(label + ": ", labelColor))
            .append(ColorUtils.coloredText(value, valueColor));
    }
    
    /**
     * Creates a label-value pair Component with default colors.
     * Uses gray for label and white for value.
     * 
     * @param label The label text
     * @param value The value text
     * @return A Component with formatted label-value pair
     */
    public static Component createLabelValue(String label, String value) {
        int labelColor = ColorUtils.rgb(170, 170, 170); // Gray
        int valueColor = ColorUtils.SPELL_WHITE; // White
        return createLabelValue(label, value, labelColor, valueColor);
    }
    
    /**
     * Creates a separator Component for tooltips.
     * 
     * @param text The separator text (e.g., "--- Section ---")
     * @return A Component with separator formatting
     */
    public static Component createSeparator(String text) {
        int separatorColor = ColorUtils.rgb(85, 85, 85); // Dark gray
        return ColorUtils.coloredText(text, separatorColor);
    }
    
    /**
     * Creates a header Component for tooltips.
     * 
     * @param text The header text
     * @return A Component with header formatting
     */
    public static Component createHeader(String text) {
        int headerColor = ColorUtils.rgb(255, 215, 0); // Gold
        return ColorUtils.coloredText(text, headerColor);
    }
}

