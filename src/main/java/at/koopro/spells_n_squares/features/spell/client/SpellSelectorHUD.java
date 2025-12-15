package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
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

import at.koopro.spells_n_squares.client.ModKeybinds;
import at.koopro.spells_n_squares.core.network.SpellCastPayload;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.SpellRegistry;

/**
 * Renders a spell selector HUD element that displays movement keybinds (W, S, A, D)
 * and allows switching between them. The selected field is highlighted with a selection texture.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class SpellSelectorHUD {
    // Selection state enum values: 0 = Top, 1 = Bottom, 2 = Left, 3 = Right
    private static final int FIELD_TOP = 0;
    private static final int FIELD_BOTTOM = 1;
    private static final int FIELD_LEFT = 2;
    private static final int FIELD_RIGHT = 3;

    // Current selected field (defaults to top)
    private static int selectedField = FIELD_TOP;

    // Track previous key states to detect key presses (not just held down)
    private static boolean lastForwardPressed = false;
    private static boolean lastBackPressed = false;
    private static boolean lastLeftPressed = false;
    private static boolean lastRightPressed = false;
    private static boolean lastCastPressed = false;
    private static boolean lastSelectionScreenPressed = false;

    // Texture identifiers
    private static final Identifier HUD_TEXTURE = Identifier.fromNamespaceAndPath(
        SpellsNSquares.MODID,
        "textures/gui/container/hud_element.png"
    );

    private static final Identifier SELECT_TEXTURE = Identifier.fromNamespaceAndPath(
        SpellsNSquares.MODID,
        "textures/gui/container/select.png"
    );

    // HUD element base size (actual position is computed dynamically each frame)
    private static final int HUD_X = 10; // kept for reference, no longer used directly
    private static final int HUD_Y = 80; // kept for reference, no longer used directly
    private static final int HUD_SIZE = 64;
    private static final int SELECT_SIZE = 32; // Half of HUD_SIZE

    // Dynamic HUD placement helpers (anchored to the right of the hotbar)
    private static int getHudX(Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int hotbarWidth = 182; // vanilla hotbar width in px
        int hotbarX = (screenWidth - hotbarWidth) / 2;
        int offset = 4; // small gap to the right of the hotbar
        return hotbarX + hotbarWidth + offset;
    }

    private static int getHudY(Minecraft mc) {
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        // Align vertically with the hotbar region near the bottom
        int hotbarHeight = 22;
        int bottomMargin = 4;
        return screenHeight - hotbarHeight - bottomMargin - HUD_SIZE;
    }
    
    /**
     * Checks if the player is currently holding a wand item.
     */
    private static boolean isHoldingWand(Minecraft mc) {
        if (mc.player == null) {
            return false;
        }
        ItemStack mainHand = mc.player.getMainHandItem();
        ItemStack offHand = mc.player.getOffhandItem();
        return (!mainHand.isEmpty() && mainHand.is(ModTags.WANDS)) || 
               (!offHand.isEmpty() && offHand.is(ModTags.WANDS));
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

        // Check current key states using custom keybinds
        boolean topPressed = ModKeybinds.SPELL_SELECTOR_TOP.isDown();
        boolean bottomPressed = ModKeybinds.SPELL_SELECTOR_BOTTOM.isDown();
        boolean leftPressed = ModKeybinds.SPELL_SELECTOR_LEFT.isDown();
        boolean rightPressed = ModKeybinds.SPELL_SELECTOR_RIGHT.isDown();

        // Detect key press (transition from not pressed to pressed) and switch selection
        if (topPressed && !lastForwardPressed) {
            selectedField = FIELD_TOP;
        } else if (bottomPressed && !lastBackPressed) {
            selectedField = FIELD_BOTTOM;
        } else if (leftPressed && !lastLeftPressed) {
            selectedField = FIELD_LEFT;
        } else if (rightPressed && !lastRightPressed) {
            selectedField = FIELD_RIGHT;
        }

        // Handle spell casting
        boolean castPressed = ModKeybinds.SPELL_CAST.isDown();
        if (castPressed && !lastCastPressed && mc.player != null && mc.level != null) {
            // Send spell cast request to server via network packet
            // This works for both single-player (integrated server) and multiplayer
            SpellCastPayload payload = new SpellCastPayload(selectedField);
            ClientPacketDistributor.sendToServer(payload);
        }
        
        // Handle spell selection screen
        boolean selectionScreenPressed = ModKeybinds.SPELL_SELECTION_SCREEN.isDown();
        if (selectionScreenPressed && !lastSelectionScreenPressed && mc.player != null) {
            mc.setScreen(new SpellSelectionScreen());
        }
        
        // Update previous states
        lastForwardPressed = topPressed;
        lastBackPressed = bottomPressed;
        lastLeftPressed = leftPressed;
        lastRightPressed = rightPressed;
        lastCastPressed = castPressed;
        lastSelectionScreenPressed = selectionScreenPressed;
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
        final int textColor = 0xFFFFFF00; // Bright yellow
        final int cooldownColor = 0xFF808080; // Gray for cooldown

        int hudX = getHudX(mc);
        int hudY = getHudY(mc);

        int centerX = hudX + HUD_SIZE / 2;
        int topY = hudY + 12;
        int bottomY = hudY + 52;
        int leftX = hudX + 16;
        int rightX = hudX + 48;
        int middleY = hudY + 28;
        
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
                int iconSize = 16; // 16x16 icon size
                int iconX = x - iconSize / 2;
                int iconY = y - iconSize / 2;
                
                // Check if texture exists (will gracefully fail if missing)
                try {
                    // Render icon with cooldown tinting (gray overlay when on cooldown)
                    int tint = isOnCooldown ? 0xFF808080 : 0xFFFFFFFF;
                    
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
                    int bgColor = 0x80000000;       // semi-transparent black
                    int borderColor = 0xCCFFFFAA;   // soft yellow border
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

        int bgColor = 0x80000000;       // semi-transparent black
        int borderColor = 0xCCFFFFFF;   // soft white border
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
        return switch (slot) {
            case SpellManager.SLOT_TOP -> ModKeybinds.SPELL_SELECTOR_TOP;
            case SpellManager.SLOT_BOTTOM -> ModKeybinds.SPELL_SELECTOR_BOTTOM;
            case SpellManager.SLOT_LEFT -> ModKeybinds.SPELL_SELECTOR_LEFT;
            case SpellManager.SLOT_RIGHT -> ModKeybinds.SPELL_SELECTOR_RIGHT;
            default -> null;
        };
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
        int topY = hudY + 12;
        int bottomY = hudY + 52;
        int leftX = hudX + 16;
        int rightX = hudX + 48;
        int middleY = hudY + 28;

        int selectX, selectY;
        
        switch (selectedField) {
            case FIELD_TOP:
                selectX = centerX - SELECT_SIZE / 2;
                selectY = topY + 3 - SELECT_SIZE / 2; // Adjusted for visual alignment
                break;
            case FIELD_BOTTOM:
                selectX = centerX - SELECT_SIZE / 2;
                selectY = bottomY - 3 - SELECT_SIZE / 2; // Adjusted for visual alignment
                break;
            case FIELD_LEFT:
                selectX = leftX - 1 - SELECT_SIZE / 2; // Adjusted for visual alignment
                selectY = middleY + 4 - SELECT_SIZE / 2;
                break;
            case FIELD_RIGHT:
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

