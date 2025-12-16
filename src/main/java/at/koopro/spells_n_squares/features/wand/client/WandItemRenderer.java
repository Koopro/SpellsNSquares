package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.features.wand.WandItem;
import at.koopro.spells_n_squares.features.spell.client.LumosGeoLayer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WandItemRenderer extends GeoItemRenderer<WandItem> {
    public WandItemRenderer() {
        super(new WandItemModel());
        this.withRenderLayer(new LumosGeoLayer<>(this));
    }
}
