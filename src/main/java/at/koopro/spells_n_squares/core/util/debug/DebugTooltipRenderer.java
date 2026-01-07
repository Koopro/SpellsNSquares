package at.koopro.spells_n_squares.core.util.debug;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders debug tooltips showing detailed information about items, blocks, entities, and players.
 * Similar to SpellTooltipRenderer but for debug data display.
 */
public final class DebugTooltipRenderer {
    private DebugTooltipRenderer() {
        // Utility class - prevent instantiation
    }
    
    private static final int MAX_TOOLTIP_WIDTH = 400;
    private static final int TOOLTIP_PADDING = 6;
    private static final int TOOLTIP_OFFSET_X = 16;
    private static final int TOOLTIP_OFFSET_Y = 16;
    private static final int MAX_NBT_LINES = 20; // Limit NBT display lines
    
    // Color codes for different sections
    private static final String COLOR_ITEM = "§b"; // Cyan
    private static final String COLOR_BLOCK = "§a"; // Green
    private static final String COLOR_ENTITY = "§e"; // Yellow
    private static final String COLOR_PLAYER = "§6"; // Gold
    private static final String COLOR_LABEL = "§7"; // Gray
    private static final String COLOR_VALUE = "§f"; // White
    private static final String COLOR_SEPARATOR = "§8"; // Dark gray
    
    /**
     * Renders a debug tooltip for an item.
     */
    public static void renderItemTooltip(GuiGraphics guiGraphics, Font font,
                                        DebugDataCollector.ItemDebugData data,
                                        int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (data == null) {
            return;
        }
        
        List<String> lines = buildItemTooltipLines(data);
        renderTooltip(guiGraphics, font, lines, mouseX, mouseY, screenWidth, screenHeight, COLOR_ITEM);
    }
    
    /**
     * Renders a debug tooltip for a block.
     */
    public static void renderBlockTooltip(GuiGraphics guiGraphics, Font font,
                                         DebugDataCollector.BlockDebugData data,
                                         int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (data == null) {
            return;
        }
        
        List<String> lines = buildBlockTooltipLines(data);
        renderTooltip(guiGraphics, font, lines, mouseX, mouseY, screenWidth, screenHeight, COLOR_BLOCK);
    }
    
    /**
     * Renders a debug tooltip for an entity.
     */
    public static void renderEntityTooltip(GuiGraphics guiGraphics, Font font,
                                          DebugDataCollector.EntityDebugData data,
                                          int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (data == null) {
            return;
        }
        
        List<String> lines = buildEntityTooltipLines(data);
        renderTooltip(guiGraphics, font, lines, mouseX, mouseY, screenWidth, screenHeight, COLOR_ENTITY);
    }
    
    /**
     * Renders a debug tooltip for a player.
     */
    public static void renderPlayerTooltip(GuiGraphics guiGraphics, Font font,
                                         DebugDataCollector.PlayerDebugData data,
                                         int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (data == null) {
            return;
        }
        
        List<String> lines = buildPlayerTooltipLines(data);
        renderTooltip(guiGraphics, font, lines, mouseX, mouseY, screenWidth, screenHeight, COLOR_PLAYER);
    }
    
    /**
     * Builds tooltip lines for an item.
     */
    private static List<String> buildItemTooltipLines(DebugDataCollector.ItemDebugData data) {
        List<String> lines = new ArrayList<>();
        
        lines.add(COLOR_ITEM + "=== ITEM DEBUG ===");
        lines.add(COLOR_LABEL + "Name: " + COLOR_VALUE + data.itemName);
        lines.add(COLOR_LABEL + "ID: " + COLOR_VALUE + data.itemId);
        lines.add(COLOR_LABEL + "Count: " + COLOR_VALUE + data.count);
        if (data.damage > 0) {
            lines.add(COLOR_LABEL + "Damage: " + COLOR_VALUE + data.damage);
        }
        
        if (!data.dataComponents.isEmpty()) {
            lines.add(COLOR_SEPARATOR + "--- Data Components ---");
            for (DebugDataCollector.ComponentInfo component : data.dataComponents) {
                String value = truncateString(component.valueString, 60);
                lines.add(COLOR_LABEL + component.componentId + ": " + COLOR_VALUE + value);
            }
        }
        
        if (data.hasNBT && data.nbtData != null) {
            lines.add(COLOR_SEPARATOR + "--- NBT Data ---");
            List<String> nbtLines = formatNBT(data.nbtData, MAX_NBT_LINES);
            lines.addAll(nbtLines);
        }
        
        return lines;
    }
    
    /**
     * Builds tooltip lines for a block.
     */
    private static List<String> buildBlockTooltipLines(DebugDataCollector.BlockDebugData data) {
        List<String> lines = new ArrayList<>();
        
        lines.add(COLOR_BLOCK + "=== BLOCK DEBUG ===");
        lines.add(COLOR_LABEL + "Name: " + COLOR_VALUE + data.blockName);
        lines.add(COLOR_LABEL + "ID: " + COLOR_VALUE + data.blockId);
        lines.add(COLOR_LABEL + "Position: " + COLOR_VALUE + formatBlockPos(data.position));
        
        if (!data.properties.isEmpty()) {
            lines.add(COLOR_SEPARATOR + "--- Properties ---");
            for (DebugDataCollector.PropertyInfo prop : data.properties) {
                lines.add(COLOR_LABEL + prop.propertyName + ": " + COLOR_VALUE + prop.value);
            }
        }
        
        if (data.blockEntity != null) {
            lines.add(COLOR_SEPARATOR + "--- Block Entity ---");
            lines.add(COLOR_LABEL + "Type: " + COLOR_VALUE + data.blockEntity.blockEntityType);
            if (data.blockEntity.nbtData != null) {
                lines.add(COLOR_SEPARATOR + "--- Block Entity NBT ---");
                List<String> nbtLines = formatNBT(data.blockEntity.nbtData, MAX_NBT_LINES);
                lines.addAll(nbtLines);
            }
        }
        
        return lines;
    }
    
    /**
     * Builds tooltip lines for an entity.
     */
    private static List<String> buildEntityTooltipLines(DebugDataCollector.EntityDebugData data) {
        List<String> lines = new ArrayList<>();
        
        lines.add(COLOR_ENTITY + "=== ENTITY DEBUG ===");
        lines.add(COLOR_LABEL + "Type: " + COLOR_VALUE + data.entityType);
        lines.add(COLOR_LABEL + "ID: " + COLOR_VALUE + data.entityId);
        lines.add(COLOR_LABEL + "Position: " + COLOR_VALUE + formatVec3(data.position));
        lines.add(COLOR_LABEL + "Motion: " + COLOR_VALUE + formatVec3(data.motion));
        lines.add(COLOR_LABEL + "Rotation: " + COLOR_VALUE + String.format("Yaw=%.1f, Pitch=%.1f", data.rotationYaw, data.rotationPitch));
        lines.add(COLOR_LABEL + "Alive: " + COLOR_VALUE + data.isAlive);
        
        if (data.nbtData != null && !data.nbtData.isEmpty()) {
            lines.add(COLOR_SEPARATOR + "--- NBT Data ---");
            List<String> nbtLines = formatNBT(data.nbtData, MAX_NBT_LINES);
            lines.addAll(nbtLines);
        }
        
        return lines;
    }
    
    /**
     * Builds tooltip lines for a player.
     */
    private static List<String> buildPlayerTooltipLines(DebugDataCollector.PlayerDebugData data) {
        List<String> lines = new ArrayList<>();
        
        lines.add(COLOR_PLAYER + "=== PLAYER DEBUG ===");
        lines.add(COLOR_LABEL + "Name: " + COLOR_VALUE + data.playerName);
        lines.add(COLOR_LABEL + "Position: " + COLOR_VALUE + formatVec3(data.position));
        lines.add(COLOR_LABEL + "Health: " + COLOR_VALUE + String.format("%.1f / %.1f", data.health, data.maxHealth));
        lines.add(COLOR_LABEL + "Food: " + COLOR_VALUE + data.foodLevel);
        lines.add(COLOR_LABEL + "Saturation: " + COLOR_VALUE + String.format("%.1f", data.saturation));
        
        if (!data.persistentDataKeys.isEmpty()) {
            lines.add(COLOR_SEPARATOR + "--- Persistent Data ---");
            for (String key : data.persistentDataKeys) {
                lines.add(COLOR_VALUE + key);
            }
        }
        
        if (data.playerDataNBT != null && !data.playerDataNBT.isEmpty()) {
            lines.add(COLOR_SEPARATOR + "--- Player Data NBT ---");
            List<String> nbtLines = formatNBT(data.playerDataNBT, MAX_NBT_LINES);
            lines.addAll(nbtLines);
        }
        
        return lines;
    }
    
    /**
     * Formats NBT data for display, with truncation for large tags.
     */
    private static List<String> formatNBT(CompoundTag nbt, int maxLines) {
        List<String> lines = new ArrayList<>();
        if (nbt == null || nbt.isEmpty()) {
            lines.add(COLOR_VALUE + "(empty)");
            return lines;
        }
        
        String nbtString = nbt.toString();
        String[] parts = nbtString.split("\n");
        
        int linesAdded = 0;
        for (String part : parts) {
            if (linesAdded >= maxLines) {
                lines.add(COLOR_SEPARATOR + "... (truncated)");
                break;
            }
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                lines.add(COLOR_VALUE + truncateString(trimmed, 80));
                linesAdded++;
            }
        }
        
        if (lines.isEmpty()) {
            String displayString = nbtString.length() > 100 ? truncateString(nbtString, 100) : nbtString;
            lines.add(COLOR_VALUE + displayString);
        }
        
        return lines;
    }
    
    /**
     * Formats a BlockPos for display.
     */
    private static String formatBlockPos(BlockPos pos) {
        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }
    
    /**
     * Formats a Vec3 for display.
     */
    private static String formatVec3(Vec3 vec) {
        return String.format("(%.2f, %.2f, %.2f)", vec.x, vec.y, vec.z);
    }
    
    /**
     * Truncates a string to a maximum length, adding ellipsis if needed.
     */
    private static String truncateString(String str, int maxLength) {
        if (str == null) {
            return "null";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Main rendering method that handles positioning and drawing.
     */
    private static void renderTooltip(GuiGraphics guiGraphics, Font font, List<String> tooltipLines,
                                     int mouseX, int mouseY, int screenWidth, int screenHeight,
                                     String headerColor) {
        if (tooltipLines.isEmpty()) {
            return;
        }
        
        // Calculate tooltip dimensions
        int tooltipWidth = calculateTooltipWidth(font, tooltipLines);
        int tooltipHeight = tooltipLines.size() * font.lineHeight + TOOLTIP_PADDING * 2;
        
        // Calculate tooltip position (near crosshair, but avoid screen edges)
        int tooltipX = mouseX + TOOLTIP_OFFSET_X;
        int tooltipY = mouseY + TOOLTIP_OFFSET_Y;
        
        // Adjust position if tooltip would go off screen
        if (tooltipX + tooltipWidth > screenWidth) {
            tooltipX = mouseX - tooltipWidth - TOOLTIP_OFFSET_X;
        }
        if (tooltipY + tooltipHeight > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - TOOLTIP_PADDING;
        }
        
        // Ensure tooltip stays on screen
        tooltipX = Math.max(TOOLTIP_PADDING, Math.min(tooltipX, screenWidth - tooltipWidth - TOOLTIP_PADDING));
        tooltipY = Math.max(TOOLTIP_PADDING, Math.min(tooltipY, screenHeight - tooltipHeight - TOOLTIP_PADDING));
        
        int alpha = 255;
        
        // Draw tooltip background
        drawTooltipBackground(guiGraphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight, alpha);
        
        // Draw tooltip text
        drawTooltipText(guiGraphics, font, tooltipLines, tooltipX, tooltipY, alpha);
    }
    
    /**
     * Calculates the width needed for the tooltip.
     */
    private static int calculateTooltipWidth(Font font, List<String> tooltipLines) {
        int maxWidth = 0;
        for (String line : tooltipLines) {
            // Remove formatting codes for width calculation
            String plainLine = line.replaceAll("§[0-9a-fk-or]", "");
            int lineWidth = font.width(plainLine);
            maxWidth = Math.max(maxWidth, lineWidth);
        }
        return Math.min(maxWidth + TOOLTIP_PADDING * 2, MAX_TOOLTIP_WIDTH);
    }
    
    /**
     * Draws the tooltip background.
     */
    private static void drawTooltipBackground(GuiGraphics guiGraphics, int x, int y,
                                             int width, int height, int alpha) {
        // Outer border
        int borderColor = (alpha << 24) | 0x000000;
        guiGraphics.fill(x, y, x + width, y + height, borderColor);
        
        // Inner background
        int bgColor = ((int) (alpha * 0.95f) << 24) | 0x1a1a1a;
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, bgColor);
        
        // Highlight border at top
        int highlightColor = ((int) (alpha * 0.3f) << 24) | 0xFFFFFF;
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, highlightColor);
    }
    
    /**
     * Draws the tooltip text.
     */
    private static void drawTooltipText(GuiGraphics guiGraphics, Font font,
                                       List<String> tooltipLines, int x, int y, int alpha) {
        int textY = y + TOOLTIP_PADDING;
        for (String line : tooltipLines) {
            // Use drawString with shadow for better visibility
            // Formatting codes are handled automatically by Minecraft's text rendering
            guiGraphics.drawString(font, line, x + TOOLTIP_PADDING, textY, 0xFFFFFF, true);
            textY += font.lineHeight;
        }
    }
}

