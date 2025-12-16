package at.koopro.spells_n_squares.features.creatures.companion.ai;

import at.koopro.spells_n_squares.features.creatures.companion.NifflerEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/**
 * AI goal for Niffler to find and collect valuable items.
 */
public class NifflerFindTreasureGoal extends Goal {
    private final NifflerEntity niffler;
    private ItemEntity targetItem;
    private int collectCooldown = 0;
    private static final int COLLECT_COOLDOWN_TICKS = 40; // 2 seconds
    
    public NifflerFindTreasureGoal(NifflerEntity niffler) {
        this.niffler = niffler;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        if (!niffler.isTame() || niffler.isOrderedToSit()) {
            return false;
        }
        
        // Check if there's a target item
        targetItem = niffler.getTargetItem();
        return targetItem != null && targetItem.isAlive() && !targetItem.hasPickUpDelay();
    }
    
    @Override
    public boolean canContinueToUse() {
        if (!niffler.isTame() || niffler.isOrderedToSit()) {
            return false;
        }
        
        if (targetItem == null || !targetItem.isAlive() || targetItem.hasPickUpDelay()) {
            return false;
        }
        
        // Stop if too far from owner
        if (niffler.getOwnerId().isPresent()) {
            Player owner = niffler.level().getPlayerByUUID(niffler.getOwnerId().get());
            if (owner != null && niffler.distanceToSqr(owner) > 256.0) {
                return false; // Too far from owner
            }
        }
        
        return true;
    }
    
    @Override
    public void start() {
        collectCooldown = 0;
    }
    
    @Override
    public void tick() {
        if (targetItem == null || !targetItem.isAlive()) {
            return;
        }
        
        collectCooldown++;
        
        // Move towards the item
        double distance = niffler.distanceToSqr(targetItem);
        
        if (distance > 1.5 * 1.5) {
            // Move closer
            niffler.getNavigation().moveTo(targetItem, 1.0D);
            niffler.getLookControl().setLookAt(targetItem, 10.0F, niffler.getMaxHeadXRot());
        } else {
            // Close enough - try to collect
            if (collectCooldown >= COLLECT_COOLDOWN_TICKS) {
                if (niffler.collectItem(targetItem)) {
                    // Successfully collected
                    niffler.setTargetItem(null);
                    this.targetItem = null;
                }
                collectCooldown = 0;
            }
        }
    }
    
    @Override
    public void stop() {
        targetItem = null;
        niffler.getNavigation().stop();
    }
}




