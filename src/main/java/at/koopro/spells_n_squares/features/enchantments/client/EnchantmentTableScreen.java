package at.koopro.spells_n_squares.features.enchantments.client;

import at.koopro.spells_n_squares.core.client.gui.BaseModScreen;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentTableMenu;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentTableBlockEntity;
import at.koopro.spells_n_squares.features.enchantments.network.EnchantmentRequestPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * GUI screen for the Enchantment Table block.
 * Allows players to enchant items with magical properties.
 */
public class EnchantmentTableScreen extends BaseModScreen<EnchantmentTableMenu> {
    private static final Identifier ENCHANTMENT_TABLE_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/enchantment_table.png");
    
    private Button enchantButton;
    
    public EnchantmentTableScreen(EnchantmentTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected Identifier getBackgroundTexture() {
        return ENCHANTMENT_TABLE_GUI_TEXTURE;
    }
    
    @Override
    protected int getImageWidth() {
        return 176;
    }
    
    @Override
    protected int getImageHeight() {
        return 166;
    }
    
    @Override
    protected void initCustomWidgets() {
        int centerX = getCenterX();
        int buttonY = this.topPos + 60;
        
        // Enchant button
        this.enchantButton = Button.builder(
            Component.translatable("gui.spells_n_squares.enchantment_table.enchant"),
            button -> {
                if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null) {
                    var tablePos = menu.getTablePos();
                    if (this.minecraft.level.getBlockEntity(tablePos) instanceof EnchantmentTableBlockEntity blockEntity) {
                        var enchantmentId = blockEntity.getSelectedEnchantmentId();
                        var enchantmentLevel = blockEntity.getSelectedEnchantmentLevel();
                        
                        if (enchantmentId != null && enchantmentLevel > 0) {
                            // Send enchantment request to server
                            var payload = new EnchantmentRequestPayload(tablePos, enchantmentId, enchantmentLevel);
                            ClientPacketDistributor.sendToServer(payload);
                        } else {
                            this.minecraft.gui.getChat().addMessage(
                                ColorUtils.coloredText("Please select an enchantment first!", ColorUtils.SPELL_RED)
                            );
                        }
                    }
                }
            }
        ).bounds(centerX - 40, buttonY, 80, 20).build();
        this.addRenderableWidget(enchantButton);
    }
    
    @Override
    protected void renderStandardLabels(GuiGraphics guiGraphics) {
        super.renderStandardLabels(guiGraphics);
        
        // Render enchantment info
        int infoY = this.topPos + 20;
        Component info = ColorUtils.coloredText("Place item to enchant", ColorUtils.SPELL_GOLD);
        guiGraphics.drawString(this.font, info, this.leftPos + 8, infoY, ColorUtils.SPELL_WHITE, false);
        
        // Render hint
        int hintY = infoY + 12;
        Component hint = ColorUtils.coloredText("Select an enchantment and click Enchant", ColorUtils.rgb(170, 170, 170));
        guiGraphics.drawString(this.font, hint, this.leftPos + 8, hintY, ColorUtils.SPELL_WHITE, false);
        
        // Render combination hint
        int comboY = hintY + 12;
        Component comboHint = ColorUtils.coloredText("Tip: Some enchantments combine for special effects!", 
            ColorUtils.SPELL_PURPLE);
        guiGraphics.drawString(this.font, comboHint, this.leftPos + 8, comboY, ColorUtils.SPELL_WHITE, false);
        
        // Check if item in slot has enchantments and show count
        if (this.menu != null && this.menu.getCarried().isEmpty()) {
            var itemStack = this.menu.getSlot(0).getItem();
            if (!itemStack.isEmpty()) {
                int enchantCount = at.koopro.spells_n_squares.features.enchantments.EnchantmentHelper.getEnchantmentCount(itemStack);
                if (enchantCount > 0) {
                    int countY = comboY + 12;
                    Component countText = ColorUtils.coloredText(
                        "Enchantments: " + enchantCount, ColorUtils.SPELL_BLUE);
                    guiGraphics.drawString(this.font, countText, this.leftPos + 8, countY, ColorUtils.SPELL_WHITE, false);
                }
            }
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render tooltip for enchant button
        if (enchantButton != null && enchantButton.isMouseOver(mouseX, mouseY)) {
            Component tooltip = Component.translatable("gui.spells_n_squares.enchantment_table.enchant_tooltip");
            List<ClientTooltipComponent> tooltipComponents = List.of(ClientTooltipComponent.create(tooltip.getVisualOrderText()));
            guiGraphics.renderTooltip(this.font, tooltipComponents, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }
}

