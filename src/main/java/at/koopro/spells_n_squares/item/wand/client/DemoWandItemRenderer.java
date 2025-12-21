package at.koopro.spells_n_squares.item.wand.client;

import at.koopro.spells_n_squares.item.client.LumosGeoLayer;
import at.koopro.spells_n_squares.item.wand.DemoWandItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DemoWandItemRenderer extends GeoItemRenderer<DemoWandItem> {
    public DemoWandItemRenderer() {
        super(new DemoWandItemModel());
        this.withRenderLayer(new LumosGeoLayer<>(this));
    }
}

