package at.koopro.spells_n_squares.item.client;

import java.util.function.BiConsumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.internal.PerBoneRender;
import software.bernie.geckolib.renderer.internal.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModTags;

/**
 * Generic emissive glow layer that attaches to a model bone.
 */
public class LumosGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState> {
    private static final Identifier GLOW_TEXTURE = Identifier.fromNamespaceAndPath(
        SpellsNSquares.MODID,
        "textures/misc/lumos_white.png"
    );

    private static final DataTicket<ItemStack> LUMOS_STACK =
        DataTicket.create("spells_n_squares_lumos_stack", ItemStack.class);

    public LumosGeoLayer(GeoRenderer<T, GeoItemRenderer.RenderData, GeoRenderState> renderer) {
        super(renderer);
    }

    @Override
    public void addRenderData(T animatable, GeoItemRenderer.RenderData relatedObject, GeoRenderState renderState, float partialTick) {
        renderState.addGeckolibData(LUMOS_STACK, relatedObject.itemStack());
    }

    @Override
    public void addPerBoneRender(RenderPassInfo<GeoRenderState> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<GeoRenderState>> consumer) {
        if (!renderPassInfo.willRender()) {
            return;
        }

        ItemStack stack = renderPassInfo.renderState().getOrDefaultGeckolibData(LUMOS_STACK, ItemStack.EMPTY);
        if (stack.isEmpty() || !stack.is(ModTags.WANDS) || !stack.getOrDefault(ModDataComponents.LUMOS_ACTIVE.get(), false)) {
            return;
        }

        renderPassInfo.model()
            .getBone("tip")
            .or(() -> renderPassInfo.model().getBone("light_source"))
            .ifPresent(bone -> consumer.accept(bone, (info, geoBone, renderTasks) -> submitGlowBillboard(info, geoBone, renderTasks)));
    }

    private void submitGlowBillboard(RenderPassInfo<GeoRenderState> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), RenderTypes.entityTranslucentEmissive(GLOW_TEXTURE), (pose, buffer) -> {
            PoseStack poseStack = new PoseStack();

            poseStack.last().set(pose);
            poseStack.pushPose();
            RenderUtil.prepMatrixForBone(poseStack, bone);
            bone.updateBonePositionListeners(poseStack, renderPassInfo);

            // Hover slightly above the tip and apply a gentle spin
            float time = (float) (Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0.0f);
            poseStack.translate(0.0f, 0.96f, 0.0f);
            poseStack.translate(0.0f, 0.03f, 0.0f);
            poseStack.mulPose(Axis.YP.rotation(time * 0.08f));
            poseStack.mulPose(Axis.XP.rotation(Mth.sin(time * 0.05f) * 0.05f));

            // Compact glow core + shell (no billboard panes)
            renderCube(buffer, poseStack, 0.06f, 0xFFFFFFFF); // bright core
            renderCube(buffer, poseStack, 0.10f, 0x60FFE080); // softer shell

            poseStack.popPose();
        });
    }

    private static void renderCube(com.mojang.blaze3d.vertex.VertexConsumer buffer, PoseStack poseStack, float half, int color) {
        Matrix4f m = new Matrix4f(poseStack.last().pose());
        java.util.function.Function<Vector3f, Vector4f> t = v -> new Vector4f(v, 1.0f).mul(m);

        // -X
        Vector4f a1 = t.apply(new Vector3f(-half, -half, -half));
        Vector4f a2 = t.apply(new Vector3f(-half, half, -half));
        Vector4f a3 = t.apply(new Vector3f(-half, half, half));
        Vector4f a4 = t.apply(new Vector3f(-half, -half, half));
        buffer.addVertex(a1.x(), a1.y(), a1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, -1, 0, 0);
        buffer.addVertex(a2.x(), a2.y(), a2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, -1, 0, 0);
        buffer.addVertex(a3.x(), a3.y(), a3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, -1, 0, 0);
        buffer.addVertex(a4.x(), a4.y(), a4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, -1, 0, 0);

        // +X
        Vector4f b1 = t.apply(new Vector3f(half, -half, -half));
        Vector4f b2 = t.apply(new Vector3f(half, half, -half));
        Vector4f b3 = t.apply(new Vector3f(half, half, half));
        Vector4f b4 = t.apply(new Vector3f(half, -half, half));
        buffer.addVertex(b1.x(), b1.y(), b1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 1, 0, 0);
        buffer.addVertex(b2.x(), b2.y(), b2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 1, 0, 0);
        buffer.addVertex(b3.x(), b3.y(), b3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 1, 0, 0);
        buffer.addVertex(b4.x(), b4.y(), b4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 1, 0, 0);

        // -Y
        Vector4f c1 = t.apply(new Vector3f(-half, -half, -half));
        Vector4f c2 = t.apply(new Vector3f(half, -half, -half));
        Vector4f c3 = t.apply(new Vector3f(half, -half, half));
        Vector4f c4 = t.apply(new Vector3f(-half, -half, half));
        buffer.addVertex(c1.x(), c1.y(), c1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, -1, 0);
        buffer.addVertex(c2.x(), c2.y(), c2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, -1, 0);
        buffer.addVertex(c3.x(), c3.y(), c3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, -1, 0);
        buffer.addVertex(c4.x(), c4.y(), c4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, -1, 0);

        // +Y
        Vector4f d1 = t.apply(new Vector3f(-half, half, -half));
        Vector4f d2 = t.apply(new Vector3f(half, half, -half));
        Vector4f d3 = t.apply(new Vector3f(half, half, half));
        Vector4f d4 = t.apply(new Vector3f(-half, half, half));
        buffer.addVertex(d1.x(), d1.y(), d1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 1, 0);
        buffer.addVertex(d2.x(), d2.y(), d2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 1, 0);
        buffer.addVertex(d3.x(), d3.y(), d3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 1, 0);
        buffer.addVertex(d4.x(), d4.y(), d4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 1, 0);

        // -Z
        Vector4f e1 = t.apply(new Vector3f(-half, -half, -half));
        Vector4f e2 = t.apply(new Vector3f(-half, half, -half));
        Vector4f e3 = t.apply(new Vector3f(half, half, -half));
        Vector4f e4 = t.apply(new Vector3f(half, -half, -half));
        buffer.addVertex(e1.x(), e1.y(), e1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, -1);
        buffer.addVertex(e2.x(), e2.y(), e2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, -1);
        buffer.addVertex(e3.x(), e3.y(), e3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, -1);
        buffer.addVertex(e4.x(), e4.y(), e4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, -1);

        // +Z
        Vector4f f1 = t.apply(new Vector3f(-half, -half, half));
        Vector4f f2 = t.apply(new Vector3f(-half, half, half));
        Vector4f f3 = t.apply(new Vector3f(half, half, half));
        Vector4f f4 = t.apply(new Vector3f(half, -half, half));
        buffer.addVertex(f1.x(), f1.y(), f1.z(), color, 0, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, 1);
        buffer.addVertex(f2.x(), f2.y(), f2.z(), color, 0, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, 1);
        buffer.addVertex(f3.x(), f3.y(), f3.z(), color, 1, 1, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, 1);
        buffer.addVertex(f4.x(), f4.y(), f4.z(), color, 1, 0, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, 0, 0, 1);
    }
}
