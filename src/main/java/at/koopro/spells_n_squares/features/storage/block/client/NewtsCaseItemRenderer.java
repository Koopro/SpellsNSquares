package at.koopro.spells_n_squares.features.storage.block.client;

import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Renderer for Newt's Case item with GeckoLib animations.
 */
public class NewtsCaseItemRenderer extends GeoItemRenderer<NewtsCaseBlockItem> {
    public NewtsCaseItemRenderer() {
        super(new NewtsCaseItemModel());
    }
}


