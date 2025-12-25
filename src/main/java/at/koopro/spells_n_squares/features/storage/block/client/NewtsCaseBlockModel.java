package at.koopro.spells_n_squares.features.storage.block.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * GeoModel for Newt's Case block entity.
 */
public class NewtsCaseBlockModel extends GeoModel<NewtsCaseBlockEntity> {
    private static final Identifier MODEL = ModIdentifierHelper.modId("newts_case");
    private static final Identifier TEXTURE = ModIdentifierHelper.modId("textures/block/newts_case.png");
    private static final Identifier ANIMATION = ModIdentifierHelper.modId("newts_case");
    
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(NewtsCaseBlockEntity animatable) {
        return ANIMATION;
    }
}

