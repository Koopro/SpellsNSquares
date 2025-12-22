package at.koopro.spells_n_squares.features.robes;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles house robe set bonuses.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class HouseRobeBonusHandler {
    
    /**
     * Checks if a player is wearing a full house robe set (chest + legs + boots).
     */
    public static House getWornHouseSet(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        
        // Check if all three pieces are house robes of the same house
        House chestHouse = getHouseFromItem(chest);
        if (chestHouse == null) {
            return null;
        }
        
        House legsHouse = getHouseFromItem(legs);
        House bootsHouse = getHouseFromItem(boots);
        
        if (legsHouse == chestHouse && bootsHouse == chestHouse) {
            return chestHouse;
        }
        
        return null;
    }
    
    /**
     * Gets the house from a robe item.
     */
    private static House getHouseFromItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        
        if (stack.is(RobesRegistry.GRYFFINDOR_ROBE_CHEST.get()) ||
            stack.is(RobesRegistry.GRYFFINDOR_ROBE_LEGS.get()) ||
            stack.is(RobesRegistry.GRYFFINDOR_ROBE_BOOTS.get())) {
            return House.GRYFFINDOR;
        }
        if (stack.is(RobesRegistry.SLYTHERIN_ROBE_CHEST.get()) ||
            stack.is(RobesRegistry.SLYTHERIN_ROBE_LEGS.get()) ||
            stack.is(RobesRegistry.SLYTHERIN_ROBE_BOOTS.get())) {
            return House.SLYTHERIN;
        }
        if (stack.is(RobesRegistry.HUFFLEPUFF_ROBE_CHEST.get()) ||
            stack.is(RobesRegistry.HUFFLEPUFF_ROBE_LEGS.get()) ||
            stack.is(RobesRegistry.HUFFLEPUFF_ROBE_BOOTS.get())) {
            return House.HUFFLEPUFF;
        }
        if (stack.is(RobesRegistry.RAVENCLAW_ROBE_CHEST.get()) ||
            stack.is(RobesRegistry.RAVENCLAW_ROBE_LEGS.get()) ||
            stack.is(RobesRegistry.RAVENCLAW_ROBE_BOOTS.get())) {
            return House.RAVENCLAW;
        }
        
        return null;
    }
    
    /**
     * Applies house robe set bonuses.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        House houseSet = getWornHouseSet(player);
        
        if (houseSet == null) {
            return;
        }
        
        // Apply house-specific bonuses
        // Note: Actual bonus implementation would require more complex systems
        // For now, this is a placeholder that can be extended
        switch (houseSet) {
            case GRYFFINDOR:
                // Small resistance to dark magic spells, bravery bonus
                // Would need to hook into damage events
                break;
            case SLYTHERIN:
                // Cunning bonus (faster spell cooldowns), resistance to charm spells
                // Would need to hook into spell cooldown system
                break;
            case HUFFLEPUFF:
                // Loyalty bonus (shared cooldown reduction with nearby allies)
                // Would need to track nearby players
                break;
            case RAVENCLAW:
                // Wisdom bonus (spell accuracy/range increase)
                // Would need to hook into spell casting
                break;
        }
    }
}

