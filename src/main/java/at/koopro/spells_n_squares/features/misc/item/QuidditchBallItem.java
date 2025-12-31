package at.koopro.spells_n_squares.features.misc.item;

import net.minecraft.world.item.Item;

/**
 * Base class for Quidditch ball items.
 */
public class QuidditchBallItem extends Item {
    private final BallType ballType;
    
    public QuidditchBallItem(Properties properties, BallType ballType) {
        super(properties);
        this.ballType = ballType;
    }
    
    public BallType getBallType() {
        return ballType;
    }
    
    public enum BallType {
        QUAFFLE,
        BLUDGER,
        SNITCH
    }
}
