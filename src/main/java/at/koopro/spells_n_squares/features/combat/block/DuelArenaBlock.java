package at.koopro.spells_n_squares.features.combat.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;

/**
 * Dueling arena block for initiating duels.
 */
public class DuelArenaBlock extends BaseInteractiveBlock {
    
    public DuelArenaBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.duel.arena.description";
    }
}






