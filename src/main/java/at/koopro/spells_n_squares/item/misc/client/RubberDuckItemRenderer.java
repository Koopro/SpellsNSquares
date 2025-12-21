package at.koopro.spells_n_squares.item.misc.client;

import at.koopro.spells_n_squares.item.misc.RubberDuckItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RubberDuckItemRenderer extends GeoItemRenderer<RubberDuckItem> {
    public RubberDuckItemRenderer() {
        super(new RubberDuckItemModel());
    }
}

