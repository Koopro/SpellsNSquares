package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Renderer for the lightning beam entity.
 * Builds a jagged mesh between entity position (wand tip) and the stored end point.
 */
public class LightningBeamRenderer extends EntityRenderer<LightningBeamEntity, LightningBeamRenderer.BeamRenderState> {

    public LightningBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BeamRenderState createRenderState() {
        return new BeamRenderState();
    }

    @Override
    public void extractRenderState(LightningBeamEntity entity, BeamRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // Start is entity position (wand tip), end is stored in entity data
        var start = entity.position();
        var end = entity.getEnd();
        var delta = end.subtract(start);

        state.dx = (float) delta.x;
        state.dy = (float) delta.y;
        state.dz = (float) delta.z;
        state.color = entity.getColor();
    }

    @Override
    public void submit(BeamRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();

                // In local space, entity origin is the wand tip (start of beam).
                // Render a white cuboid lightning core along the stored beam direction.
                renderBeam(buffer, ps, renderState, RendererConstants.COLOR_LIGHTNING_BEAM);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(BeamRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }

    private static void renderBeam(com.mojang.blaze3d.vertex.VertexConsumer buffer, PoseStack poseStack, BeamRenderState state, int color) {
        // Render a jagged beam along the delta stored in the render state as a thin cuboid
        Matrix4f m = new Matrix4f(poseStack.last().pose());

        Vector3f delta = new Vector3f(state.dx, state.dy, state.dz);
        float length = delta.length();
        if (length < RendererConstants.LIGHTNING_BEAM_MIN_LENGTH) {
            return;
        }

        Vector3f dirNorm = new Vector3f(delta).div(length);

        int segments = RendererConstants.LIGHTNING_BEAM_SEGMENTS;
        float thickness = RendererConstants.LIGHTNING_BEAM_THICKNESS;

        Vector3f[] points = new Vector3f[segments + 1];
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / (float) segments;
            // Base point along the straight line
            Vector3f base = new Vector3f(dirNorm).mul(t * length);

            // Small animated offset for "snake" motion
            float wobble = (float) Math.sin((t + state.ageInTicks * RendererConstants.LIGHTNING_BEAM_WOBBLE_SPEED_1) * RendererConstants.LIGHTNING_BEAM_WOBBLE_FREQ_1) * RendererConstants.LIGHTNING_BEAM_WOBBLE_AMPLITUDE;
            float wobble2 = (float) Math.cos((t + state.ageInTicks * RendererConstants.LIGHTNING_BEAM_WOBBLE_SPEED_2) * RendererConstants.LIGHTNING_BEAM_WOBBLE_FREQ_2) * RendererConstants.LIGHTNING_BEAM_WOBBLE_AMPLITUDE;

            // We'll apply wobble in the cross-section basis later; for now store base,
            // wobble values will be used per segment.
            points[i] = new Vector3f(base.x, base.y, base.z);
        }

        for (int i = 0; i < segments; i++) {
            Vector3f p0 = points[i];
            Vector3f p1 = points[i + 1];

            Vector3f dir = new Vector3f(p1).sub(p0).normalize();

            // Build an orthonormal basis around the beam direction:
            // side1 and side2 span the cuboid cross-section.
            Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
            if (Math.abs(dir.y) > 0.9f) {
                worldUp.set(1.0f, 0.0f, 0.0f);
            }

            Vector3f side1 = new Vector3f(worldUp).cross(dir).normalize(thickness);
            Vector3f side2 = new Vector3f(dir).cross(side1).normalize(thickness);

            // Four corners at each segment end (square cross section)
            Vector3f c0 = new Vector3f(p0).add(side1).add(side2);
            Vector3f c1 = new Vector3f(p0).add(side1).sub(side2);
            Vector3f c2 = new Vector3f(p0).sub(side1).sub(side2);
            Vector3f c3 = new Vector3f(p0).sub(side1).add(side2);

            Vector3f c4 = new Vector3f(p1).add(side1).add(side2);
            Vector3f c5 = new Vector3f(p1).add(side1).sub(side2);
            Vector3f c6 = new Vector3f(p1).sub(side1).sub(side2);
            Vector3f c7 = new Vector3f(p1).sub(side1).add(side2);

            // 4 side faces: (c0,c1,c5,c4), (c1,c2,c6,c5), (c2,c3,c7,c6), (c3,c0,c4,c7)
            RendererUtils.addQuad(buffer, m, c0, c1, c5, c4, color);
            RendererUtils.addQuad(buffer, m, c1, c2, c6, c5, color);
            RendererUtils.addQuad(buffer, m, c2, c3, c7, c6, color);
            RendererUtils.addQuad(buffer, m, c3, c0, c4, c7, color);
        }
    }

    /**
     * Custom render state carrying beam direction and color.
     */
    public static class BeamRenderState extends net.minecraft.client.renderer.entity.state.EntityRenderState {
        public float dx;
        public float dy;
        public float dz;
        public int color;
    }
}

