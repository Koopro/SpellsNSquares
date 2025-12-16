package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Emissive visual for the protective shield orb: larger core/shell glow.
 */
public class ShieldOrbRenderer extends EntityRenderer<ShieldOrbEntity, EntityRenderState> {

    public ShieldOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);

        float half = Math.max(renderState.boundingBoxWidth, renderState.boundingBoxHeight) * 1f;

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();

                // Single large blue shell, no inner core, no spin/hover
                RendererUtils.renderCube(buffer, ps, half, RendererConstants.COLOR_SHIELD_ORB);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}
