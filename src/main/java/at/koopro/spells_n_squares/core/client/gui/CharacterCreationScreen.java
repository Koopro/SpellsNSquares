package at.koopro.spells_n_squares.core.client.gui;

import at.koopro.spells_n_squares.core.data.PlayerIdentityData;
import at.koopro.spells_n_squares.core.data.PlayerIdentityHelper;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Character creation screen for selecting blood status and magical race/type.
 * Only shown to new players who haven't set their identity yet.
 */
public class CharacterCreationScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 25;
    private static final int DESCRIPTION_Y_OFFSET = 5;
    
    private PlayerIdentityData.BloodStatus selectedBloodStatus = PlayerIdentityData.BloodStatus.HALF_BLOOD;
    private PlayerIdentityData.MagicalType selectedMagicalType = PlayerIdentityData.MagicalType.WIZARD;
    
    private Button confirmButton;
    private final List<Button> bloodStatusButtons = new ArrayList<>();
    private final List<Button> magicalTypeButtons = new ArrayList<>();
    
    public CharacterCreationScreen() {
        super(Component.translatable("gui.spells_n_squares.character_creation.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Title
        int titleY = startY - 30;
        
        // Blood Status Section
        int bloodStatusY = startY + 20;
        this.addRenderableWidget(Button.builder(
            Component.literal("Blood Status:").withStyle(style -> style.withColor(ColorUtils.SPELL_GOLD)),
            button -> {}
        ).bounds(centerX - BUTTON_WIDTH / 2, bloodStatusY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        int bloodStatusButtonY = bloodStatusY + BUTTON_HEIGHT + 10;
        int buttonX = centerX - (BUTTON_WIDTH * 2 + 10);
        
        for (PlayerIdentityData.BloodStatus status : PlayerIdentityData.BloodStatus.values()) {
            Button button = Button.builder(
                Component.literal(status.getDisplayName()),
                btn -> {
                    selectedBloodStatus = status;
                    updateButtons();
                    // Validate: if Squib blood status, force Squib type
                    if (status == PlayerIdentityData.BloodStatus.SQUIB) {
                        selectedMagicalType = PlayerIdentityData.MagicalType.SQUIB;
                        updateButtons();
                    }
                }
            ).bounds(buttonX, bloodStatusButtonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            bloodStatusButtons.add(button);
            this.addRenderableWidget(button);
            
            buttonX += BUTTON_WIDTH + 10;
            if (buttonX > centerX + BUTTON_WIDTH / 2) {
                buttonX = centerX - (BUTTON_WIDTH * 2 + 10);
                bloodStatusButtonY += BUTTON_HEIGHT + 5;
            }
        }
        
        // Magical Type Section
        int magicalTypeY = bloodStatusButtonY + BUTTON_HEIGHT + 30;
        this.addRenderableWidget(Button.builder(
            Component.literal("Magical Race/Type:").withStyle(style -> style.withColor(ColorUtils.SPELL_GOLD)),
            button -> {}
        ).bounds(centerX - BUTTON_WIDTH / 2, magicalTypeY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        int magicalTypeButtonY = magicalTypeY + BUTTON_HEIGHT + 10;
        buttonX = centerX - (BUTTON_WIDTH * 2 + 10);
        int buttonsPerRow = 0;
        
        for (PlayerIdentityData.MagicalType type : PlayerIdentityData.MagicalType.values()) {
            Button button = Button.builder(
                Component.literal(type.getDisplayName()),
                btn -> {
                    selectedMagicalType = type;
                    updateButtons();
                    // Validate: if Squib type, force Squib blood status
                    if (type == PlayerIdentityData.MagicalType.SQUIB) {
                        selectedBloodStatus = PlayerIdentityData.BloodStatus.SQUIB;
                        updateButtons();
                    }
                }
            ).bounds(buttonX, magicalTypeButtonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            magicalTypeButtons.add(button);
            this.addRenderableWidget(button);
            
            buttonX += BUTTON_WIDTH + 10;
            buttonsPerRow++;
            if (buttonsPerRow >= 4) {
                buttonX = centerX - (BUTTON_WIDTH * 2 + 10);
                magicalTypeButtonY += BUTTON_HEIGHT + 5;
                buttonsPerRow = 0;
            }
        }
        
        // Confirm Button
        int confirmY = magicalTypeButtonY + BUTTON_HEIGHT + 30;
        confirmButton = Button.builder(
            Component.translatable("gui.spells_n_squares.character_creation.confirm"),
            this::onConfirm
        ).bounds(centerX - BUTTON_WIDTH / 2, confirmY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(confirmButton);
        
        updateButtons();
    }
    
    private void updateButtons() {
        // Update blood status buttons
        for (Button button : bloodStatusButtons) {
            Component text = button.getMessage();
            if (text.getString().equals(selectedBloodStatus.getDisplayName())) {
                button.setMessage(Component.literal("> " + selectedBloodStatus.getDisplayName() + " <")
                    .withStyle(style -> style.withColor(ColorUtils.SPELL_GREEN)));
            } else {
                button.setMessage(Component.literal(selectedBloodStatus.getDisplayName()));
            }
        }
        
        // Update magical type buttons
        for (Button button : magicalTypeButtons) {
            Component text = button.getMessage();
            if (text.getString().equals(selectedMagicalType.getDisplayName()) || 
                text.getString().equals("> " + selectedMagicalType.getDisplayName() + " <")) {
                button.setMessage(Component.literal("> " + selectedMagicalType.getDisplayName() + " <")
                    .withStyle(style -> style.withColor(ColorUtils.SPELL_GREEN)));
            } else {
                String displayName = text.getString().replace("> ", "").replace(" <", "");
                for (PlayerIdentityData.MagicalType type : PlayerIdentityData.MagicalType.values()) {
                    if (type.getDisplayName().equals(displayName)) {
                        button.setMessage(Component.literal(type.getDisplayName()));
                        break;
                    }
                }
            }
        }
    }
    
    private void onConfirm(Button button) {
        // Send identity data to server
        at.koopro.spells_n_squares.core.network.CharacterCreationPayload payload = 
            new at.koopro.spells_n_squares.core.network.CharacterCreationPayload(
                selectedBloodStatus, selectedMagicalType
            );
        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
        
        // Close screen
        this.minecraft.setScreen(null);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Title
        int titleY = startY - 30;
        Component title = Component.translatable("gui.spells_n_squares.character_creation.title")
            .withStyle(style -> style.withColor(ColorUtils.SPELL_GOLD));
        guiGraphics.drawCenteredString(this.font, title, centerX, titleY, ColorUtils.SPELL_WHITE);
        
        // Description
        int descY = titleY + 20;
        Component description = Component.translatable("gui.spells_n_squares.character_creation.description")
            .withStyle(style -> style.withColor(ColorUtils.SPELL_WHITE));
        guiGraphics.drawCenteredString(this.font, description, centerX, descY, ColorUtils.SPELL_WHITE);
        
        // Selected identity info
        int infoY = this.height - 60;
        String bloodStatusDesc = PlayerIdentityHelper.getBloodStatusDescription(selectedBloodStatus);
        String magicalTypeDesc = PlayerIdentityHelper.getMagicalTypeDescription(selectedMagicalType);
        
        guiGraphics.drawString(this.font, 
            ColorUtils.coloredText("Selected: " + selectedMagicalType.getDisplayName() + 
                " (" + selectedBloodStatus.getDisplayName() + ")", ColorUtils.SPELL_GREEN),
            centerX - 100, infoY, ColorUtils.SPELL_WHITE);
        
        guiGraphics.drawString(this.font, 
            ColorUtils.coloredText(magicalTypeDesc, ColorUtils.rgb(170, 170, 170)),
            centerX - 100, infoY + 12, ColorUtils.SPELL_WHITE);
        
        guiGraphics.drawString(this.font, 
            ColorUtils.coloredText(bloodStatusDesc, ColorUtils.rgb(170, 170, 170)),
            centerX - 100, infoY + 24, ColorUtils.SPELL_WHITE);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Prevent closing without confirming
    }
}

