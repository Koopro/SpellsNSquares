package at.koopro.spells_n_squares.item.client;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import at.koopro.spells_n_squares.item.RubberDuckItem;

public class RubberDuckItemRenderer extends GeoItemRenderer<RubberDuckItem> {
    public RubberDuckItemRenderer() {
        super(new RubberDuckItemModel());
    }
}
