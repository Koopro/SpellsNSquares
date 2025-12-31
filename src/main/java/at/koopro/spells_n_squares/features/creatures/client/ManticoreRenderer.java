package at.koopro.spells_n_squares.features.creatures.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.creatures.hostile.ManticoreEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;

/**
 * Renderer for Manticore entity - lion body, human head, scorpion tail.
 */
public class ManticoreRenderer extends EntityRenderer<ManticoreEntity, EntityRenderState> {
    
    public ManticoreRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(ManticoreEntity entity, EntityRenderState state, float partialTick) {
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
                
                // Lion body
                RendererUtils.renderCube(buffer, ps, half * 0.7f, 0xFFFFA500);
                
                // Human head
                ps.pushPose();
                ps.translate(0, half * 0.5f, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.3f, 0xFFFFDBB3);
                ps.popPose();
                
                // Scorpion tail
                ps.pushPose();
                ps.translate(0, -half * 0.3f, -half * 0.5f);
                RendererUtils.renderCube(buffer, ps, half * 0.2f, half * 0.6f, half * 0.2f, 0, 0xFF8B0000);
                ps.popPose();
                
                ps.popPose();
            }
        );
    }
}
















