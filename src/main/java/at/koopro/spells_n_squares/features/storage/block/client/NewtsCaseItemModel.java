package at.koopro.spells_n_squares.features.storage.block.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * GeoModel for Newt's Case item.
 */
public class NewtsCaseItemModel extends GeoModel<NewtsCaseBlockItem> {
    // Use item-specific geo model
    private static final Identifier MODEL = ModIdentifierHelper.modId("newts_case_item");
    // Use item-specific texture
    private static final Identifier TEXTURE = ModIdentifierHelper.modId("textures/item/newts_case.png");
    // Use item-specific animation file
    private static final Identifier ANIMATION = ModIdentifierHelper.modId("newts_case_item");
    
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(NewtsCaseBlockItem animatable) {
        return ANIMATION;
    }
}

