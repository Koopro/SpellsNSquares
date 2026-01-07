package at.koopro.spells_n_squares.core.base.entity;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Component for GeckoLib entity functionality.
 * Provides GeckoLib setup, animation management, and renderer creation.
 * Uses composition instead of inheritance for flexibility.
 */
public class GeckoLibEntityComponent {
    private final GeoEntity entity;
    private final AnimatableInstanceCache cache;
    private AnimationCallback animationCallback;
    
    /**
     * Callback for setting up animations.
     */
    @FunctionalInterface
    public interface AnimationCallback {
        void setupAnimations(AnimatableManager.ControllerRegistrar controllers);
    }
    
    public GeckoLibEntityComponent(GeoEntity entity) {
        this.entity = entity;
        this.cache = GeckoLibUtil.createInstanceCache(entity);
        DevLogger.logMethodEntry((Entity) entity, "GeckoLibEntityComponent", "registering GeckoLib entity");
    }
    
    /**
     * Sets the callback for setting up animations.
     */
    public void setAnimationCallback(AnimationCallback callback) {
        this.animationCallback = callback;
    }
    
    /**
     * Registers animation controllers.
     * Calls the animation callback if set.
     */
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        DevLogger.logMethodEntry((Entity) entity, "registerControllers");
        if (animationCallback != null) {
            animationCallback.setupAnimations(controllers);
        }
        DevLogger.logMethodExit((Entity) entity, "registerControllers");
    }
    
    /**
     * Gets the animatable instance cache.
     */
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    /**
     * Triggers a client-side animation for this entity.
     * 
     * @param controllerName The name of the animation controller
     * @param animName The name of the animation
     */
    public void triggerAnimation(String controllerName, String animName) {
        DevLogger.logMethodEntry((Entity) entity, "triggerAnimation", 
            "controller=" + controllerName + ", animation=" + animName);
        // This would typically use GeckoLib's triggerAnim method
        // For now, we'll leave it as a placeholder that subclasses can use
        DevLogger.logMethodExit((Entity) entity, "triggerAnimation");
    }
}

