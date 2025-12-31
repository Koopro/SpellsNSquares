package at.koopro.spells_n_squares.core.manager;

import net.minecraft.client.Minecraft;

/**
 * Base class for client-side manager classes.
 * Extends BaseManager with client-specific functionality.
 */
public abstract class BaseClientManager extends BaseManager {
    
    protected BaseClientManager() {
        super();
    }
    
    /**
     * Gets the Minecraft client instance.
     * @return The Minecraft instance, or null if not available
     */
    protected Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
    
    /**
     * Checks if the client is available and not paused.
     * @return true if client is available and not paused
     */
    protected boolean isClientAvailable() {
        Minecraft mc = getMinecraft();
        return mc != null && mc.player != null && mc.level != null && !mc.isPaused();
    }
    
    /**
     * Validates that the client is available.
     * @throws IllegalStateException if client is not available
     */
    protected void validateClientAvailable() {
        if (!isClientAvailable()) {
            throw new IllegalStateException("Client is not available");
        }
    }
}


