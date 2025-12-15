package at.koopro.spells_n_squares.item.client;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import at.koopro.spells_n_squares.features.wand.DemoWandItem;

public class DemoWandItemRenderer extends GeoItemRenderer<DemoWandItem> {
    public DemoWandItemRenderer() {
        super(new DemoWandItemModel());
        this.withRenderLayer(new LumosGeoLayer<>(this));
    }
}

