package at.koopro.spells_n_squares.features.creatures.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.creatures.companion.DemiguiseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;

/**
 * Renderer for Demiguise entity - ape-like creature that can turn invisible.
 */
public class DemiguiseRenderer extends EntityRenderer<DemiguiseEntity, EntityRenderState> {
    
    public DemiguiseRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(DemiguiseEntity entity, EntityRenderState state, float partialTick) {
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
                
                // Ape-like body (silver/gray)
                int color = 0xFFC0C0C0; // Silver-gray
                RendererUtils.renderCube(buffer, ps, half * 0.6f, half * 0.8f, half * 0.5f, 0, color);
                
                // Head
                ps.pushPose();
                ps.translate(0, half * 0.7f, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.4f, 0xFFB0B0B0);
                ps.popPose();
                
                // Arms
                ps.pushPose();
                ps.translate(half * 0.5f, 0, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.2f, half * 0.6f, half * 0.2f, 0, color);
                ps.popPose();
                
                ps.pushPose();
                ps.translate(-half * 0.5f, 0, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.2f, half * 0.6f, half * 0.2f, 0, color);
                ps.popPose();
                
                ps.popPose();
            }
        );
    }
}
















