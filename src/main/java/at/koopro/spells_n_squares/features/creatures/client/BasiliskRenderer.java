package at.koopro.spells_n_squares.features.creatures.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.creatures.hostile.BasiliskEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Basilisk entity - a giant serpent with petrifying gaze.
 */
public class BasiliskRenderer extends EntityRenderer<BasiliskEntity, EntityRenderState> {
    
    public BasiliskRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(BasiliskEntity entity, EntityRenderState state, float partialTick) {
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
                
                // Giant serpent body (green/dark colored)
                RendererUtils.renderCube(buffer, ps, half * 1.5f, 0xFF2A4B2A);
                
                // Large head
                ps.pushPose();
                ps.translate(0, 0, -half * 0.8f);
                RendererUtils.renderCube(buffer, ps, half * 0.8f, 0xFF1A3B1A);
                ps.popPose();
                
                // Glowing eyes
                ps.pushPose();
                ps.translate(half * 0.3f, half * 0.2f, -half * 0.9f);
                RendererUtils.renderCube(buffer, ps, half * 0.15f, 0xFFFF0000);
                ps.popPose();
                
                ps.pushPose();
                ps.translate(-half * 0.3f, half * 0.2f, -half * 0.9f);
                RendererUtils.renderCube(buffer, ps, half * 0.15f, 0xFFFF0000);
                ps.popPose();
                
                ps.popPose();
            }
        );
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}







