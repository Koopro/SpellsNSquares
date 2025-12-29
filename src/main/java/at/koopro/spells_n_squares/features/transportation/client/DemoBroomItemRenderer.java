package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.features.transportation.BroomstickItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DemoBroomItemRenderer extends GeoItemRenderer<BroomstickItem> {
    public DemoBroomItemRenderer() {
        super(new DemoBroomItemModel());
    }
}


