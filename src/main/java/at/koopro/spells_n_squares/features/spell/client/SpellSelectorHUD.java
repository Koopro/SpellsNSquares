package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.client.KeyStateTracker;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Renders a spell selector HUD element that displays movement keybinds (W, S, A, D)
 * and allows switching between them. The selected field is highlighted with a selection texture.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class SpellSelectorHUD {
    // Current selected field (defaults to top) - uses SpellManager slot constants
    private static int selectedField = SpellManager.SLOT_TOP;

    // Key state tracker for detecting key presses
    private static final KeyStateTracker keyTracker = new KeyStateTracker();

    // Texture identifiers
    private static final Identifier HUD_TEXTURE = ModIdentifierHelper.modId("textures/gui/container/hud_element.png");

    private static final Identifier SELECT_TEXTURE = ModIdentifierHelper.modId("textures/gui/container/select.png");

    // HUD element base size (actual position is computed dynamically each frame)
    private static final int HUD_SIZE = 64;
    private static final int SELECT_SIZE = 32; // Half of HUD_SIZE

    // Hotbar dimensions for HUD positioning
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int HOTBAR_OFFSET = 4;
    private static final int BOTTOM_MARGIN = 4;
    
    // HUD slot position offsets (relative to hudX/hudY)
    private static final int SLOT_TOP_Y_OFFSET = 12;
    private static final int SLOT_BOTTOM_Y_OFFSET = 52;
    private static final int SLOT_LEFT_X_OFFSET = 16;
    private static final int SLOT_RIGHT_X_OFFSET = 48;
    private static final int SLOT_MIDDLE_Y_OFFSET = 28;
    
    // Dynamic HUD placement helpers (anchored to the right of the hotbar)
    private static int getHudX(Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int hotbarX = (screenWidth - HOTBAR_WIDTH) / 2;
        return hotbarX + HOTBAR_WIDTH + HOTBAR_OFFSET;
    }

    private static int getHudY(Minecraft mc) {
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        // Align vertically with the hotbar region near the bottom
        return screenHeight - HOTBAR_HEIGHT - BOTTOM_MARGIN - HUD_SIZE;
    }
    
    /**
     * Checks if the player is currently holding a wand item.
     */
    private static boolean isHoldingWand(Minecraft mc) {
        if (mc.player == null) {
            return false;
        }
        return at.koopro.spells_n_squares.core.util.PlayerItemUtils.isHoldingItemByTag(mc.player, ModTags.WANDS);
    }
    
    /**
     * Handles key press detection to switch between fields.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        // Only process spell-related inputs when holding a wand
        if (!isHoldingWand(mc)) {
            return;
        }

        // Tick client-side cooldowns
        ClientSpellData.tickCooldowns();

        // TODO: Re-enable when ModKeybinds and SpellCastPayload are implemented
        // Detect key press (transition from not pressed to pressed) and switch selection
        /*
        if (keyTracker.wasJustPressed(ModKeybinds.SPELL_SELECTOR_TOP)) {
            selectedField = SpellManager.SLOT_TOP;
        } else if (keyTracker.wasJustPressed(ModKeybinds.SPELL_SELECTOR_BOTTOM)) {
            selectedField = SpellManager.SLOT_BOTTOM;
        } else if (keyTracker.wasJustPressed(ModKeybinds.SPELL_SELECTOR_LEFT)) {
            selectedField = SpellManager.SLOT_LEFT;
        } else if (keyTracker.wasJustPressed(ModKeybinds.SPELL_SELECTOR_RIGHT)) {
            selectedField = SpellManager.SLOT_RIGHT;
        }

        // Handle spell casting
        if (keyTracker.wasJustPressed(ModKeybinds.SPELL_CAST) && mc.player != null && mc.level != null) {
            // Send spell cast request to server via network packet
            // This works for both single-player (integrated server) and multiplayer
            SpellCastPayload payload = new SpellCastPayload(selectedField);
            ClientPacketDistributor.sendToServer(payload);
        }
        
        // Handle spell selection screen
        if (keyTracker.wasJustPressed(ModKeybinds.SPELL_SELECTION_SCREEN) && mc.player != null) {
            mc.setScreen(new SpellSelectionScreen());
        }
        */
    }

    /**
     * Renders the HUD element on the appropriate GUI layer.
     */
    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        // Only render HUD when holding a wand
        if (!isHoldingWand(mc)) {
            return;
        }
        
        Identifier layerName = event.getName();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        // Render base HUD texture on CROSSHAIR and HOTBAR layers
        if (layerName == VanillaGuiLayers.CROSSHAIR || layerName == VanillaGuiLayers.HOTBAR) {
            renderHudTexture(guiGraphics, mc);
        }
        
        // Render text and selection indicator on HOTBAR layer only
        if (layerName == VanillaGuiLayers.HOTBAR) {
            renderSpellText(guiGraphics, mc);
            renderSelectionIndicator(guiGraphics);
        }
    }
    
    /**
     * Renders the base HUD texture element.
     */
    private static void renderHudTexture(GuiGraphics guiGraphics, Minecraft mc) {
        int hudX = getHudX(mc);
        int hudY = getHudY(mc);

        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            HUD_TEXTURE,
            hudX, hudY,
            0.0f, 0.0f,
            HUD_SIZE, HUD_SIZE,
            HUD_SIZE, HUD_SIZE,
            HUD_SIZE, HUD_SIZE,
            -1
        );
    }
    
    /**
     * Renders spell icons and keybinds in each field.
     * Icons are displayed when a spell is assigned, otherwise keybinds are shown.
     */
    private static void renderSpellText(GuiGraphics guiGraphics, Minecraft mc) {
        final int textColor = SpellUIConstants.TEXT_COLOR_NORMAL;
        final int cooldownColor = SpellUIConstants.TEXT_COLOR_COOLDOWN;

        int hudX = getHudX(mc);
        int hudY = getHudY(mc);

        int centerX = hudX + HUD_SIZE / 2;
        int topY = hudY + SLOT_TOP_Y_OFFSET;
        int bottomY = hudY + SLOT_BOTTOM_Y_OFFSET;
        int leftX = hudX + SLOT_LEFT_X_OFFSET;
        int rightX = hudX + SLOT_RIGHT_X_OFFSET;
        int middleY = hudY + SLOT_MIDDLE_Y_OFFSET;
        
        // Render spell icons/keybinds for each slot
        renderSpellInSlot(guiGraphics, mc, SpellManager.SLOT_TOP, centerX, topY, textColor, cooldownColor);
        renderSpellInSlot(guiGraphics, mc, SpellManager.SLOT_BOTTOM, centerX, bottomY, textColor, cooldownColor);
        renderSpellInSlot(guiGraphics, mc, SpellManager.SLOT_LEFT, leftX, middleY, textColor, cooldownColor);
        renderSpellInSlot(guiGraphics, mc, SpellManager.SLOT_RIGHT, rightX, middleY, textColor, cooldownColor);
    }
    
    /**
     * Renders the spell icon or keybind for a specific slot.
     * If a spell is assigned, displays the spell icon. Otherwise, displays the keybind.
     * Applies cooldown tinting if the spell in this slot is on cooldown.
     * The keybind is rendered inside a small "chip" with background + border for a nicer look.
     */
    private static void renderSpellInSlot(GuiGraphics guiGraphics, Minecraft mc, int slot, int x, int y, int normalColor, int cooldownColor) {
        if (mc.player == null) {
            return;
        }
        
        Identifier spellId = ClientSpellData.getSpellInSlot(slot);
        boolean isOnCooldown = spellId != null && ClientSpellData.isOnCooldown(spellId);
        
        // If a spell is assigned, render its icon
        if (spellId != null) {
            Spell spell = SpellRegistry.get(spellId);
            if (spell != null) {
                Identifier iconTexture = spell.getIcon();
                int iconSize = SpellUIConstants.ICON_SIZE_HUD;
                int iconX = x - iconSize / 2;
                int iconY = y - iconSize / 2;
                
                // Check if texture exists (will gracefully fail if missing)
                try {
                    // Render icon with cooldown tinting (gray overlay when on cooldown)
                    int tint = isOnCooldown ? SpellUIConstants.TINT_COOLDOWN : SpellUIConstants.TINT_NORMAL;
                    
                    guiGraphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        iconTexture,
                        iconX, iconY,
                        0.0f, 0.0f,
                        iconSize, iconSize,
                        iconSize, iconSize,
                        iconSize, iconSize,
                        tint
                    );
                } catch (Exception e) {
                    // Texture not found or error loading - fall back to text display
                    // This allows the mod to work even if icons aren't created yet
                }
                
                // Also show keybind as small "chip" below icon
                KeyMapping keybind = getKeybindForSlot(slot);
                if (keybind != null) {
                    String keyText = getKeybindDisplayName(keybind);
                    int textY = iconY + iconSize + 2;
                    int textColor = isOnCooldown ? cooldownColor : normalColor;

                    // Slightly transparent dark background with brighter border
                    int bgColor = SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT;
                    int borderColor = SpellUIConstants.BORDER_COLOR_KEYBIND;
                    drawKeybindChip(guiGraphics, mc, keyText, x, textY, textColor, bgColor, borderColor);
                }
                return;
            }
        }
        
        // No spell assigned, show keybind only (as centered "chip")
        KeyMapping keybind = getKeybindForSlot(slot);
        String displayText;
        if (keybind != null) {
            displayText = getKeybindDisplayName(keybind);
        } else {
            displayText = "";
        }

        int bgColor = SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT;
        int borderColor = SpellUIConstants.BORDER_COLOR_KEYBIND_WHITE;
        drawKeybindChip(guiGraphics, mc, displayText, x, y, normalColor, bgColor, borderColor);
    }

    /**
     * Draws a small rounded-style "chip" for a keybind: background rect + border + centered text.
     * (Not truly rounded, but visually close enough and cheap to render.)
     */
    private static void drawKeybindChip(
        GuiGraphics guiGraphics,
        Minecraft mc,
        String text,
        int centerX,
        int centerY,
        int textColor,
        int bgColor,
        int borderColor
    ) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int textWidth = mc.font.width(text);
        int textHeight = mc.font.lineHeight;

        int paddingX = 4;
        int paddingY = 2;

        int left = centerX - textWidth / 2 - paddingX;
        int right = centerX + textWidth / 2 + paddingX;
        int top = centerY - textHeight / 2 - paddingY;
        int bottom = centerY + textHeight / 2 + paddingY;

        // Background
        guiGraphics.fill(left, top, right, bottom, bgColor);

        // Pseudo-rounded 1px border: inset corners slightly so it feels softer
        int inset = 1;
        // top / bottom lines slightly shorter
        guiGraphics.fill(left + inset, top, right - inset, top + 1, borderColor);         // top
        guiGraphics.fill(left + inset, bottom - 1, right - inset, bottom, borderColor);   // bottom
        // left / right lines with small gaps at the corners
        guiGraphics.fill(left, top + inset, left + 1, bottom - inset, borderColor);       // left
        guiGraphics.fill(right - 1, top + inset, right, bottom - inset, borderColor);     // right

        // Text
        int textX = centerX - textWidth / 2;
        int textY = centerY - textHeight / 2 + 1; // small tweak so it looks centered
        guiGraphics.drawString(mc.font, text, textX, textY, textColor, true);
    }
    
    /**
     * Gets the keybind for a specific slot.
     */
    private static KeyMapping getKeybindForSlot(int slot) {
        // TODO: Re-enable when ModKeybinds is implemented
        return null;
        /*
        return switch (slot) {
            case SpellManager.SLOT_TOP -> ModKeybinds.SPELL_SELECTOR_TOP;
            case SpellManager.SLOT_BOTTOM -> ModKeybinds.SPELL_SELECTOR_BOTTOM;
            case SpellManager.SLOT_LEFT -> ModKeybinds.SPELL_SELECTOR_LEFT;
            case SpellManager.SLOT_RIGHT -> ModKeybinds.SPELL_SELECTOR_RIGHT;
            default -> null;
        };
        */
    }
    
    /**
     * Gets the display name for a keybind, extracting just the key character.
     * Returns a shortened version for display in the HUD.
     */
    private static String getKeybindDisplayName(KeyMapping keybind) {
        String displayName = keybind.getTranslatedKeyMessage().getString();
        
        // Extract just the key name (e.g., "W" from "key.keyboard.w" or "W" from "W")
        // The display name might be something like "W" or "Left Shift" or "Mouse Button 1"
        // We want to show a short version, so take the last part or first character
        
        // If it's a single character or short string, use it as-is
        if (displayName.length() <= 3) {
            return displayName.toUpperCase();
        }
        
        // For longer names, try to extract a meaningful abbreviation
        // Common patterns: "Left Shift" -> "LS", "Right Control" -> "RC", etc.
        if (displayName.contains("Shift")) {
            return displayName.startsWith("Left") ? "LS" : "RS";
        }
        if (displayName.contains("Control") || displayName.contains("Ctrl")) {
            return displayName.startsWith("Left") ? "LC" : "RC";
        }
        if (displayName.contains("Alt")) {
            return displayName.startsWith("Left") ? "LA" : "RA";
        }
        if (displayName.contains("Mouse")) {
            // Extract button number if present
            String[] parts = displayName.split(" ");
            if (parts.length > 0) {
                try {
                    int buttonNum = Integer.parseInt(parts[parts.length - 1]);
                    return "M" + buttonNum;
                } catch (NumberFormatException e) {
                    return "MB";
                }
            }
            return "MB";
        }
        
        // Default: take first character or first 2 characters
        return displayName.substring(0, Math.min(2, displayName.length())).toUpperCase();
    }
    
    /**
     * Renders the selection indicator texture over the currently selected field.
     */
    private static void renderSelectionIndicator(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        int hudX = getHudX(mc);
        int hudY = getHudY(mc);

        int centerX = hudX + HUD_SIZE / 2;
        int topY = hudY + SLOT_TOP_Y_OFFSET;
        int bottomY = hudY + SLOT_BOTTOM_Y_OFFSET;
        int leftX = hudX + SLOT_LEFT_X_OFFSET;
        int rightX = hudX + SLOT_RIGHT_X_OFFSET;
        int middleY = hudY + SLOT_MIDDLE_Y_OFFSET;

        int selectX, selectY;
        
        switch (selectedField) {
            case SpellManager.SLOT_TOP:
                selectX = centerX - SELECT_SIZE / 2;
                selectY = topY + 3 - SELECT_SIZE / 2; // Adjusted for visual alignment
                break;
            case SpellManager.SLOT_BOTTOM:
                selectX = centerX - SELECT_SIZE / 2;
                selectY = bottomY - 3 - SELECT_SIZE / 2; // Adjusted for visual alignment
                break;
            case SpellManager.SLOT_LEFT:
                selectX = leftX - 1 - SELECT_SIZE / 2; // Adjusted for visual alignment
                selectY = middleY + 4 - SELECT_SIZE / 2;
                break;
            case SpellManager.SLOT_RIGHT:
                selectX = rightX + 1 - SELECT_SIZE / 2; // Adjusted for visual alignment
                selectY = middleY + 4 - SELECT_SIZE / 2;
                break;
            default:
                selectX = centerX - SELECT_SIZE / 2;
                selectY = topY + 3 - SELECT_SIZE / 2;
                break;
        }
        
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            SELECT_TEXTURE,
            selectX, selectY,
            0.0f, 0.0f,
            SELECT_SIZE, SELECT_SIZE,
            SELECT_SIZE, SELECT_SIZE,
            SELECT_SIZE, SELECT_SIZE,
            -1
        );
    }
}

