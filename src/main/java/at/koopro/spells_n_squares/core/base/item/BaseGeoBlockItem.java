package at.koopro.spells_n_squares.core.base.item;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base class for GeckoLib block items with common functionality.
 * Provides GeckoLib setup for block items, animation management, and renderer creation.
 * Extends BlockItem and implements GeoItem.
 */
public abstract class BaseGeoBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private @Nullable GeoItemRenderer<?> cachedRenderer;
    
    public BaseGeoBlockItem(Block block, Properties properties) {
        super(block, properties);
        DevLogger.logMethodEntry(this, "BaseGeoBlockItem", 
            "block=" + (block != null ? block.getDescriptionId() : "null") + 
            ", registering synced animatable");
        GeoItem.registerSyncedAnimatable(this);
        DevLogger.logMethodExit(this, "BaseGeoBlockItem");
    }
    
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        DevLogger.logMethodEntry(this, "createGeoRenderer");
        consumer.accept(new GeoRenderProvider() {
            @Override
            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (cachedRenderer == null) {
                    DevLogger.logDebug(BaseGeoBlockItem.this, "getGeoItemRenderer", "Creating new renderer");
                    cachedRenderer = createRenderer();
                }
                return cachedRenderer;
            }
        });
        DevLogger.logMethodExit(this, "createGeoRenderer");
    }
    
    /**
     * Creates the GeoItemRenderer for this block item.
     * Subclasses must implement to provide their specific renderer.
     * 
     * @return The GeoItemRenderer instance
     */
    protected abstract GeoItemRenderer<?> createRenderer();
    
    /**
     * Gets a renderer instance using a supplier (lazy initialization).
     * 
     * @param rendererSupplier The supplier that creates the renderer
     * @return A GeoRenderProvider that uses the supplier
     */
    protected GeoRenderProvider createRendererProvider(Supplier<GeoItemRenderer<?>> rendererSupplier) {
        return new GeoRenderProvider() {
            private @Nullable GeoItemRenderer<?> renderer;
            
            @Override
            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = rendererSupplier.get();
                }
                return renderer;
            }
        };
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        DevLogger.logMethodEntry(this, "registerControllers");
        setupAnimations(controllers);
        DevLogger.logMethodExit(this, "registerControllers");
    }
    
    /**
     * Sets up animations for this block item.
     * Subclasses should override to register their animation controllers.
     * Default implementation does nothing (no animations).
     * 
     * @param controllers The controller registrar
     */
    protected void setupAnimations(AnimatableManager.ControllerRegistrar controllers) {
        DevLogger.logMethodEntry(this, "setupAnimations");
        // Override in subclasses to add animations
        DevLogger.logMethodExit(this, "setupAnimations");
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    /**
     * Triggers a client-side animation for this block item.
     * 
     * @param stack The item stack
     * @param controllerName The name of the animation controller
     * @param animName The name of the animation
     */
    protected void triggerAnimation(ItemStack stack, String controllerName, String animName) {
        if (stack.getItem() == this) {
            triggerAnim(stack, controllerName, animName);
        }
    }
    
    /**
     * Triggers a client-side animation for this block item using the GeckoLib trigger method.
     * 
     * @param stack The item stack
     * @param controllerName The name of the animation controller
     * @param animName The name of the animation
     */
    private void triggerAnim(ItemStack stack, String controllerName, String animName) {
        // This would typically use GeckoLib's triggerAnim method
        // For now, we'll leave it as a placeholder that subclasses can use
    }
    
    /**
     * Gets a data component value from the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @return The component value, or null if not present
     */
    protected <T> @Nullable T getDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        return stack.get(componentType);
    }
    
    /**
     * Sets a data component value on the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @param value The value to set
     */
    protected <T> void setDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType, T value) {
        stack.set(componentType, value);
    }
    
    /**
     * Removes a data component from the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     */
    protected <T> void removeDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        stack.remove(componentType);
    }
    
    /**
     * Checks if a data component is present on the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @return True if the component is present
     */
    protected <T> boolean hasDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        return stack.has(componentType);
    }
}


