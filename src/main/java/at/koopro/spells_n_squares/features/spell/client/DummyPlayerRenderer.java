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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity;

/**
 * Very simple cuboid-based humanoid renderer for DummyPlayerEntity.
 * Not a full player model, but good enough as a visual target dummy.
 */
public class DummyPlayerRenderer extends EntityRenderer<DummyPlayerEntity, EntityRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "textures/misc/lumos_white.png");

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
            RenderTypes.entityTranslucentEmissive(TEXTURE),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();

                // Center model on entity position
                ps.translate(0.0f, 0.0f, 0.0f);

                // Body cuboid (torso)
                renderCube(buffer, ps, 0.25f, 0.5f, 0.15f, 0.5f, 0xFF80D8FF);
                // Head cuboid on top
                ps.translate(0.0f, 0.75f, 0.0f);
                renderCube(buffer, ps, 0.2f, 0.2f, 0.2f, 0.5f, 0xFFFFFFFF);

                ps.popPose();
            }
        );
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }

    private static void renderCube(VertexConsumer buffer, PoseStack poseStack, float halfX, float halfY, float halfZ, float yOffset, int color) {
        Matrix4f m = new Matrix4f(poseStack.last().pose());
        java.util.function.Function<Vector3f, Vector4f> t = v -> new Vector4f(v.x, v.y + yOffset, v.z, 1.0f).mul(m);

        // -X
        Vector4f a1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f a2 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f a3 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        Vector4f a4 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        buffer.addVertex(a1.x(), a1.y(), a1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, -1, 0, 0);
        buffer.addVertex(a2.x(), a2.y(), a2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, -1, 0, 0);
        buffer.addVertex(a3.x(), a3.y(), a3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, -1, 0, 0);
        buffer.addVertex(a4.x(), a4.y(), a4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, -1, 0, 0);

        // +X
        Vector4f b1 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        Vector4f b2 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f b3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f b4 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        buffer.addVertex(b1.x(), b1.y(), b1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 1, 0, 0);
        buffer.addVertex(b2.x(), b2.y(), b2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 1, 0, 0);
        buffer.addVertex(b3.x(), b3.y(), b3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 1, 0, 0);
        buffer.addVertex(b4.x(), b4.y(), b4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 1, 0, 0);

        // -Y
        Vector4f c1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f c2 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        Vector4f c3 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        Vector4f c4 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        buffer.addVertex(c1.x(), c1.y(), c1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, -1, 0);
        buffer.addVertex(c2.x(), c2.y(), c2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, -1, 0);
        buffer.addVertex(c3.x(), c3.y(), c3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, -1, 0);
        buffer.addVertex(c4.x(), c4.y(), c4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, -1, 0);

        // +Y
        Vector4f d1 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f d2 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f d3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f d4 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        buffer.addVertex(d1.x(), d1.y(), d1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 1, 0);
        buffer.addVertex(d2.x(), d2.y(), d2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 1, 0);
        buffer.addVertex(d3.x(), d3.y(), d3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 1, 0);
        buffer.addVertex(d4.x(), d4.y(), d4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 1, 0);

        // -Z
        Vector4f e1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f e2 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f e3 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f e4 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        buffer.addVertex(e1.x(), e1.y(), e1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, -1);
        buffer.addVertex(e2.x(), e2.y(), e2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, -1);
        buffer.addVertex(e3.x(), e3.y(), e3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, -1);
        buffer.addVertex(e4.x(), e4.y(), e4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, -1);

        // +Z
        Vector4f f1 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        Vector4f f2 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        Vector4f f3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f f4 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        buffer.addVertex(f1.x(), f1.y(), f1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(f2.x(), f2.y(), f2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(f3.x(), f3.y(), f3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
        buffer.addVertex(f4.x(), f4.y(), f4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, 0xF000F0, 0, 0, 1);
    }
}
