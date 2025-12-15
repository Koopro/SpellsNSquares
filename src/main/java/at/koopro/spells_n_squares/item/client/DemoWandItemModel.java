package at.koopro.spells_n_squares.item.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import at.koopro.spells_n_squares.features.wand.DemoWandItem;

public class DemoWandItemModel extends GeoModel<DemoWandItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "demo_wand");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "textures/item/demo_wand.png");
    }

    @Override
    public Identifier getAnimationResource(DemoWandItem animatable) {
        // No animations for demo wand, but method must return something
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "demo_wand");
    }
}
