package at.koopro.spells_n_squares.core.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Base class for GUI screens that need to update dynamically based on data changes.
 * Provides automatic change detection and refresh mechanisms.
 */
public abstract class DynamicScreen extends Screen {
    private int updateCheckInterval = 1; // Check every tick
    private int updateCheckCounter = 0;
    private int refreshDelayTicks = 0;
    private boolean needsRefresh = false;
    private Object lastDataSnapshot = null;
    
    protected DynamicScreen(Component title) {
        super(title);
    }
    
    /**
     * Gets the current data snapshot for comparison.
     * Subclasses should return a comparable object that represents the current state.
     */
    protected abstract Object getDataSnapshot();
    
    /**
     * Called when data has changed and the screen needs to refresh.
     * Subclasses should rebuild their UI elements here.
     */
    protected abstract void onDataChanged();
    
    /**
     * Called every tick to check for data changes.
     * Override this if you need custom change detection logic.
     */
    protected boolean hasDataChanged(Object currentSnapshot, Object lastSnapshot) {
        if (currentSnapshot == null && lastSnapshot == null) return false;
        if (currentSnapshot == null || lastSnapshot == null) return true;
        return !currentSnapshot.equals(lastSnapshot);
    }
    
    /**
     * Force an immediate refresh of the screen.
     */
    public void forceRefresh() {
        needsRefresh = true;
        refreshDelayTicks = 0;
    }
    
    /**
     * Schedule a refresh after a delay (in ticks).
     */
    public void scheduleRefresh(int delayTicks) {
        needsRefresh = true;
        refreshDelayTicks = delayTicks;
    }
    
    /**
     * Set how often to check for data changes (in ticks).
     * Default is every tick (1).
     */
    protected void setUpdateCheckInterval(int ticks) {
        this.updateCheckInterval = Math.max(1, ticks);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Check for data changes at specified interval
        updateCheckCounter++;
        if (updateCheckCounter >= updateCheckInterval) {
            updateCheckCounter = 0;
            
            Object currentSnapshot = getDataSnapshot();
            if (hasDataChanged(currentSnapshot, lastDataSnapshot)) {
                lastDataSnapshot = currentSnapshot;
                needsRefresh = true;
                refreshDelayTicks = 0; // Refresh immediately on change detection
            }
        }
        
        // Handle scheduled refresh
        if (needsRefresh) {
            if (refreshDelayTicks > 0) {
                refreshDelayTicks--;
            } else {
                onDataChanged();
                needsRefresh = false;
                // Update snapshot after refresh
                lastDataSnapshot = getDataSnapshot();
            }
        }
    }
    
    @Override
    protected void init() {
        super.init();
        // Initialize snapshot on first init
        lastDataSnapshot = getDataSnapshot();
        onDataChanged();
    }
}

