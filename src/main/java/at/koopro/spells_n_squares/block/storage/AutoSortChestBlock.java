package at.koopro.spells_n_squares.block.storage;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;

/**
 * Auto-sorting chest block that automatically organizes items.
 */
public class AutoSortChestBlock extends BaseInteractiveBlock {
    
    public AutoSortChestBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.chest.description";
    }
}

