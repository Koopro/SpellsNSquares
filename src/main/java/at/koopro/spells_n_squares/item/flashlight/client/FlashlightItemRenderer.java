package at.koopro.spells_n_squares.item.flashlight.client;

import at.koopro.spells_n_squares.item.flashlight.FlashlightItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FlashlightItemRenderer extends GeoItemRenderer<FlashlightItem> {
    public FlashlightItemRenderer() {
        super(new FlashlightItemModel());
    }
}

