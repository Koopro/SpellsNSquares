package at.koopro.spells_n_squares.mixin;

import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.wand.core.WandItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept item usage for spell casting and magical item interactions.
 * Handles wand spell casting cooldown checks and custom interaction results.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    /**
     * Intercept use() calls to add spell casting cooldown checks before item usage.
     * This allows us to prevent item usage when spells are on cooldown.
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack self = (ItemStack) (Object) this;
        Item item = self.getItem();
        
        // Check if this is a wand item
        if (item instanceof WandItem) {
            // On client side, check client-side cooldowns
            if (level.isClientSide()) {
                int selectedSlot = ClientSpellData.getSelectedSlot();
                Identifier spellId = ClientSpellData.getSpellInSlot(selectedSlot);
                if (spellId != null && ClientSpellData.isOnCooldown(spellId)) {
                    // Spell is on cooldown - prevent wand usage
                    cir.setReturnValue(InteractionResult.FAIL);
                    return;
                }
            } else {
                // On server side, check server-side cooldowns
                int selectedSlot = ClientSpellData.getSelectedSlot(); // This will be synced from client
                Identifier spellId = SpellManager.getSpellInSlot(player, selectedSlot);
                if (spellId != null && SpellManager.isOnCooldown(player, spellId)) {
                    // Spell is on cooldown - prevent wand usage
                    cir.setReturnValue(InteractionResult.FAIL);
                    return;
                }
            }
        }
    }
}

