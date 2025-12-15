package at.koopro.spells_n_squares.item.client;

import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.item.RubberDuckItem;

public class RubberDuckItemModel extends GeoModel<RubberDuckItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "rubber_duck");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "textures/item/rubber_duck.png");
    }

    @Override
    public Identifier getAnimationResource(RubberDuckItem animatable) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "rubber_duck");
    }
}
