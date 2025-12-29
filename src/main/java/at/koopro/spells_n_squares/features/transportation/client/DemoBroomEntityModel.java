package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DemoBroomEntityModel extends GeoModel<BroomEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("demo_broom_entity");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/entity/demo_broom.png");
    }

    @Override
    public Identifier getAnimationResource(BroomEntity animatable) {
        return ModIdentifierHelper.modId("demo_broom.animation");
    }
}

