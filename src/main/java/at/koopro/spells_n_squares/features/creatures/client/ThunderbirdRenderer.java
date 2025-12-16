package at.koopro.spells_n_squares.features.creatures.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.creatures.mount.ThunderbirdEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Thunderbird entity - a large bird with storm powers.
 */
public class ThunderbirdRenderer extends EntityRenderer<ThunderbirdEntity, EntityRenderState> {
    
    public ThunderbirdRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(ThunderbirdEntity entity, EntityRenderState state, float partialTick) {
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
                
                // Large bird body (brown/gold colored)
                RendererUtils.renderCube(buffer, ps, half * 0.9f, 0xFF8B6914);
                
                // Wings
                ps.pushPose();
                ps.translate(half * 0.6f, 0, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.5f, 0xFF6B4A04);
                ps.popPose();
                
                ps.pushPose();
                ps.translate(-half * 0.6f, 0, 0);
                RendererUtils.renderCube(buffer, ps, half * 0.5f, 0xFF6B4A04);
                ps.popPose();
                
                ps.popPose();
            }
        );
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}




