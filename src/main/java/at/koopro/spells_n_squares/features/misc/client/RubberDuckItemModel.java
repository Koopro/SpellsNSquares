package at.koopro.spells_n_squares.features.misc.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.misc.RubberDuckItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RubberDuckItemModel extends GeoModel<RubberDuckItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("rubber_duck");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/rubber_duck.png");
    }

    @Override
    public Identifier getAnimationResource(RubberDuckItem animatable) {
        return ModIdentifierHelper.modId("rubber_duck");
    }
}








