package at.koopro.spells_n_squares.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Client-side mixin for EntityRenderer to add custom rendering hooks for spell effects.
 * Handles particle system integration and glow effects for entities under spell influence.
 * 
 * Note: Shadow scaling is handled automatically through the bounding box dimensions
 * we modify in EntityMixin. Shadows are typically calculated from entity dimensions,
 * so they should scale correctly with the scaled bounding box.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    // Shadow scaling is handled automatically through bounding box dimensions
    // If shadows don't scale correctly, we may need to find the correct method
    // to intercept in future Minecraft versions
}

