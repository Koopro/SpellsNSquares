package at.koopro.spells_n_squares.block.economy;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;

/**
 * Automated shop block for buying/selling items (no NPCs).
 */
public class AutomatedShopBlock extends BaseInteractiveBlock {
    
    public AutomatedShopBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.shop.description";
    }
}






