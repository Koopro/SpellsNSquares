package at.koopro.spells_n_squares.item.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;

public class FlashlightItemModel extends GeoModel<FlashlightItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "flashlight");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "textures/item/flashlight.png");
    }

    @Override
    public Identifier getAnimationResource(FlashlightItem animatable) {
        // No animations for flashlight, but method must return something
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "flashlight");
    }
}
