package at.koopro.spells_n_squares.features.artifact.client;

import at.koopro.spells_n_squares.features.artifact.PhilosophersStoneItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Renderer for the Philosopher's Stone item using GeckoLib.
 * GeckoLib automatically handles semi-transparent textures if the texture has an alpha channel.
 * Make sure your texture file (philosophers_stone.png) has transparency enabled.
 */
public class PhilosophersStoneItemRenderer extends GeoItemRenderer<PhilosophersStoneItem> {
    public PhilosophersStoneItemRenderer() {
        super(new PhilosophersStoneItemModel());
    }
}
