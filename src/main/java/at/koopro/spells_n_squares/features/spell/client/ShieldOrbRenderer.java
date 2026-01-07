package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererConstants;
import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.core.client.ShaderRenderHelper;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Shield Orb entity.
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
    public void extractRenderState(ShieldOrbEntity entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }
    
    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);
        
        // Frustum culling and distance-based LOD: Skip rendering if too far from camera
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft != null && minecraft.player != null && minecraft.level != null) {
            try {
                // Get entity position via entity ID lookup
                var idField = EntityRenderState.class.getDeclaredField("id");
                idField.setAccessible(true);
                int entityId = idField.getInt(renderState);
                
                var entity = minecraft.level.getEntity(entityId);
                if (entity != null) {
                    var playerPos = minecraft.player.position();
                    var entityPos = entity.position();
                    double distanceSq = playerPos.distanceToSqr(entityPos);
                    
                    // Distance-based culling: Skip rendering if beyond 64 blocks (4096 squared distance)
                    if (distanceSq > 4096.0) {
                        return;
                    }
                    
                    // Basic frustum culling: Check if entity is behind camera
                    // Note: CameraRenderState doesn't have position()/forward() methods
                    // Using player position for simple distance-based culling instead
                    // More advanced frustum culling would require accessing camera directly
                }
            } catch (Exception e) {
                // If we can't get position, render anyway
            }
        }
        
        float half = Math.max(renderState.boundingBoxWidth, renderState.boundingBoxHeight) * 0.5f;
        
        // Update shader time uniform for animated effects (if shader is used)
        ShaderRenderHelper.updateTimeUniform(ShaderEffectHandler.CUT_EFFECT_SHADER);
        
        // Get render type - will use shader if available, otherwise fallback to texture
        net.minecraft.client.renderer.rendertype.RenderType renderType = 
            ShaderRenderHelper.getTranslucentEmissiveOrTexture(
                ShaderEffectHandler.CUT_EFFECT_SHADER,
                RendererConstants.GLOW_TEXTURE
            );
        
        collector.submitCustomGeometry(
            poseStack,
            renderType,
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                
                // Shield orb (blue/cyan translucent)
                RendererUtils.renderCube(buffer, ps, half, 0x40FFFFFF);
                
                ps.popPose();
            }
        );
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        return RendererConstants.GLOW_TEXTURE;
    }
}













