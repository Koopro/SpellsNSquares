package at.koopro.spells_n_squares.core.base.entity;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

/**
 * Base class for GeckoLib entities with common functionality.
 * Provides GeckoLib setup, animation management, and renderer creation.
 * Extends BaseModEntity to inherit all entity functionality.
 * Uses composition with GeckoLibEntityComponent for flexibility.
 */
public abstract class BaseGeoEntity extends BaseModEntity implements GeoEntity {
    protected final GeckoLibEntityComponent geckoLibComponent;
    
    public BaseGeoEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseGeoEntity", "registering GeckoLib entity");
        
        this.geckoLibComponent = new GeckoLibEntityComponent(this);
        geckoLibComponent.setAnimationCallback(controllers -> setupAnimations(controllers));
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        geckoLibComponent.registerControllers(controllers);
    }
    
    /**
     * Sets up animations for this entity.
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
        return geckoLibComponent.getAnimatableInstanceCache();
    }
    
    /**
     * Triggers a client-side animation for this entity.
     * 
     * @param controllerName The name of the animation controller
     * @param animName The name of the animation
     */
    protected void triggerAnimation(String controllerName, String animName) {
        geckoLibComponent.triggerAnimation(controllerName, animName);
    }
}

