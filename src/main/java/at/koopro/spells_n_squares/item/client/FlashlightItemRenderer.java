package at.koopro.spells_n_squares.item.client;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;

public class FlashlightItemRenderer extends GeoItemRenderer<FlashlightItem> {
    public FlashlightItemRenderer() {
        super(new FlashlightItemModel());
    }
}
