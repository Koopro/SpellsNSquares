package at.koopro.spells_n_squares.features.artifact.client;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.artifact.PhilosophersStoneItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Model for the Philosopher's Stone item.
 * Expects model file at: assets/spells_n_squares/geckolib/models/philosophers_stone.geo.json
 * Expects texture at: assets/spells_n_squares/textures/item/philosophers_stone.png
 */
public class PhilosophersStoneItemModel extends GeoModel<PhilosophersStoneItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("philosophers_stone");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/philosophers_stone.png");
    }

    @Override
    public Identifier getAnimationResource(PhilosophersStoneItem animatable) {
        // Return model resource if no separate animation file exists
        // If you have a separate animation file, use: ModIdentifierHelper.modId("geckolib/animations/philosophers_stone")
        return ModIdentifierHelper.modId("philosophers_stone");
    }
}

