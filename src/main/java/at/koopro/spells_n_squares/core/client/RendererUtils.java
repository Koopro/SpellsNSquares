package at.koopro.spells_n_squares.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Utility class for common rendering operations.
 * Extracts shared rendering logic to reduce code duplication.
 */
public final class RendererUtils {
    private RendererUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Renders a cube with uniform size (same half-size for all dimensions).
     * @param buffer The vertex buffer to write to
     * @param poseStack The pose stack for transformations
     * @param half The half-size of the cube (cube will be 2*half in each dimension)
     * @param color The color of the cube (ARGB format)
     */
    public static void renderCube(VertexConsumer buffer, PoseStack poseStack, float half, int color) {
        renderCube(buffer, poseStack, half, half, half, 0.0f, color);
    }
    
    /**
     * Renders a cuboid with specified dimensions.
     * @param buffer The vertex buffer to write to
     * @param poseStack The pose stack for transformations
     * @param halfX Half-width in X direction
     * @param halfY Half-height in Y direction
     * @param halfZ Half-depth in Z direction
     * @param yOffset Vertical offset for the cuboid
     * @param color The color of the cuboid (ARGB format)
     */
    public static void renderCube(VertexConsumer buffer, PoseStack poseStack, float halfX, float halfY, float halfZ, float yOffset, int color) {
        Matrix4f m = new Matrix4f(poseStack.last().pose());
        java.util.function.Function<Vector3f, Vector4f> t = v -> new Vector4f(v.x, v.y + yOffset, v.z, 1.0f).mul(m);

        // -X face
        Vector4f a1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f a2 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f a3 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        Vector4f a4 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        buffer.addVertex(a1.x(), a1.y(), a1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, -1, 0, 0);
        buffer.addVertex(a2.x(), a2.y(), a2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, -1, 0, 0);
        buffer.addVertex(a3.x(), a3.y(), a3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, -1, 0, 0);
        buffer.addVertex(a4.x(), a4.y(), a4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, -1, 0, 0);

        // +X face
        Vector4f b1 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        Vector4f b2 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f b3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f b4 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        buffer.addVertex(b1.x(), b1.y(), b1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 1, 0, 0);
        buffer.addVertex(b2.x(), b2.y(), b2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 1, 0, 0);
        buffer.addVertex(b3.x(), b3.y(), b3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 1, 0, 0);
        buffer.addVertex(b4.x(), b4.y(), b4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 1, 0, 0);

        // -Y face
        Vector4f c1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f c2 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        Vector4f c3 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        Vector4f c4 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        buffer.addVertex(c1.x(), c1.y(), c1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, -1, 0);
        buffer.addVertex(c2.x(), c2.y(), c2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, -1, 0);
        buffer.addVertex(c3.x(), c3.y(), c3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, -1, 0);
        buffer.addVertex(c4.x(), c4.y(), c4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, -1, 0);

        // +Y face
        Vector4f d1 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f d2 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f d3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f d4 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        buffer.addVertex(d1.x(), d1.y(), d1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 1, 0);
        buffer.addVertex(d2.x(), d2.y(), d2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 1, 0);
        buffer.addVertex(d3.x(), d3.y(), d3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 1, 0);
        buffer.addVertex(d4.x(), d4.y(), d4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 1, 0);

        // -Z face
        Vector4f e1 = t.apply(new Vector3f(-halfX, -halfY, -halfZ));
        Vector4f e2 = t.apply(new Vector3f(-halfX, halfY, -halfZ));
        Vector4f e3 = t.apply(new Vector3f(halfX, halfY, -halfZ));
        Vector4f e4 = t.apply(new Vector3f(halfX, -halfY, -halfZ));
        buffer.addVertex(e1.x(), e1.y(), e1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, -1);
        buffer.addVertex(e2.x(), e2.y(), e2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, -1);
        buffer.addVertex(e3.x(), e3.y(), e3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, -1);
        buffer.addVertex(e4.x(), e4.y(), e4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, -1);

        // +Z face
        Vector4f f1 = t.apply(new Vector3f(-halfX, -halfY, halfZ));
        Vector4f f2 = t.apply(new Vector3f(-halfX, halfY, halfZ));
        Vector4f f3 = t.apply(new Vector3f(halfX, halfY, halfZ));
        Vector4f f4 = t.apply(new Vector3f(halfX, -halfY, halfZ));
        buffer.addVertex(f1.x(), f1.y(), f1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(f2.x(), f2.y(), f2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(f3.x(), f3.y(), f3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(f4.x(), f4.y(), f4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
    }
    
    /**
     * Adds a quad (4 vertices) to the vertex buffer.
     * Used for rendering beam segments and other custom geometry.
     * @param buffer The vertex buffer to write to
     * @param m The transformation matrix
     * @param v0 First vertex
     * @param v1 Second vertex
     * @param v2 Third vertex
     * @param v3 Fourth vertex
     * @param color The color of the quad (ARGB format)
     */
    public static void addQuad(VertexConsumer buffer, Matrix4f m, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, int color) {
        Vector4f p0 = new Vector4f(v0, 1.0f).mul(m);
        Vector4f p1 = new Vector4f(v1, 1.0f).mul(m);
        Vector4f p2 = new Vector4f(v2, 1.0f).mul(m);
        Vector4f p3 = new Vector4f(v3, 1.0f).mul(m);

        // Single sided; if we want double-sided, we could add reversed order as well
        buffer.addVertex(p0.x(), p0.y(), p0.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(p1.x(), p1.y(), p1.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(p2.x(), p2.y(), p2.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
        buffer.addVertex(p3.x(), p3.y(), p3.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, RendererConstants.OVERLAY_BITS, 0, 0, 1);
    }
}









