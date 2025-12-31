package at.koopro.spells_n_squares.mixin;

import at.koopro.spells_n_squares.features.creatures.ghosts.GhostEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for base Entity class to add custom handling for all magical entities.
 * Handles custom collision detection for ghosts and teleportation effect hooks.
 */
@Mixin(Entity.class)
public class EntityMixin {
    
    /**
     * Modify collision detection for ghost entities (phase-through).
     * Ghosts can phase through other entities as per their design.
     */
    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void onCanCollideWith(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        
        // Ghosts can phase through entities
        if (self instanceof GhostEntity || entity instanceof GhostEntity) {
            cir.setReturnValue(false);
            return;
        }
        
        // Check for other ghost-related entities
        if (self instanceof at.koopro.spells_n_squares.features.ghosts.GhostEntity || 
            entity instanceof at.koopro.spells_n_squares.features.ghosts.GhostEntity) {
            cir.setReturnValue(false);
            return;
        }
    }
    
    /**
     * Modify pushability for ghost entities.
     * Ghosts can't be pushed as per their design.
     */
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void onIsPushable(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        
        // Ghosts can't be pushed
        if (self instanceof GhostEntity) {
            cir.setReturnValue(false);
            return;
        }
        
        // Check for other ghost-related entities
        if (self instanceof at.koopro.spells_n_squares.features.ghosts.GhostEntity) {
            cir.setReturnValue(false);
            return;
        }
    }
    
    /**
     * Hook into teleportation to add apparition effects.
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"))
    private void onTeleportTo(double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        
        // Check if this is an apparition teleportation
        // Example: if (isApparating(self)) {
        //     Vec3 originPos = self.position();
        //     spawnApparitionEffects(self.level(), originPos);
        // }
    }
    
    /**
     * Modify movement for magical entities.
     */
    @ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vec3 modifyMovement(Vec3 original, net.minecraft.world.entity.MoverType type) {
        Entity self = (Entity) (Object) this;
        
        // Modify movement for ghost entities (they can phase through blocks)
        if (self instanceof GhostEntity) {
            // Ghosts use noPhysics, so movement is already handled
            // But we can add additional modifications here if needed
        }
        
        // Modify movement for other magical entities
        // Example: if (hasLevitationSpell(self)) {
        //     return original.add(0, 0.1, 0); // Add upward movement
        // }
        
        return original;
    }
    
    /**
     * Inject into tick() to handle magical entity behaviors.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        
        // Handle magical entity behaviors
        // Example: if (isMagicalEntity(self)) {
        //     updateMagicalEntityBehavior(self);
        // }
        
        // Handle spell effect updates
        // Example: if (hasActiveSpellEffects(self)) {
        //     updateSpellEffects(self);
        // }
    }
}

