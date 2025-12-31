package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.transportation.BroomstickItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DemoBroomItemModel extends GeoModel<BroomstickItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("demo_broom");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/demo_broom.png");
    }

    @Override
    public Identifier getAnimationResource(BroomstickItem animatable) {
        return ModIdentifierHelper.modId("demo_broom");
    }
}



