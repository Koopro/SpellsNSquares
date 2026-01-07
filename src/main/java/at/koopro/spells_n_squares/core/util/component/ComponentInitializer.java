package at.koopro.spells_n_squares.core.util.component;

import at.koopro.spells_n_squares.core.base.entity.ModEntityComponent;
import at.koopro.spells_n_squares.core.base.entity.GeckoLibEntityComponent;
import at.koopro.spells_n_squares.core.base.block.ModBlockComponent;
import at.koopro.spells_n_squares.core.base.block.GeckoLibBlockComponent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Utility class for initializing components with common callback patterns.
 * Reduces boilerplate in component-based classes by providing helper methods
 * for setting up common callback configurations.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // In BaseModEntity constructor:
 * ComponentInitializer.setupModEntityComponent(
 *     modEntityComponent,
 *     this::defineCustomSynchedData,
 *     this::saveCustomData,
 *     this::loadCustomData,
 *     this::onHurt
 * );
 * }</pre>
 */
public final class ComponentInitializer {
    private ComponentInitializer() {
        // Utility class - prevent instantiation
    }
    
    // ========== ModEntityComponent Setup ==========
    
    /**
     * Sets up a ModEntityComponent with standard callbacks.
     * 
     * @param component The component to set up
     * @param dataCallback The callback for defining synched data
     * @param saveCallback The callback for saving data
     * @param loadCallback The callback for loading data
     * @param hurtCallback The callback for handling damage
     */
    public static void setupModEntityComponent(
            ModEntityComponent component,
            ModEntityComponent.DataCallback dataCallback,
            java.util.function.Consumer<ValueOutput> saveCallback,
            java.util.function.Consumer<ValueInput> loadCallback,
            ModEntityComponent.HurtCallback hurtCallback) {
        if (component == null) {
            return;
        }
        
        if (dataCallback != null) {
            component.setDataCallback(dataCallback);
        }
        
        if (saveCallback != null || loadCallback != null) {
            component.setSaveLoadCallback(new ModEntityComponent.SaveLoadCallback() {
                @Override
                public void saveCustomData(ValueOutput output) {
                    if (saveCallback != null) {
                        saveCallback.accept(output);
                    }
                }
                
                @Override
                public void loadCustomData(ValueInput input) {
                    if (loadCallback != null) {
                        loadCallback.accept(input);
                    }
                }
            });
        }
        
        if (hurtCallback != null) {
            component.setHurtCallback(hurtCallback);
        }
    }
    
    // ========== GeckoLibEntityComponent Setup ==========
    
    /**
     * Sets up a GeckoLibEntityComponent with standard callbacks.
     * 
     * @param component The component to set up
     * @param animationCallback The callback for setting up animations
     */
    public static void setupGeckoLibEntityComponent(
            GeckoLibEntityComponent component,
            GeckoLibEntityComponent.AnimationCallback animationCallback) {
        if (component == null) {
            return;
        }
        
        if (animationCallback != null) {
            component.setAnimationCallback(animationCallback);
        }
    }
    
    // ========== ModBlockComponent Setup ==========
    
    /**
     * Sets up a ModBlockComponent with standard callbacks.
     * 
     * @param component The component to set up
     * @param stateCallback The callback for state operations
     * @param placementCallback The callback for placement customization
     * @param interactionCallback The callback for interactions
     */
    public static void setupModBlockComponent(
            ModBlockComponent component,
            ModBlockComponent.StateCallback stateCallback,
            ModBlockComponent.PlacementCallback placementCallback,
            ModBlockComponent.InteractionCallback interactionCallback) {
        if (component == null) {
            return;
        }
        
        if (stateCallback != null) {
            component.setStateCallback(stateCallback);
        }
        
        if (placementCallback != null) {
            component.setPlacementCallback(placementCallback);
        }
        
        if (interactionCallback != null) {
            component.setInteractionCallback(interactionCallback);
        }
    }
    
    // ========== GeckoLibBlockComponent Setup ==========
    
    /**
     * Sets up a GeckoLibBlockComponent with standard callbacks.
     * 
     * @param component The component to set up
     * @param blockEntityCallback The callback for creating block entities
     * @param tickerCallback The callback for tickers
     */
    public static void setupGeckoLibBlockComponent(
            GeckoLibBlockComponent component,
            GeckoLibBlockComponent.BlockEntityCallback blockEntityCallback,
            GeckoLibBlockComponent.TickerCallback tickerCallback) {
        if (component == null) {
            return;
        }
        
        if (blockEntityCallback != null) {
            component.setBlockEntityCallback(blockEntityCallback);
        }
        
        if (tickerCallback != null) {
            component.setTickerCallback(tickerCallback);
        }
    }
}

