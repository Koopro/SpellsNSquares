package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.features.wand.WandItem;
// TODO: Re-enable when LumosGeoLayer is implemented
// import at.koopro.spells_n_squares.domain.spells.presentation.client.LumosGeoLayer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WandItemRenderer extends GeoItemRenderer<WandItem> {
    public WandItemRenderer() {
        super(new WandItemModel());
        // TODO: Re-enable when LumosGeoLayer is implemented
        // this.withRenderLayer(new LumosGeoLayer<>(this));
    }
}








