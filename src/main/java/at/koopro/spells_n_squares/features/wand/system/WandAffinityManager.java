package at.koopro.spells_n_squares.features.wand.system;

import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.player.PlayerItemUtils;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandAffinity;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Manages wand affinity bonuses and applies them during spell casting.
 */
public final class WandAffinityManager {
    private WandAffinityManager() {
    }
    
    /**
     * Gets the wand affinity for a player's currently held wand.
     */
    public static WandAffinity getPlayerWandAffinity(Player player) {
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (wand.isEmpty()) {
            return WandAffinity.NONE;
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        WandWood wood = WandDataHelper.getWood(wand);
        boolean attuned = WandDataHelper.isAttuned(wand);
        
        if (core == null || wood == null) {
            return WandAffinity.NONE;
        }
        
        return WandAffinity.getAffinity(core, wood, attuned);
    }
    
    /**
     * Applies cooldown modifier from wand affinity.
     * @param baseCooldown The base cooldown in ticks
     * @param affinity The wand affinity
     * @return Modified cooldown in ticks
     */
    public static int applyCooldownModifier(int baseCooldown, WandAffinity affinity) {
        return Math.max(1, (int) (baseCooldown * affinity.cooldownModifier()));
    }
    
    /**
     * Checks if a spell miscasts based on wand affinity.
     * @param affinity The wand affinity
     * @param random Random source
     * @return true if the spell miscasts
     */
    public static boolean checkMiscast(WandAffinity affinity, RandomSource random) {
        if (affinity.miscastChance() <= 0.0f) {
            return false;
        }
        
        // Apply stability bonus to reduce miscast chance
        float actualMiscastChance = Math.max(0.0f, affinity.miscastChance() - affinity.stabilityBonus());
        return random.nextFloat() < actualMiscastChance;
    }
    
    /**
     * Checks if a spell crits based on wand affinity.
     * @param affinity The wand affinity
     * @param random Random source
     * @return true if the spell crits
     */
    public static boolean checkCrit(WandAffinity affinity, RandomSource random) {
        if (affinity.critChanceBonus() <= 0.0f) {
            return false;
        }
        return random.nextFloat() < affinity.critChanceBonus();
    }
    
    /**
     * Applies power modifier from wand affinity.
     * @param basePower The base power value
     * @param affinity The wand affinity
     * @return Modified power value
     */
    public static float applyPowerModifier(float basePower, WandAffinity affinity) {
        return basePower * affinity.powerModifier();
    }
}

