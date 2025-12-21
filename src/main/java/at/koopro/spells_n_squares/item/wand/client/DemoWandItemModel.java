package at.koopro.spells_n_squares.item.wand.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.item.wand.DemoWandItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DemoWandItemModel extends GeoModel<DemoWandItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("demo_wand");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ModIdentifierHelper.modId("textures/item/demo_wand.png");
    }

    @Override
    public Identifier getAnimationResource(DemoWandItem animatable) {
        // No animations for demo wand, but method must return something
        return ModIdentifierHelper.modId("demo_wand");
    }
}

