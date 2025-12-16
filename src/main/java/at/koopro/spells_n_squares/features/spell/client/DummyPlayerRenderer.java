package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Very simple cuboid-based humanoid renderer for DummyPlayerEntity.
 * Not a full player model, but good enough as a visual target dummy.
 */
public class DummyPlayerRenderer extends EntityRenderer<DummyPlayerEntity, EntityRenderState> {

    public DummyPlayerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void extractRenderState(DummyPlayerEntity entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();

                // Center model on entity position
                ps.translate(0.0f, 0.0f, 0.0f);

                // Body cuboid (torso)
                RendererUtils.renderCube(buffer, ps, 
                    RendererConstants.DUMMY_BODY_WIDTH, 
                    RendererConstants.DUMMY_BODY_HEIGHT, 
                    RendererConstants.DUMMY_BODY_DEPTH, 
                    RendererConstants.DUMMY_BODY_Y_OFFSET, 
                    RendererConstants.COLOR_DUMMY_BODY);
                // Head cuboid on top
                ps.translate(0.0f, 0.75f, 0.0f);
                RendererUtils.renderCube(buffer, ps, 
                    RendererConstants.DUMMY_HEAD_SIZE, 
                    RendererConstants.DUMMY_HEAD_SIZE, 
                    RendererConstants.DUMMY_HEAD_SIZE, 
                    RendererConstants.DUMMY_HEAD_Y_OFFSET, 
                    RendererConstants.COLOR_DUMMY_HEAD);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}
