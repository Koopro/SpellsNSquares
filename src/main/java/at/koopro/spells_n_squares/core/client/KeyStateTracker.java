package at.koopro.spells_n_squares.core.client;

import net.minecraft.client.KeyMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for tracking key states to detect key press events.
 * Simplifies the pattern of tracking previous key states to detect transitions.
 */
public final class KeyStateTracker {
    private final Map<KeyMapping, Boolean> lastStates = new HashMap<>();
    
    /**
     * Checks if a key was just pressed (transitioned from not pressed to pressed).
     * Updates the internal state tracking.
     * @param key The key mapping to check
     * @return true if the key was just pressed this frame
     */
    public boolean wasJustPressed(KeyMapping key) {
        boolean current = key.isDown();
        boolean last = lastStates.getOrDefault(key, false);
        lastStates.put(key, current);
        return current && !last;
    }
    
    /**
     * Resets the state tracker (clears all tracked states).
     * Useful when switching contexts or resetting input state.
     */
    public void reset() {
        lastStates.clear();
    }
}

















