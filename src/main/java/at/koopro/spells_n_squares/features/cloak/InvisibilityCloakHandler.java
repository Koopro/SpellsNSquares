package at.koopro.spells_n_squares.features.cloak;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import at.koopro.spells_n_squares.core.registry.ModItems;

/**
 * Handles invisibility effect for players wearing invisibility cloaks.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class InvisibilityCloakHandler {
    
    /**
     * Applies invisibility to players wearing invisibility cloaks.
     * Checks every tick on the server side.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        
        // Only apply on server side
        if (player.level().isClientSide()) {
            return;
        }
        
        // Check if player is wearing an invisibility cloak in the chest slot
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        
        boolean hasDemiguiseCloak = chestArmor.getItem() == ModItems.DEMIGUISE_CLOAK.get();
        boolean hasDeathlyHallowCloak = chestArmor.getItem() == ModItems.DEATHLY_HALLOW_CLOAK.get();
        
        if (hasDemiguiseCloak || hasDeathlyHallowCloak) {
            // Check if cloak is not broken (for Demiguise cloak)
            if (hasDemiguiseCloak && chestArmor.getDamageValue() >= chestArmor.getMaxDamage()) {
                // Cloak is broken, remove invisibility if it was active
                if (player.isInvisible()) {
                    player.setInvisible(false);
                }
                return;
            }
            
            // Apply direct invisibility (without status effect)
            player.setInvisible(true);
        } else {
            // Player is not wearing a cloak, ensure invisibility is removed
            // Only remove if it was set by our cloaks (check if they have no invisibility potion effect)
            // If they have the potion effect, don't interfere with it
            if (player.isInvisible() && player.getEffect(MobEffects.INVISIBILITY) == null) {
                // Player is invisible but doesn't have the potion effect
                // This was likely set by our cloak, so remove it
                player.setInvisible(false);
            }
        }
    }
}
