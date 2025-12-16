package at.koopro.spells_n_squares.features.gear;

import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Manages spell augmentations from socketed runes/charms.
 */
public final class SpellAugmentManager {
    private SpellAugmentManager() {
    }
    
    /**
     * Gets the total augment value for a spell from the player's gear.
     */
    public static float getAugmentValue(Player player, Identifier spellId) {
        float totalAugment = 0.0f;
        
        // Check wand for sockets
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (!wand.isEmpty()) {
            totalAugment += getAugmentFromItem(wand, spellId);
        }
        
        return totalAugment;
    }
    
    /**
     * Gets augment value from a specific item's sockets.
     */
    private static float getAugmentFromItem(ItemStack stack, Identifier spellId) {
        SocketData.SocketDataComponent data = stack.get(SocketData.SOCKET_DATA.get());
        if (data == null) {
            return 0.0f;
        }
        
        float total = 0.0f;
        for (Identifier socketedId : data.socketedItems()) {
            // In full implementation, would look up the rune/charm item and get its augment value
            // For now, simplified version
            total += 0.1f; // Placeholder augment value
        }
        
        return total;
    }
}

