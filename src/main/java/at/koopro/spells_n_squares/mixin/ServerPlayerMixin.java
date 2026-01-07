package at.koopro.spells_n_squares.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for server-side player enhancements.
 * Handles spell effect tracking, custom teleportation, and dynamic ability modifications.
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    
    /**
     * Inject into tick() to track spell effects and manage server-side spell state.
     * Integrates with SpellManager to update cooldowns and track active spells.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        
        // Update spell cooldowns (SpellManager handles this, but we ensure it's called)
        at.koopro.spells_n_squares.features.spell.manager.SpellManager.tickCooldowns(self);
        
        // Track active hold-to-cast spells
        // SpellManager already tracks this, but we can add additional monitoring here
        net.minecraft.resources.Identifier activeHoldSpell = 
            at.koopro.spells_n_squares.features.spell.manager.SpellManager.getActiveHoldSpell(self);
        if (activeHoldSpell != null) {
            // Player is holding a spell - we can add additional tracking here
            // Example: updateHoldSpellEffects(self, activeHoldSpell);
        }
    }
    
    /**
     * Hook into teleportation to integrate with apparition system.
     * Allows custom teleportation handling for apparition spells.
     * NOTE: The teleportTo() method with this signature may not exist in ServerPlayer.
     * Commented out until the correct method signature is determined.
     */
    // @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"))
    // private void onTeleportTo(net.minecraft.server.level.ServerLevel level, double x, double y, double z, 
    //                           float yRot, float xRot, CallbackInfo ci) {
    //     // Check if this is an apparition teleportation
    //     // This can be tracked via a flag or data component
    //     // Example: if (isApparating(self)) { spawnApparitionEffects(self, new Vec3(x, y, z)); }
    //     
    //     // For now, let normal teleportation proceed
    // }
    
    /**
     * Modify player capabilities dynamically based on spell effects.
     * Allows spells to grant temporary abilities like flight or enhanced movement.
     * NOTE: The target method Player.tick() may not exist or have a different signature.
     * Commented out until the correct method signature is determined.
     */
    // @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;tick()V", shift = At.Shift.AFTER))
    // private void updateDynamicAbilities(CallbackInfo ci) {
    //     ServerPlayer self = (ServerPlayer) (Object) this;
    //     
    //     // Update player abilities based on active spell effects
    //     // Example: if (hasSpellEffect(self, "flight")) { self.getAbilities().setMayFly(true); }
    //     
    //     // BroomEntity removed - transportation feature removed
    // }
}

