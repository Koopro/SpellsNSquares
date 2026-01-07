package at.koopro.spells_n_squares.core.util.debug;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.base.init.client.ModKeybinds;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.client.KeyStateTracker;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.core.data.PlayerIdentityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

/**
 * Main handler for debug tooltip display.
 * Extends default Minecraft tooltips with comprehensive debug information.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class DebugTooltipHandler {
    private static boolean tooltipEnabled = false;
    private static final KeyStateTracker keyTracker = new KeyStateTracker();
    
    // Color constants for debug tooltips
    private static final int COLOR_HEADER = ColorUtils.rgb(255, 215, 0); // Gold
    private static final int COLOR_LABEL = ColorUtils.rgb(170, 170, 170); // Gray
    private static final int COLOR_VALUE = ColorUtils.SPELL_WHITE; // White
    private static final int COLOR_SEPARATOR = ColorUtils.rgb(85, 85, 85); // Dark gray
    private static final int COLOR_ERROR = ColorUtils.SPELL_RED; // Red
    private static final int COLOR_SUCCESS = ColorUtils.SPELL_GREEN; // Green
    
    /**
     * Handles keybind toggle for debug tooltips.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Check if keybind is initialized
            if (ModKeybinds.DEBUG_ITEM_TOOLTIP == null) {
                return;
            }
            
            // Toggle tooltip when keybind is pressed
            if (keyTracker.wasJustPressed(ModKeybinds.DEBUG_ITEM_TOOLTIP)) {
                tooltipEnabled = !tooltipEnabled;
                // Send feedback to player via action bar
                if (tooltipEnabled) {
                    mc.gui.setOverlayMessage(ColorUtils.coloredText("Debug tooltips enabled", COLOR_SUCCESS), false);
                } else {
                    mc.gui.setOverlayMessage(ColorUtils.coloredText("Debug tooltips disabled", COLOR_ERROR), false);
                }
            }
        }, "handling debug tooltip toggle", mc.player);
    }
    
    /**
     * Adds debug information to item tooltips.
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!isTooltipEnabled()) {
            return;
        }
        
        ItemStack stack = event.getItemStack();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        SafeEventHandler.execute(() -> {
            List<Component> tooltip = event.getToolTip();
        
            // Add separator
            tooltip.add(ColorUtils.coloredText("--- Debug Info ---", COLOR_SEPARATOR));
            
            // Collect and add debug data
            DebugDataCollector.ItemDebugData itemData = DebugDataCollector.collectItemData(stack);
            if (itemData != null) {
                // Item ID
                tooltip.add(createLabelValue("ID", itemData.itemId));
                
                // Count
                if (itemData.count > 1) {
                    tooltip.add(createLabelValue("Count", String.valueOf(itemData.count)));
                }
                
                // Damage
                if (itemData.damage > 0) {
                    tooltip.add(createLabelValue("Damage", String.valueOf(itemData.damage)));
                }
                
                // Data Components
                if (!itemData.dataComponents.isEmpty()) {
                    tooltip.add(ColorUtils.coloredText("--- Data Components ---", COLOR_SEPARATOR));
                    for (DebugDataCollector.ComponentInfo component : itemData.dataComponents) {
                        String value = truncateString(component.valueString, 50);
                        tooltip.add(createLabelValue(component.componentId, value));
                    }
                }
                
                // NBT Data
                if (itemData.hasNBT && itemData.nbtData != null) {
                    tooltip.add(ColorUtils.coloredText("--- NBT Data ---", COLOR_SEPARATOR));
                    List<String> nbtLines = formatNBT(itemData.nbtData, 10);
                    for (String line : nbtLines) {
                        tooltip.add(ColorUtils.coloredText(line, COLOR_LABEL));
                    }
                }
            }
        }, "adding debug tooltip info", mc.player);
    }
    
    /**
     * Renders player debug tooltip when looking at a player entity.
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // Use same check as item tooltips for consistency
        if (!isTooltipEnabled()) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Don't render when a screen is open (except inventory) - same as VitalityOverlay
        if (mc.screen != null && !(mc.screen instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen)) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            boolean showingOtherPlayer = false;
            
            // Check if player is looking at another player
            var hitResult = mc.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                var entityHit = (EntityHitResult) hitResult;
                if (entityHit.getEntity() instanceof Player targetPlayer && targetPlayer != mc.player) {
                    // Collect player data
                    DebugDataCollector.PlayerDebugData playerData = DebugDataCollector.collectPlayerData(targetPlayer);
                    if (playerData != null) {
                        // Render tooltip overlay
                        renderPlayerTooltipOverlay(event.getGuiGraphics(), mc.font, playerData, 
                            mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                        showingOtherPlayer = true;
                    }
                }
            }
            
            // Always show tooltip for self when debug tooltips are enabled
            // (unless already showing another player's tooltip)
            if (!showingOtherPlayer) {
                // First, render a simple test indicator to verify rendering works
                Component indicator = ColorUtils.coloredText("[DEBUG TOOLTIP ACTIVE]", COLOR_SUCCESS);
                event.getGuiGraphics().drawString(mc.font, indicator, 10, 5, ColorUtils.SPELL_WHITE, true);
                
                renderSelfPlayerTooltip(event.getGuiGraphics(), mc.font, mc.player,
                    mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }
        }, "rendering debug tooltip GUI", mc.player);
    }
    
    /**
     * Renders player tooltip overlay when looking at a player.
     */
    private static void renderPlayerTooltipOverlay(GuiGraphics guiGraphics, Font font,
                                                   DebugDataCollector.PlayerDebugData data,
                                                   int screenWidth, int screenHeight) {
        List<Component> tooltipLines = buildPlayerTooltipLines(data);
        
        // Position near center of screen
        int x = screenWidth / 2 + 20;
        int y = screenHeight / 2 - 100;
        
        // Render text directly (simple approach)
        int currentY = y;
        for (Component line : tooltipLines) {
            guiGraphics.drawString(font, line, x, currentY, ColorUtils.SPELL_WHITE, true);
            currentY += font.lineHeight;
        }
    }
    
    /**
     * Renders self player tooltip in debug style (top-left corner).
     */
    private static void renderSelfPlayerTooltip(GuiGraphics guiGraphics, Font font, Player player,
                                               int screenWidth, int screenHeight) {
        DebugDataCollector.PlayerDebugData playerData = DebugDataCollector.collectPlayerData(player);
        if (playerData == null) {
            // Even if data collection fails, show basic info
            guiGraphics.drawString(font, ColorUtils.coloredText("=== Player Debug ===", COLOR_HEADER), 10, 20, ColorUtils.SPELL_WHITE, true);
            guiGraphics.drawString(font, createLabelValue("Name", player.getName().getString()), 10, 32, ColorUtils.SPELL_WHITE, true);
            guiGraphics.drawString(font, ColorUtils.coloredText("Failed to collect player data", COLOR_ERROR), 10, 44, ColorUtils.SPELL_WHITE, true);
            return;
        }
        
        List<Component> tooltipLines = buildPlayerTooltipLines(playerData);
        
        if (tooltipLines.isEmpty()) {
            // Show at least something if lines are empty
            guiGraphics.drawString(font, ColorUtils.coloredText("=== Player Debug ===", COLOR_HEADER), 10, 20, ColorUtils.SPELL_WHITE, true);
            guiGraphics.drawString(font, createLabelValue("Name", playerData.playerName), 10, 32, ColorUtils.SPELL_WHITE, true);
            return;
        }
        
        // Render in top-left corner (below the debug indicator)
        int y = 20; // Start below the "[DEBUG TOOLTIP ACTIVE]" indicator
        for (Component line : tooltipLines) {
            guiGraphics.drawString(font, line, 10, y, ColorUtils.SPELL_WHITE, true);
            y += font.lineHeight;
        }
    }
    
    /**
     * Builds tooltip lines for player data.
     */
    private static List<Component> buildPlayerTooltipLines(DebugDataCollector.PlayerDebugData data) {
        List<Component> lines = new java.util.ArrayList<>();
        
        if (data == null) {
            lines.add(ColorUtils.coloredText("Error: Player data is null", COLOR_ERROR));
            return lines;
        }
        
        // Always show basic player info first
        lines.add(ColorUtils.coloredText("=== Player Debug ===", COLOR_HEADER));
        lines.add(createLabelValue("Name", data.playerName));
        lines.add(createLabelValue("Position", String.format("(%.2f, %.2f, %.2f)", 
            data.position.x, data.position.y, data.position.z)));
        lines.add(createLabelValue("Health", String.format("%.1f / %.1f", data.health, data.maxHealth)));
        lines.add(createLabelValue("Food", String.valueOf(data.foodLevel)));
        lines.add(createLabelValue("Saturation", String.format("%.1f", data.saturation)));
        
        // Player data details (may be empty on client side)
        boolean hasPlayerData = data.playerDataNBT != null && !data.playerDataNBT.isEmpty();
        if (hasPlayerData) {
            lines.add(ColorUtils.coloredText("--- Player Data ---", COLOR_SEPARATOR));
            
            // Spell data
            if (data.playerDataNBT.contains("learnedSpellsCount")) {
                lines.add(createLabelValue("Learned Spells", String.valueOf(data.playerDataNBT.getInt("learnedSpellsCount").orElse(0))));
            }
            if (data.playerDataNBT.contains("cooldownsCount")) {
                lines.add(createLabelValue("Active Cooldowns", String.valueOf(data.playerDataNBT.getInt("cooldownsCount").orElse(0))));
            }
            if (data.playerDataNBT.contains("activeHoldSpell")) {
                String activeSpell = data.playerDataNBT.getString("activeHoldSpell").orElse("");
                if (!activeSpell.isEmpty() && !activeSpell.equals("none")) {
                    lines.add(createLabelValue("Active Hold Spell", activeSpell));
                }
            }
            if (data.playerDataNBT.contains("masteryUsesCount")) {
                lines.add(createLabelValue("Mastery Uses", String.valueOf(data.playerDataNBT.getInt("masteryUsesCount").orElse(0))));
            }
            
            // Spell slots
            if (data.playerDataNBT.contains("slotTop")) {
                String slot = data.playerDataNBT.getString("slotTop").orElse("none");
                if (!slot.equals("none")) {
                    lines.add(createLabelValue("Slot Top", slot));
                }
            }
            if (data.playerDataNBT.contains("slotBottom")) {
                String slot = data.playerDataNBT.getString("slotBottom").orElse("none");
                if (!slot.equals("none")) {
                    lines.add(createLabelValue("Slot Bottom", slot));
                }
            }
            if (data.playerDataNBT.contains("slotLeft")) {
                String slot = data.playerDataNBT.getString("slotLeft").orElse("none");
                if (!slot.equals("none")) {
                    lines.add(createLabelValue("Slot Left", slot));
                }
            }
            if (data.playerDataNBT.contains("slotRight")) {
                String slot = data.playerDataNBT.getString("slotRight").orElse("none");
                if (!slot.equals("none")) {
                    lines.add(createLabelValue("Slot Right", slot));
                }
            }
            
            // Identity data
            if (data.playerDataNBT.contains("bloodStatus") || data.playerDataNBT.contains("magicalType")) {
                lines.add(ColorUtils.coloredText("--- Identity Data ---", COLOR_SEPARATOR));
                String bloodStatus = data.playerDataNBT.getString("bloodStatus").orElse("none");
                String magicalType = data.playerDataNBT.getString("magicalType").orElse("none");
                if (!bloodStatus.equals("none")) {
                    try {
                        PlayerIdentityData.BloodStatus status = PlayerIdentityData.BloodStatus.valueOf(bloodStatus);
                        lines.add(createLabelValue("Blood Status", status.getDisplayName()));
                    } catch (IllegalArgumentException e) {
                        lines.add(createLabelValue("Blood Status", bloodStatus));
                    }
                }
                if (!magicalType.equals("none")) {
                    try {
                        PlayerIdentityData.MagicalType type = PlayerIdentityData.MagicalType.valueOf(magicalType);
                        lines.add(createLabelValue("Magical Type", type.getDisplayName()));
                    } catch (IllegalArgumentException e) {
                        lines.add(createLabelValue("Magical Type", magicalType));
                    }
                }
            }
            
            // Wand data
            if (data.playerDataNBT.contains("wandCore") || data.playerDataNBT.contains("wandWood")) {
                lines.add(ColorUtils.coloredText("--- Wand Data ---", COLOR_SEPARATOR));
                String core = data.playerDataNBT.getString("wandCore").orElse("none");
                String wood = data.playerDataNBT.getString("wandWood").orElse("none");
                if (!core.equals("none") || !wood.equals("none")) {
                    lines.add(createLabelValue("Core", core));
                    lines.add(createLabelValue("Wood", wood));
                    if (data.playerDataNBT.contains("wandAttuned")) {
                        lines.add(createLabelValue("Attuned", String.valueOf(data.playerDataNBT.getBoolean("wandAttuned").orElse(false))));
                    }
                    if (data.playerDataNBT.contains("wandOwner")) {
                        String owner = data.playerDataNBT.getString("wandOwner").orElse("");
                        if (!owner.isEmpty()) {
                            lines.add(createLabelValue("Owner", truncateString(owner, 30)));
                        }
                    }
                }
            }
            
            // Show error message if present
            if (data.playerDataNBT.contains("error")) {
                String error = data.playerDataNBT.getString("error").orElse("");
                if (!error.isEmpty()) {
                    lines.add(net.minecraft.network.chat.Component.literal("")
                        .append(ColorUtils.coloredText("Error: ", COLOR_ERROR))
                        .append(ColorUtils.coloredText(error, COLOR_VALUE)));
                }
            }
        } else {
            // On client side, PlayerDataHelper returns empty data
            lines.add(ColorUtils.coloredText("--- Player Data ---", COLOR_SEPARATOR));
            lines.add(net.minecraft.network.chat.Component.literal("")
                .append(ColorUtils.coloredText("Note: ", COLOR_LABEL))
                .append(ColorUtils.coloredText("Custom player data not available on client", COLOR_VALUE)));
            lines.add(ColorUtils.coloredText("(Data syncs from server)", COLOR_LABEL));
        }
        
        // Persistent data keys
        if (!data.persistentDataKeys.isEmpty()) {
            lines.add(ColorUtils.coloredText("--- Persistent Data ---", COLOR_SEPARATOR));
            for (String key : data.persistentDataKeys) {
                lines.add(ColorUtils.coloredText(key, COLOR_LABEL));
            }
        }
        
        return lines;
    }
    
    /**
     * Creates a label-value pair component with proper coloring.
     */
    private static Component createLabelValue(String label, String value) {
        return net.minecraft.network.chat.Component.literal("")
            .append(ColorUtils.coloredText(label + ": ", COLOR_LABEL))
            .append(ColorUtils.coloredText(value, COLOR_VALUE));
    }
    
    /**
     * Checks if debug tooltips are enabled.
     */
    private static boolean isTooltipEnabled() {
        boolean configEnabled = Config.isDebugTooltipsEnabled();
        boolean verboseEnabled = Config.isVerboseLoggingEnabled();
        // Tooltip must be enabled via toggle AND (config OR verbose logging)
        return tooltipEnabled && (configEnabled || verboseEnabled);
    }
    
    /**
     * Formats NBT data for display.
     */
    private static List<String> formatNBT(net.minecraft.nbt.CompoundTag nbt, int maxLines) {
        List<String> lines = new java.util.ArrayList<>();
        if (nbt == null || nbt.isEmpty()) {
            lines.add("(empty)");
            return lines;
        }
        
        String nbtString = nbt.toString();
        String[] parts = nbtString.split("\n");
        
        int linesAdded = 0;
        for (String part : parts) {
            if (linesAdded >= maxLines) {
                lines.add("... (truncated)");
                break;
            }
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                lines.add(truncateString(trimmed, 60));
                linesAdded++;
            }
        }
        
        if (lines.isEmpty()) {
            String displayString = nbtString.length() > 60 ? truncateString(nbtString, 60) : nbtString;
            lines.add(displayString);
        }
        
        return lines;
    }
    
    /**
     * Truncates a string to a maximum length.
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
}
