package at.koopro.spells_n_squares.core.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Utility class for common player item holding checks.
 * Reduces repetitive patterns of checking main hand and off-hand for items.
 */
public final class PlayerItemUtils {
    private PlayerItemUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Finds the first held item matching the given Item.
     * Checks main hand first, then off-hand.
     * @param player The player to check
     * @param item The item to find
     * @return Optional containing the ItemStack if found, empty otherwise
     */
    public static Optional<ItemStack> findHeldItem(Player player, Item item) {
        if (player == null) {
            return Optional.empty();
        }
        
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.is(item)) {
            return Optional.of(mainHand);
        }
        
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && offHand.is(item)) {
            return Optional.of(offHand);
        }
        
        return Optional.empty();
    }
    
    /**
     * Finds the first held item matching the given TagKey.
     * Checks main hand first, then off-hand.
     * @param player The player to check
     * @param tag The tag to match
     * @return Optional containing the ItemStack if found, empty otherwise
     */
    public static Optional<ItemStack> findHeldItemByTag(Player player, TagKey<Item> tag) {
        if (player == null) {
            return Optional.empty();
        }
        
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.is(tag)) {
            return Optional.of(mainHand);
        }
        
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && offHand.is(tag)) {
            return Optional.of(offHand);
        }
        
        return Optional.empty();
    }
    
    /**
     * Checks if the player is holding the given Item in either hand.
     * @param player The player to check
     * @param item The item to check for
     * @return true if the player is holding the item
     */
    public static boolean isHoldingItem(Player player, Item item) {
        return findHeldItem(player, item).isPresent();
    }
    
    /**
     * Checks if the player is holding an item with the given TagKey in either hand.
     * @param player The player to check
     * @param tag The tag to check for
     * @return true if the player is holding an item with the tag
     */
    public static boolean isHoldingItemByTag(Player player, TagKey<Item> tag) {
        return findHeldItemByTag(player, tag).isPresent();
    }
}





















