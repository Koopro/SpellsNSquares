package at.koopro.spells_n_squares.features.education.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import at.koopro.spells_n_squares.SpellsNSquares;

/**
 * Event handler for bestiary screen to prevent inventory key from closing it and handle clicks.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class BestiaryScreenHandler {
    
    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        // Prevent inventory key from closing the bestiary screen
        if (event.getScreen() instanceof BestiaryScreen) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.options != null && mc.options.keyInventory != null) {
                // Check if the pressed key matches the inventory key
                boolean isInventoryKey = false;
                
                // Try multiple methods to detect the inventory key
                int keyCode = event.getKeyCode();
                
                // Method 1: Check if key code matches the inventory key's default key code
                int inventoryKeyCode = mc.options.keyInventory.getDefaultKey().getValue();
                if (keyCode == inventoryKeyCode) {
                    isInventoryKey = true;
                }
                
                // Method 2: Try matching with KeyEvent if available
                if (!isInventoryKey && event.getKeyEvent() != null) {
                    try {
                        isInventoryKey = mc.options.keyInventory.matches(event.getKeyEvent());
                    } catch (Exception e) {
                        // Ignore - fallback to key code check
                    }
                }
                
                // Method 3: Check if the key is currently down (as a last resort)
                if (!isInventoryKey && mc.options.keyInventory.isDown()) {
                    // This is less reliable but might catch edge cases
                    isInventoryKey = true;
                }
                
                if (isInventoryKey) {
                    System.out.println("[BestiaryScreenHandler] Inventory key detected, checking if search box is focused...");
                    // If search box is focused, let it handle the key
                    BestiaryScreen screen = (BestiaryScreen) event.getScreen();
                    if (screen.isSearchBoxFocused()) {
                        System.out.println("[BestiaryScreenHandler] Search box is focused, allowing key press");
                        return; // Let search box handle it
                    }
                    // Otherwise, prevent closing
                    System.out.println("[BestiaryScreenHandler] Canceling inventory key press to prevent screen from closing");
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        // Handle clicks on creature list items
        if (event.getScreen() instanceof BestiaryScreen && event.getButton() == 0) {
            BestiaryScreen screen = (BestiaryScreen) event.getScreen();
            screen.handleCreatureListClick(event.getMouseX(), event.getMouseY());
        }
    }
}

