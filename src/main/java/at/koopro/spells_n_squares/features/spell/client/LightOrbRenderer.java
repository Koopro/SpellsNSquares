package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.core.client.ShaderRenderHelper;
import at.koopro.spells_n_squares.features.fx.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Light Orb entity.
 *
 * Uses {@link ShaderRenderHelper} to prefer the lumos orb shader when available,
 * with a clean fallback to the original texture-based glow.
 */
public class LightOrbRenderer extends EntityRenderer<LightOrbEntity, EntityRenderState> {
    
    public LightOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(LightOrbEntity entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }
    
    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);
        
        float half = Math.max(renderState.boundingBoxWidth, renderState.boundingBoxHeight) * 0.5f;

        // Optionally update shader time uniform via the helper (lightweight hook).
        ShaderRenderHelper.updateTimeUniform(ShaderEffectHandler.LUMOS_ORB_SHADER);
        
        collector.submitCustomGeometry(
            poseStack,
            // Prefer lumos orb shader when available; otherwise use the original glow texture.
            ShaderRenderHelper.getLumosOrbRenderType(),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                
                // Light orb (yellow/white glow)
                RendererUtils.renderCube(buffer, ps, half, 0xFFFFFF00);
                
                ps.popPose();
            }
        );
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}










