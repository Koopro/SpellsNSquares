package at.koopro.spells_n_squares.features.economy.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;

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






