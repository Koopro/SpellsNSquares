package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Handles setting up default spells for players when they join.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class SpellSetup {
    
    /**
     * Sets up default spell assignments when a player joins the world.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Assign default spells to the 4 slots
        // You can customize these assignments here
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_TOP, 
            SpellRegistry.spellId("heal"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_BOTTOM, 
            SpellRegistry.spellId("protego"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_LEFT, 
            SpellRegistry.spellId("fireball"));
        SpellManager.setSpellInSlot(event.getEntity(), SpellManager.SLOT_RIGHT, 
            SpellRegistry.spellId("lightning"));
        
        // SpellManager.setSpellInSlot will automatically sync to client
    }
}
