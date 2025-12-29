package at.koopro.spells_n_squares.features.debug;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Manages the state of the item/block debugger tool.
 * Tracks whether debug information should be displayed in tooltips.
 */
@EventBusSubscriber(modid = "spells_n_squares", value = Dist.CLIENT)
public class ItemDebuggerManager {
    private static boolean enabled = false;
    
    /**
     * Toggles the debugger state.
     * @return The new enabled state
     */
    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }
    
    /**
     * Checks if the debugger is currently enabled.
     * @return true if debug information should be shown
     */
    public static boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets the debugger state explicitly.
     * @param enabled The new enabled state
     */
    public static void setEnabled(boolean enabled) {
        ItemDebuggerManager.enabled = enabled;
    }
}

