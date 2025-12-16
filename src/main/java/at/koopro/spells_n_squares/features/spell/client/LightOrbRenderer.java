package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Lightweight renderer for the flying light orb.
 * Uses a simple cube-core + shell, emissive.
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

        float time = renderState.ageInTicks;

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                ps.translate(0.0f, RendererConstants.LIGHT_ORB_TRANSLATE_Y, 0.0f);
                ps.mulPose(Axis.YP.rotation(time * RendererConstants.LIGHT_ORB_ROTATION_SPEED));
                ps.mulPose(Axis.XP.rotation(Mth.sin(time * RendererConstants.LIGHT_ORB_BOB_SPEED) * RendererConstants.LIGHT_ORB_BOB_AMPLITUDE));

                RendererUtils.renderCube(buffer, ps, RendererConstants.LIGHT_ORB_CORE_SIZE, RendererConstants.COLOR_LIGHT_ORB_CORE);
                RendererUtils.renderCube(buffer, ps, RendererConstants.LIGHT_ORB_SHELL_SIZE, RendererConstants.COLOR_LIGHT_ORB_SHELL);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}
