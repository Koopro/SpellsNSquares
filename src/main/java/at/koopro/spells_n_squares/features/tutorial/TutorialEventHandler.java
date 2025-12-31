package at.koopro.spells_n_squares.features.tutorial;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import at.koopro.spells_n_squares.features.wand.WandDataHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles tutorial progression based on player actions.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class TutorialEventHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        if (player == null || TutorialSystem.isTutorialComplete(player)) {
            return;
        }
        
        // Only check tutorial progress periodically (every 2 seconds)
        if (player.tickCount % 40 != 0) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            TutorialSystem.TutorialStep currentStep = TutorialSystem.getCurrentStep(player);
            
            switch (currentStep) {
                case WELCOME:
                    // Welcome step - automatically advance after a short delay
                    if (player.tickCount > 100) { // 5 seconds after login
                        TutorialSystem.advanceStep(player);
                    }
                    break;
                    
                case GET_WAND:
                    // Check if player has a wand
                    ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
                    if (!wand.isEmpty() && WandDataHelper.hasWandData(wand)) {
                        TutorialSystem.advanceStep(player);
                    }
                    break;
                    
                case CHOOSE_WAND:
                    // Check if player has chosen a wand (has owner)
                    ItemStack heldWand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
                    if (!heldWand.isEmpty()) {
                        java.util.UUID owner = WandDataHelper.getOwner(heldWand);
                        if (owner != null && owner.equals(player.getUUID())) {
                            TutorialSystem.advanceStep(player);
                        }
                    }
                    break;
                    
                case CAST_FIRST_SPELL:
                    // Check if player has cast a spell (check mastery uses)
                    at.koopro.spells_n_squares.modules.spell.internal.SpellData spellData = 
                        at.koopro.spells_n_squares.core.data.PlayerDataHelper.getSpellData(player);
                    if (!spellData.masteryUses().isEmpty()) {
                        TutorialSystem.advanceStep(player);
                    }
                    break;
                    
                case LEARN_SPELLS:
                    // Check if player has learned multiple spells
                    at.koopro.spells_n_squares.modules.spell.internal.SpellData learnSpellData = 
                        at.koopro.spells_n_squares.core.data.PlayerDataHelper.getSpellData(player);
                    if (learnSpellData.learnedSpells().size() >= 3) {
                        TutorialSystem.advanceStep(player);
                    }
                    break;
                    
                case COMPLETE:
                    // Tutorial complete - nothing to do
                    break;
            }
        }, "tutorial progression", player);
    }
}

