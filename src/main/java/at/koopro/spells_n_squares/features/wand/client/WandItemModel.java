package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.wand.WandItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WandItemModel extends GeoModel<WandItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("demo_wand");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/demo_wand.png");
    }

    @Override
    public Identifier getAnimationResource(WandItem animatable) {
        // No animations for wand, but method must return something
        return ModIdentifierHelper.modId("demo_wand");
    }
}








