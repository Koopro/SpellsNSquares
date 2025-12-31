package at.koopro.spells_n_squares.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for Mob entities to add custom AI goal registration for magical creatures.
 * Handles spell resistance/immunity system and magical creature behavior modifications.
 */
@Mixin(Mob.class)
public abstract class MobMixin {
    
    @Shadow
    protected GoalSelector goalSelector;
    
    @Shadow
    protected GoalSelector targetSelector;
    
    /**
     * Inject into registerGoals() to add custom AI goals for magical creatures.
     */
    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void onRegisterGoals(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        
        // Add custom AI goals for magical creatures
        // Example: if (isMagicalCreature(self)) {
        //     this.goalSelector.addGoal(1, new MagicalCreatureGoal(self));
        // }
        
        // Add spell resistance behaviors
        // Example: if (hasSpellResistance(self)) {
        //     this.goalSelector.addGoal(0, new SpellResistanceGoal(self));
        // }
    }
    
    /**
     * Inject into tick() to handle spell resistance and magical creature behaviors.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        
        // Check for spell resistance/immunity
        // Example: if (hasSpellResistance(self)) {
        //     // Reduce spell damage or effects
        // }
        
        // Handle magical creature behavior modifications
        // Example: if (isMagicalCreature(self)) {
        //     updateMagicalBehavior(self);
        // }
    }
    
    /**
     * Hook into hurt() to apply spell resistance.
     */
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(net.minecraft.world.damagesource.DamageSource source, float amount, 
                        org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        Mob self = (Mob) (Object) this;
        
        // Check for spell-based damage and apply resistance
        // Example: if (isSpellDamage(source) && hasSpellResistance(self)) {
        //     amount *= 0.5f; // 50% spell damage reduction
        // }
        
        // Check for complete spell immunity
        // Example: if (isSpellDamage(source) && hasSpellImmunity(self)) {
        //     cir.setReturnValue(false);
        //     return;
        // }
    }
}

