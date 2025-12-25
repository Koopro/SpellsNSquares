package at.koopro.spells_n_squares.features.education.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;

/**
 * House points hourglass block that displays house points.
 */
public class HousePointsHourglassBlock extends BaseInteractiveBlock {
    
    public HousePointsHourglassBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.house_points.display";
    }
}








