package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Manages Lumos spell state for players via item data components.
 */
public class LumosManager {
    private LumosManager() {
    }

    /**
     * Checks if a player has Lumos active on a wand in either hand.
     * @param player The player
     * @return true if Lumos is active on a held wand
     */
    public static boolean isLumosActive(Player player) {
        return findHeldWand(player)
            .map(stack -> stack.getOrDefault(ModDataComponents.LUMOS_ACTIVE.get(), false))
            .orElse(false);
    }

    /**
     * Sets the Lumos state on the first held wand that matches the tag.
     * @param player The player
     * @param active Whether Lumos should be active
     * @return true if a wand was updated
     */
    public static boolean setLumosActive(Player player, boolean active) {
        return findHeldWand(player)
            .map(stack -> applyState(stack, active))
            .orElse(false);
    }

    /**
     * Toggles Lumos on the first held wand that matches the tag.
     * @param player The player
     * @return The new state if toggled, or empty if no wand was found
     */
    public static Optional<Boolean> toggleLumos(Player player) {
        return findHeldWand(player)
            .map(LumosManager::toggleStackState);
    }

    /**
     * Toggles Lumos on the provided stack if it is a wand.
     * @param player The player holding the stack
     * @param stack The stack to toggle
     * @return New state if toggled, false if not applicable
     */
    public static boolean toggleLumos(Player player, ItemStack stack) {
        if (stack.isEmpty() || !stack.is(ModTags.WANDS)) {
            return false;
        }
        return toggleStackState(stack);
    }

    /**
     * Clears lumos data for a player.
     * Left in place to match lifecycle hooks.
     */
    public static void clearPlayerData(Player player) {
        // Item data components live on the stack; nothing to clear here.
    }

    private static Optional<ItemStack> findHeldWand(Player player) {
        return PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS);
    }

    private static boolean toggleStackState(ItemStack stack) {
        boolean current = stack.getOrDefault(ModDataComponents.LUMOS_ACTIVE.get(), false);
        return applyState(stack, !current);
    }

    private static boolean applyState(ItemStack stack, boolean active) {
        if (active) {
            stack.set(ModDataComponents.LUMOS_ACTIVE.get(), true);
        } else {
            stack.remove(ModDataComponents.LUMOS_ACTIVE.get());
        }
        return active;
    }
}

