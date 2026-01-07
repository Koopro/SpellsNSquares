package at.koopro.spells_n_squares.core.client.gui;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Handler for character creation screen.
 * Detects first-time players and opens the character creation screen automatically.
 */
@EventBusSubscriber(modid = "spells_n_squares", value = Dist.CLIENT)
public class CharacterCreationHandler {
    private static boolean hasCheckedThisSession = false;
    
    /**
     * Checks if the player needs to create their character and opens the screen if needed.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            return;
        }
        
        // Only check once per session
        if (hasCheckedThisSession) {
            return;
        }
        
        // Check if player has identity data set
        // On client, PlayerDataHelper.get() returns empty, so we need to check differently
        // For now, we'll use a simple flag in persistent data or check on first join
        // This is a simplified check - in a full implementation, you'd sync identity data to client
        
        // For now, skip automatic opening - can be enhanced later with proper client-side data sync
        hasCheckedThisSession = true;
    }
    
    /**
     * Opens the character creation screen.
     * Can be called manually or from server-side detection.
     */
    public static void openCharacterCreationScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.screen == null) {
            mc.setScreen(new CharacterCreationScreen());
        }
    }
    
    /**
     * Resets the check flag (for testing or manual triggers).
     */
    public static void resetCheck() {
        hasCheckedThisSession = false;
    }
}

