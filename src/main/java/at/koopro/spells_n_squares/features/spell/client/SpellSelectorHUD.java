package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.client.KeyStateTracker;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.core.util.text.StringUtils;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

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
    
    // Track left mouse button state for shoot detection
    private static boolean lastLeftMouseState = false;

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
        
        // Check main hand and offhand for wand items
        net.minecraft.world.item.ItemStack mainHand = mc.player.getMainHandItem();
        net.minecraft.world.item.ItemStack offHand = mc.player.getOffhandItem();
        
        // Check by tag first
        if (!mainHand.isEmpty() && mainHand.is(ModTags.WANDS)) {
            return true;
        }
        if (!offHand.isEmpty() && offHand.is(ModTags.WANDS)) {
            return true;
        }
        
        // Also check by direct item reference as fallback
        if (!mainHand.isEmpty() && mainHand.is(at.koopro.spells_n_squares.features.wand.registry.WandRegistry.DEMO_WAND.get())) {
            return true;
        }
        if (!offHand.isEmpty() && offHand.is(at.koopro.spells_n_squares.features.wand.registry.WandRegistry.DEMO_WAND.get())) {
            return true;
        }
        
        return false;
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

        SafeEventHandler.execute(() -> {
            // Only process spell-related inputs when holding a wand
            if (!isHoldingWand(mc)) {
                // Reset mouse state tracker when not holding wand
                lastLeftMouseState = false;
                return;
            }

            // Tick client-side cooldowns
            ClientSpellData.tickCooldowns();

            // Detect key press (transition from not pressed to pressed) and switch selection
            if (keyTracker.wasJustPressed(at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_TOP)) {
                selectedField = SpellManager.SLOT_TOP;
                ClientSpellData.setSelectedSlot(SpellManager.SLOT_TOP);
            } else if (keyTracker.wasJustPressed(at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_BOTTOM)) {
                selectedField = SpellManager.SLOT_BOTTOM;
                ClientSpellData.setSelectedSlot(SpellManager.SLOT_BOTTOM);
            } else if (keyTracker.wasJustPressed(at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_LEFT)) {
                selectedField = SpellManager.SLOT_LEFT;
                ClientSpellData.setSelectedSlot(SpellManager.SLOT_LEFT);
            } else if (keyTracker.wasJustPressed(at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_RIGHT)) {
                selectedField = SpellManager.SLOT_RIGHT;
                ClientSpellData.setSelectedSlot(SpellManager.SLOT_RIGHT);
            }

            // Handle spell casting
            KeyMapping castKey = at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_CAST;
            boolean isCastKeyPressed = castKey.isDown();
            boolean wasCastKeyJustPressed = keyTracker.wasJustPressed(castKey);
            
            // Track left mouse button state for shoot detection
            KeyMapping attackKey = mc.options.keyAttack;
            boolean currentLeftMouseState = attackKey.isDown();
            
            if (mc.player != null && mc.level != null) {
                int slotToCast = ClientSpellData.getSelectedSlot();
                Identifier spellId = ClientSpellData.getSpellInSlot(slotToCast);
                
                if (spellId != null) {
                    Spell spell = SpellRegistry.get(spellId);
                    
                    // Check if this is a hold-to-cast spell
                    if (spell != null && spell.isHoldToCast()) {
                        // Handle hold-to-cast spells
                        if (wasCastKeyJustPressed && !ClientSpellData.isHoldingSpell()) {
                            // Start holding the spell (only if not already holding)
                            at.koopro.spells_n_squares.features.spell.network.SpellCastPayload payload = 
                                new at.koopro.spells_n_squares.features.spell.network.SpellCastPayload(slotToCast);
                            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                            ClientSpellData.setHoldingSpell(true);
                        } else if (!isCastKeyPressed && ClientSpellData.isHoldingSpell()) {
                            // Stop holding the spell
                            at.koopro.spells_n_squares.features.spell.network.SpellCastPayload payload = 
                                new at.koopro.spells_n_squares.features.spell.network.SpellCastPayload(-1); // -1 means stop
                            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                            ClientSpellData.setHoldingSpell(false);
                        }
                        
                        // Detect left-click while holding a spell to shoot entities/blocks away
                        if (currentLeftMouseState && !lastLeftMouseState && ClientSpellData.isHoldingSpell()) {
                            // Left-click detected while holding spell - shoot entities away
                            at.koopro.spells_n_squares.features.spell.network.SpellShootPayload shootPayload = 
                                new at.koopro.spells_n_squares.features.spell.network.SpellShootPayload();
                            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(shootPayload);
                        }
                    } else {
                        // Normal one-time cast spell
                        if (wasCastKeyJustPressed) {
                            at.koopro.spells_n_squares.features.spell.network.SpellCastPayload payload = 
                                new at.koopro.spells_n_squares.features.spell.network.SpellCastPayload(slotToCast);
                            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                        }
                    }
                }
            }
            
            // Update mouse state tracker
            lastLeftMouseState = currentLeftMouseState;
            
            // Handle spell selection screen
            if (keyTracker.wasJustPressed(at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTION_SCREEN) && mc.player != null) {
                mc.setScreen(new SpellSelectionScreen());
            }
        }, "ticking spell selector HUD", mc.player);
    }

    /**
     * Renders the HUD element on the GUI.
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Don't render HUD when a screen is open (except inventory)
            if (mc.screen != null && !(mc.screen instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen)) {
                return;
            }
            
            // Only render HUD when holding a wand
            if (!isHoldingWand(mc)) {
                return;
            }
            
            GuiGraphics guiGraphics = event.getGuiGraphics();

            // Render all HUD elements
            renderHudTexture(guiGraphics, mc);
            renderSpellText(guiGraphics, mc);
            renderSelectionIndicator(guiGraphics);
            renderWandCompatibilityMeter(guiGraphics, mc);
            renderComboIndicators(guiGraphics, mc);
        }, "rendering spell selector HUD", mc.player);
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
        int cooldownTicks = spellId != null ? ClientSpellData.getCooldown(spellId) : 0;
        
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
                    
                    // Render cooldown overlay (dark semi-transparent bar from bottom)
                    if (isOnCooldown && spell.getCooldown() > 0) {
                        float cooldownProgress = 1.0f - ((float) cooldownTicks / (float) spell.getCooldown());
                        int overlayHeight = (int) (iconSize * cooldownProgress);
                        if (overlayHeight > 0 && overlayHeight < iconSize) {
                            int overlayY = iconY + iconSize - overlayHeight;
                            int overlayColor = (int) (SpellUIConstants.COOLDOWN_OVERLAY_ALPHA * 255) << 24 | 0x000000; // Black with alpha
                            guiGraphics.fill(iconX, overlayY, iconX + iconSize, iconY + iconSize, overlayColor);
                        }
                        
                        // Render cooldown progress ring around icon
                        if (cooldownProgress > 0.0f && cooldownProgress < 1.0f) {
                            int ringThickness = 2;
                            int ringColor = cooldownTicks > spell.getCooldown() / 2 
                                ? 0x80FF4444  // Red when more than half cooldown remaining
                                : 0x80FFAA00; // Orange when less than half remaining
                            
                            // Draw progress ring segments (simplified - draws top, right, bottom, left segments)
                            float segments = 8.0f; // 8 segments for smoother ring
                            float angleStep = (float) (Math.PI * 2 / segments);
                            float progressAngle = cooldownProgress * (float) (Math.PI * 2);
                            
                            for (float angle = 0; angle < progressAngle; angle += angleStep) {
                                float startAngle = angle;
                                float endAngle = Math.min(angle + angleStep, progressAngle);
                                
                                // Calculate positions for ring segment
                                int centerX = x;
                                int centerY = y;
                                int radius = iconSize / 2 + ringThickness;
                                
                                // Draw ring segment (simplified line drawing)
                                int x1 = centerX + (int) (Math.cos(startAngle) * radius);
                                int y1 = centerY + (int) (Math.sin(startAngle) * radius);
                                int x2 = centerX + (int) (Math.cos(endAngle) * radius);
                                int y2 = centerY + (int) (Math.sin(endAngle) * radius);
                                
                                // Draw thick line segment
                                guiGraphics.fill(x1 - ringThickness/2, y1 - ringThickness/2, 
                                    x2 + ringThickness/2, y2 + ringThickness/2, ringColor);
                            }
                        }
                    }
                    
                    // Render cooldown time text overlay (centered on icon, no rotation needed)
                    if (isOnCooldown && cooldownTicks > 0) {
                        float cooldownSeconds = cooldownTicks / 20.0f;
                        String cooldownText = cooldownSeconds < 1.0f 
                            ? String.format("%.1f", cooldownSeconds) 
                            : String.format("%.0f", cooldownSeconds);
                        
                        // Color-code based on cooldown status
                        int textColor = cooldownTicks > spell.getCooldown() / 2 
                            ? SpellUIConstants.TEXT_COLOR_COOLDOWN_TIME  // Red when more than half remaining
                            : 0xFFFFAA00; // Orange when less than half remaining
                        
                        // Draw with shadow for better visibility
                        int textX = x - mc.font.width(cooldownText) / 2;
                        int textY = iconY + iconSize / 2 - mc.font.lineHeight / 2;
                        guiGraphics.drawString(mc.font, cooldownText, textX, textY, textColor, true);
                    }
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
        if (StringUtils.isEmpty(text)) {
            return;
        }

        int textWidth = mc.font.width(text);
        int textHeight = mc.font.lineHeight;

        int paddingX = SpellUIConstants.KEYBIND_CHIP_PADDING_X;
        int paddingY = SpellUIConstants.KEYBIND_CHIP_PADDING_Y;

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
            case SpellManager.SLOT_TOP -> at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_TOP;
            case SpellManager.SLOT_BOTTOM -> at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_BOTTOM;
            case SpellManager.SLOT_LEFT -> at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_LEFT;
            case SpellManager.SLOT_RIGHT -> at.koopro.spells_n_squares.core.base.init.client.ModKeybinds.SPELL_SELECTOR_RIGHT;
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
    
    /**
     * Renders a wand compatibility meter showing how well the current wand works with the selected spell.
     */
    private static void renderWandCompatibilityMeter(GuiGraphics guiGraphics, Minecraft mc) {
        if (mc.player == null) {
            return;
        }
        
        // Get current wand from player's hand
        net.minecraft.world.item.ItemStack wandStack = mc.player.getMainHandItem();
        if (wandStack.isEmpty() || !wandStack.is(ModTags.WANDS)) {
            wandStack = mc.player.getOffhandItem();
            if (wandStack.isEmpty() || !wandStack.is(ModTags.WANDS)) {
                return; // No wand in hand
            }
        }
        
        // Get selected spell
        int selectedSlot = ClientSpellData.getSelectedSlot();
        Identifier spellId = ClientSpellData.getSpellInSlot(selectedSlot);
        if (spellId == null) {
            return;
        }
        
        // Calculate compatibility
        float compatibility = at.koopro.spells_n_squares.features.wand.compatibility.WandCompatibilityHelper.calculateCompatibility(
            wandStack, spellId);
        
        // Render compatibility bar (small bar above HUD)
        int hudX = getHudX(mc);
        int hudY = getHudY(mc);
        int barX = hudX + HUD_SIZE / 2 - 20; // Center of HUD, offset for bar width
        int barY = hudY - 8; // Above HUD
        
        int barWidth = 40;
        int barHeight = 4;
        
        // Background
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);
        
        // Compatibility fill (color-coded: red=low, yellow=medium, green=high)
        int fillWidth = (int) (barWidth * compatibility);
        int fillColor;
        if (compatibility < 0.4f) {
            fillColor = 0xFF0000; // Red
        } else if (compatibility < 0.7f) {
            fillColor = 0xFFFF00; // Yellow
        } else {
            fillColor = 0x00FF00; // Green
        }
        guiGraphics.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFF000000 | fillColor);
        
        // Border
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 1, 0xFFFFFFFF); // Top
        guiGraphics.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFFFFFFFF); // Bottom
        guiGraphics.fill(barX, barY, barX + 1, barY + barHeight, 0xFFFFFFFF); // Left
        guiGraphics.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFFFFFFFF); // Right
    }
    
    /**
     * Renders spell combo indicators when a combo is active.
     */
    private static void renderComboIndicators(GuiGraphics guiGraphics, Minecraft mc) {
        if (mc.player == null) {
            return;
        }
        
        // Check for active combos (recent spell casts)
        java.util.List<at.koopro.spells_n_squares.features.spell.combo.SpellComboSystem.ComboEntry> comboHistory = 
            at.koopro.spells_n_squares.features.spell.combo.SpellComboSystem.getComboHistory(mc.player);
        
        if (comboHistory == null || comboHistory.isEmpty()) {
            return;
        }
        
        // Check if there's an active combo (recent enough)
        long currentTick = mc.level != null ? mc.level.getGameTime() : 0;
        long recentThreshold = 100; // 5 seconds
        boolean hasActiveCombo = comboHistory.stream()
            .anyMatch(entry -> currentTick - entry.timestamp() < recentThreshold);
        
        if (!hasActiveCombo) {
            return;
        }
        
        // Render combo indicator (small icon/text next to HUD)
        int hudX = getHudX(mc);
        int hudY = getHudY(mc);
        int comboX = hudX + HUD_SIZE + 4; // To the right of HUD
        int comboY = hudY + HUD_SIZE / 2 - 8; // Vertically centered
        
        // Render combo count badge
        String comboText = "x" + comboHistory.size();
        int textWidth = mc.font.width(comboText);
        int textHeight = mc.font.lineHeight;
        int padding = 4;
        
        int bgX = comboX;
        int bgY = comboY - textHeight / 2;
        int bgWidth = textWidth + padding * 2;
        int bgHeight = textHeight + padding * 2;
        
        // Background with glow effect
        int bgColor = 0xAAFFD700; // Gold with transparency
        guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, bgColor);
        
        // Border
        int borderColor = 0xFFFFD700; // Gold
        guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + 1, borderColor); // Top
        guiGraphics.fill(bgX, bgY + bgHeight - 1, bgX + bgWidth, bgY + bgHeight, borderColor); // Bottom
        guiGraphics.fill(bgX, bgY, bgX + 1, bgY + bgHeight, borderColor); // Left
        guiGraphics.fill(bgX + bgWidth - 1, bgY, bgX + bgWidth, bgY + bgHeight, borderColor); // Right
        
        // Text
        int textX = bgX + padding;
        int textY = bgY + padding;
        guiGraphics.drawString(mc.font, comboText, textX, textY, 0xFFFFFFFF, true);
    }
}

