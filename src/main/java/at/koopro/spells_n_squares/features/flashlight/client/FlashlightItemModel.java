package at.koopro.spells_n_squares.features.flashlight.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FlashlightItemModel extends GeoModel<FlashlightItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("flashlight");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/flashlight.png");
    }

    @Override
    public Identifier getAnimationResource(FlashlightItem animatable) {
        // No animations for flashlight, but method must return something
        return ModIdentifierHelper.modId("flashlight");
    }
}
