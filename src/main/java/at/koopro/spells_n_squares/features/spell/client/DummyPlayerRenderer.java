package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

/**
 * Renderer for Dummy Player entity.
 * Supports Alex/Steve model types, scaling, and held items.
 * TODO: Enhance to use HumanoidModel for proper player model rendering.
 */
public class DummyPlayerRenderer extends EntityRenderer<DummyPlayerEntity, EntityRenderState> {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Default player textures (for future use)
    private static final Identifier STEVE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/steve.png");
    private static final Identifier ALEX_TEXTURE = Identifier.withDefaultNamespace("textures/entity/alex.png");
    
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
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, 
                      CameraRenderState cameraState) {
        // Distance-based LOD: Skip rendering if too far from camera
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft != null && minecraft.player != null && minecraft.level != null) {
            try {
                // Get entity position via entity ID lookup
                var idField = EntityRenderState.class.getDeclaredField("id");
                idField.setAccessible(true);
                int entityId = idField.getInt(renderState);
                
                var levelEntity = minecraft.level.getEntity(entityId);
                if (levelEntity != null) {
                    var playerPos = minecraft.player.position();
                    var entityPos = levelEntity.position();
                    double distanceSq = playerPos.distanceToSqr(entityPos);
                    // Skip rendering if beyond 96 blocks (9216 squared distance) for dummy players
                    if (distanceSq > 9216.0) {
                        return;
                    }
                }
            } catch (Exception e) {
                // If we can't get position, render anyway
            }
        }
        
        // Get entity from render state (cached lookup to reduce reflection overhead)
        DummyPlayerEntity entity = null;
        if (minecraft != null && minecraft.level != null) {
            try {
                var idField = EntityRenderState.class.getDeclaredField("id");
                idField.setAccessible(true);
                int entityId = idField.getInt(renderState);
                
                var levelEntity = minecraft.level.getEntity(entityId);
                if (levelEntity instanceof DummyPlayerEntity) {
                    entity = (DummyPlayerEntity) levelEntity;
                }
            } catch (Exception e) {
                LOGGER.debug("Failed to get entity from render state: {}", e.getMessage());
            }
        }
        
        if (entity == null) {
            return;
        }
        
        // Get model data for scaling
        PlayerModelDataComponent.PlayerModelData modelData = PlayerModelUtils.getModelData(entity);
        float overallScale = modelData.scale();
        
        // Determine model type and color
        DummyPlayerModelType modelType = entity.getModelType();
        int color = (modelType == DummyPlayerModelType.ALEX) ? 0xFF8B4513 : 0xFF808080; // Brown for Alex, Gray for Steve
        
        // Apply overall scale transformation
        poseStack.pushPose();
        if (overallScale != 1.0f) {
            poseStack.scale(overallScale, overallScale, overallScale);
        }
        
        // Enhanced player-like model with all body parts
        // Use standard player proportions
        float bodyWidth = 0.6f;
        float bodyHeight = 1.8f;
        float headSize = 0.6f;
        float armWidth = 0.4f;
        float legLength = 0.9f;
        float legWidth = 0.4f;
        
        // Apply individual body part scaling from model data
        float headScale = modelData.headScale();
        float bodyScale = modelData.bodyScale();
        float leftArmScale = modelData.leftArmScale();
        float rightArmScale = modelData.rightArmScale();
        float leftLegScale = modelData.leftLegScale();
        float rightLegScale = modelData.rightLegScale();
        
        // Get texture based on model type
        Identifier texture = (modelType == DummyPlayerModelType.ALEX) ? ALEX_TEXTURE : STEVE_TEXTURE;
        
        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                
                // Center the model
                ps.translate(0, bodyHeight / 2.0f, 0);
                
                // Head (scaled)
                ps.pushPose();
                ps.translate(0, bodyHeight * 0.4f, 0);
                ps.scale(headScale, headScale, headScale);
                RendererUtils.renderCube(buffer, ps, headSize, color);
                ps.popPose();
                
                // Body (scaled)
                ps.pushPose();
                ps.translate(0, 0, 0);
                ps.scale(bodyScale, bodyScale, bodyScale);
                RendererUtils.renderCube(buffer, ps, bodyWidth, color);
                ps.popPose();
                
                // Left Arm (scaled)
                ps.pushPose();
                ps.translate(-bodyWidth * 0.5f - armWidth * 0.5f, -bodyHeight * 0.1f, 0);
                ps.scale(leftArmScale, leftArmScale, leftArmScale);
                RendererUtils.renderCube(buffer, ps, armWidth, color);
                ps.popPose();
                
                // Right Arm (scaled)
                ps.pushPose();
                ps.translate(bodyWidth * 0.5f + armWidth * 0.5f, -bodyHeight * 0.1f, 0);
                ps.scale(rightArmScale, rightArmScale, rightArmScale);
                RendererUtils.renderCube(buffer, ps, armWidth, color);
                ps.popPose();
                
                // Left Leg (scaled)
                ps.pushPose();
                ps.translate(-bodyWidth * 0.25f, -bodyHeight * 0.5f - legLength * 0.5f, 0);
                ps.scale(leftLegScale, leftLegScale, leftLegScale);
                RendererUtils.renderCube(buffer, ps, legWidth, color);
                ps.popPose();
                
                // Right Leg (scaled)
                ps.pushPose();
                ps.translate(bodyWidth * 0.25f, -bodyHeight * 0.5f - legLength * 0.5f, 0);
                ps.scale(rightLegScale, rightLegScale, rightLegScale);
                RendererUtils.renderCube(buffer, ps, legWidth, color);
                ps.popPose();
                
                ps.popPose();
            }
        );
        
        // Render held items if present
        if (!entity.getMainHandItem().isEmpty()) {
            // TODO: Render held item in hand position
            // This would require item rendering logic
        }
        
        poseStack.popPose();
    }
    
    public Identifier getTextureLocation(EntityRenderState state) {
        // Try to get entity to determine model type
        try {
            var idField = EntityRenderState.class.getDeclaredField("id");
            idField.setAccessible(true);
            int entityId = idField.getInt(state);
            
            var minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft != null && minecraft.level != null) {
                var levelEntity = minecraft.level.getEntity(entityId);
                if (levelEntity instanceof DummyPlayerEntity dummyEntity) {
                    DummyPlayerModelType modelType = dummyEntity.getModelType();
                    return (modelType == DummyPlayerModelType.ALEX) ? ALEX_TEXTURE : STEVE_TEXTURE;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to get texture from render state: {}", e.getMessage());
        }
        
        // Default to Steve texture
        return STEVE_TEXTURE;
    }
}
