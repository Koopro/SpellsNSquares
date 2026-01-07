package at.koopro.spells_n_squares.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for PlayerAbilities to enable dynamic ability modifications.
 * Handles flight, walk speed, jump height, and other ability modifications from spells.
 */
@Mixin(Player.class)
public abstract class PlayerAbilitiesMixin {
    
    @Shadow
    public abstract net.minecraft.world.entity.player.Abilities getAbilities();
    
    /**
     * Inject into tick() to dynamically modify player abilities based on spell effects.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;updatePlayerPose()V", shift = At.Shift.AFTER))
    private void updateSpellBasedAbilities(CallbackInfo ci) {
        // BroomEntity removed - transportation feature removed
        // This mixin is kept for potential future spell-based ability modifications
        
        // Apply spell-based ability modifications
        // Example: Levitation spell could grant flight
        // Player self = (Player) (Object) this;
        // net.minecraft.world.entity.player.Abilities abilities = this.getAbilities();
        // if (hasSpellEffect(self, "levitation")) {
        //     abilities.mayfly = true;
        //     abilities.flying = true;
        // }
        
        // Apply spell-based speed modifications
        // Note: Speed modifications are better handled via MobEffects (SPEED effect)
        // But we can add custom modifications here if needed
        // Example: if (hasSpellEffect(self, "speed_boost")) {
        //     abilities.walkingSpeed = 0.1f * 1.5f; // 50% speed boost
        // }
    }
}

