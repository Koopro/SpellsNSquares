package at.koopro.spells_n_squares.features.spell.client;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;

/**
 * Renderer for the lightning beam entity.
 * Builds a jagged mesh between entity position (wand tip) and the stored end point.
 */
public class LightningBeamRenderer extends EntityRenderer<LightningBeamEntity, LightningBeamRenderer.BeamRenderState> {

    private static final Identifier BEAM_TEXTURE = Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "textures/misc/lumos_white.png");

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
            RenderTypes.entityTranslucentEmissive(BEAM_TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();

                // In local space, entity origin is the wand tip (start of beam).
                // Render a white cuboid lightning core along the stored beam direction.
                renderBeam(buffer, ps, renderState, 0xFFFFFFFF);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(BeamRenderState state) {
        return BEAM_TEXTURE;
    }

    private static void renderBeam(VertexConsumer buffer, PoseStack poseStack, BeamRenderState state, int color) {
        // Render a jagged beam along the delta stored in the render state as a thin cuboid
        Matrix4f m = new Matrix4f(poseStack.last().pose());

        Vector3f delta = new Vector3f(state.dx, state.dy, state.dz);
        float length = delta.length();
        if (length < 0.01f) {
            return;
        }

        Vector3f dirNorm = new Vector3f(delta).div(length);

        int segments = 10;
        float thickness = 0.04f;

        Vector3f[] points = new Vector3f[segments + 1];
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / (float) segments;
            // Base point along the straight line
            Vector3f base = new Vector3f(dirNorm).mul(t * length);

            // Small animated offset for "snake" motion
            float wobble = (float) Math.sin((t + state.ageInTicks * 0.08f) * 10.0f) * 0.15f;
            float wobble2 = (float) Math.cos((t + state.ageInTicks * 0.05f) * 11.0f) * 0.15f;

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
            addQuad(buffer, m, c0, c1, c5, c4, color);
            addQuad(buffer, m, c1, c2, c6, c5, color);
            addQuad(buffer, m, c2, c3, c7, c6, color);
            addQuad(buffer, m, c3, c0, c4, c7, color);
        }
    }

    private static void addQuad(VertexConsumer buffer, Matrix4f m, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, int color) {
        Vector4f p0 = new Vector4f(v0, 1.0f).mul(m);
        Vector4f p1 = new Vector4f(v1, 1.0f).mul(m);
        Vector4f p2 = new Vector4f(v2, 1.0f).mul(m);
        Vector4f p3 = new Vector4f(v3, 1.0f).mul(m);

        // Single sided; if we want double-sided, we could add reversed order as well
        buffer.addVertex(p0.x(), p0.y(), p0.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(p1.x(), p1.y(), p1.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(p2.x(), p2.y(), p2.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(p3.x(), p3.y(), p3.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
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

