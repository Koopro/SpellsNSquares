package at.koopro.spells_n_squares.features.economy.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;

/**
 * Trading post block for player-to-player trading.
 */
public class TradingPostBlock extends BaseInteractiveBlock {
    
    public TradingPostBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.trading_post.description";
    }
}



