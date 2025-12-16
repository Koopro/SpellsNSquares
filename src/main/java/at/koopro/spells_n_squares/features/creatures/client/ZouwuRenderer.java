package at.koopro.spells_n_squares.features.creatures.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.creatures.mount.ZouwuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Zouwu entity - a fast cat-like mount with teleportation.
 */
public class ZouwuRenderer extends EntityRenderer<ZouwuEntity, EntityRenderState> {
    
    public ZouwuRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(ZouwuEntity entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }
    
    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);
        
        float half = Math.max(renderState.boundingBoxWidth, renderState.boundingBoxHeight) * 0.5f;
        
        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucent(RendererConstants.GLOW_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                
                // Cat-like body (colorful/striped)
                RendererUtils.renderCube(buffer, ps, half * 0.9f, 0xFF8B4513);
                
                // Long tail
                ps.pushPose();
                ps.translate(0, -half * 0.3f, half * 0.8f);
                RendererUtils.renderCube(buffer, ps, half * 0.3f, 0xFF654321);
                ps.popPose();
                
                // Ears
                ps.pushPose();
                ps.translate(half * 0.3f, half * 0.4f, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.2f, 0xFF6B3410);
                ps.popPose();
                
                ps.pushPose();
                ps.translate(-half * 0.3f, half * 0.4f, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.2f, 0xFF6B3410);
                ps.popPose();
                
                ps.popPose();
            }
        );
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}




