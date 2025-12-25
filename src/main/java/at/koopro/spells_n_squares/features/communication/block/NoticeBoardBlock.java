package at.koopro.spells_n_squares.features.communication.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;

/**
 * Notice board block for server-wide announcements.
 */
public class NoticeBoardBlock extends BaseInteractiveBlock {
    
    public NoticeBoardBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.notice_board.description";
    }
}






