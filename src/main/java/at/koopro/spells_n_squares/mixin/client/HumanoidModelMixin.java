package at.koopro.spells_n_squares.mixin.client;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to modify player model pose when riding a broom.
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart leftLeg;
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart rightLeg;
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart body;
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart leftArm;
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart rightArm;
    
    @Shadow
    public net.minecraft.client.model.geom.ModelPart head;
    
    @Inject(method = "setupAnim", at = @At(value = "RETURN"))
    private void applyBroomRidingPose(HumanoidRenderState renderState, CallbackInfo ci) {
        // Get the entity from the render state - try entity ID lookup first (most reliable)
        AbstractClientPlayer player = null;
        
        // Try entity ID lookup first (same approach as AvatarRendererMixin)
        try {
            var idField = EntityRenderState.class.getDeclaredField("id");
            idField.setAccessible(true);
            int entityId = idField.getInt(renderState);
            
            var minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft != null && minecraft.level != null) {
                var entity = minecraft.level.getEntity(entityId);
                if (entity instanceof AbstractClientPlayer) {
                    player = (AbstractClientPlayer) entity;
                    LOGGER.info("[HumanoidModelMixin] applyBroomRidingPose: Successfully looked up player from entity ID {}", entityId);
                }
            }
        } catch (Exception e) {
            LOGGER.debug("[HumanoidModelMixin] applyBroomRidingPose: Entity ID lookup failed: {}", e.getMessage());
        }
        
        // Fallback: Try reflection for entity field
        if (player == null) {
            // First, try to find a getter method
            try {
                var getEntityMethod = EntityRenderState.class.getMethod("getEntity");
                Entity entity = (Entity) getEntityMethod.invoke(renderState);
                if (entity instanceof AbstractClientPlayer) {
                    player = (AbstractClientPlayer) entity;
                    LOGGER.info("[HumanoidModelMixin] applyBroomRidingPose: Successfully accessed player via getEntity() method");
                }
            } catch (NoSuchMethodException e) {
                // Try field access with multiple possible field names
                String[] possibleFieldNames = {"entity", "f_entity", "livingEntity", "f_livingEntity"};
                for (String fieldName : possibleFieldNames) {
                    try {
                        var entityField = EntityRenderState.class.getDeclaredField(fieldName);
                        entityField.setAccessible(true);
                        Object fieldValue = entityField.get(renderState);
                        if (fieldValue instanceof AbstractClientPlayer) {
                            player = (AbstractClientPlayer) fieldValue;
                            LOGGER.info("[HumanoidModelMixin] applyBroomRidingPose: Successfully accessed player via '{}' field", fieldName);
                            break;
                        }
                    } catch (NoSuchFieldException ignored) {
                        // Try next field name
                    } catch (Exception e2) {
                        LOGGER.debug("[HumanoidModelMixin] applyBroomRidingPose: Could not access entity field '{}': {}", fieldName, e2.getMessage());
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("[HumanoidModelMixin] applyBroomRidingPose: Error accessing entity from render state: {}", e.getMessage());
            }
        }
        
        if (player == null) {
            LOGGER.debug("[HumanoidModelMixin] applyBroomRidingPose: Could not access player, skipping scaling");
            return;
        }
        
        // Apply player model scaling for individual body parts
        applyPlayerModelScaling(player);
    }
    
    /**
     * Applies player model scaling for individual body parts and overall scale.
     * Based on Pehkui's approach - they apply scale at the model level as well as renderer level.
     * Also adjusts pivot positions when scaling to keep parts aligned (based on soniccycle mod approach).
     */
    private void applyPlayerModelScaling(AbstractClientPlayer player) {
        if (player == null || head == null || body == null || 
            leftArm == null || rightArm == null || leftLeg == null || rightLeg == null) {
            return;
        }
        
        // Get model data (client-side, may need to sync from server)
        PlayerModelDataComponent.PlayerModelData modelData = PlayerModelUtils.getModelData(player);
        float overallScale = modelData.scale();
        
        // Only log if scaling is actually applied (to reduce log spam)
        if (overallScale != 1.0f || modelData.headScale() != 1.0f || modelData.bodyScale() != 1.0f) {
            LOGGER.debug("[HumanoidModelMixin] applyPlayerModelScaling: Player: {}, overallScale: {}, headScale: {}, bodyScale: {}", 
                player.getName().getString(), overallScale, modelData.headScale(), modelData.bodyScale());
        }
        
        // Apply individual body part scales INDEPENDENTLY
        // Body parts should scale independently of overall scale
        // Overall scale is applied at the renderer level (PoseStack), not here
        // This allows scaling individual parts even when overall scale is 1.0
        if (head != null) {
            float headScale = modelData.headScale(); // Independent of overallScale
            head.xScale = headScale;
            head.yScale = headScale;
            head.zScale = headScale;
            
            // Adjust pivot position when scaling head to keep it aligned
            adjustPivotPosition(head, "head", headScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
        
        if (body != null) {
            float bodyScale = modelData.bodyScale(); // Independent of overallScale
            body.xScale = bodyScale;
            body.yScale = bodyScale;
            body.zScale = bodyScale;
            
            // Body pivot adjustments are usually not needed, but we can adjust if needed
            adjustPivotPosition(body, "body", bodyScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
        
        if (leftArm != null) {
            float leftArmScale = modelData.leftArmScale(); // Independent of overallScale
            leftArm.xScale = leftArmScale;
            leftArm.yScale = leftArmScale;
            leftArm.zScale = leftArmScale;
            
            // Adjust arm pivot when scaling to keep attachment point correct
            adjustPivotPosition(leftArm, "leftArm", leftArmScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
        
        if (rightArm != null) {
            float rightArmScale = modelData.rightArmScale(); // Independent of overallScale
            rightArm.xScale = rightArmScale;
            rightArm.yScale = rightArmScale;
            rightArm.zScale = rightArmScale;
            
            // Adjust arm pivot when scaling to keep attachment point correct
            adjustPivotPosition(rightArm, "rightArm", rightArmScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
        
        if (leftLeg != null) {
            float leftLegScale = modelData.leftLegScale(); // Independent of overallScale
            leftLeg.xScale = leftLegScale;
            leftLeg.yScale = leftLegScale;
            leftLeg.zScale = leftLegScale;
            
            // Adjust leg pivot when scaling to keep attachment point correct
            adjustPivotPosition(leftLeg, "leftLeg", leftLegScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
        
        if (rightLeg != null) {
            float rightLegScale = modelData.rightLegScale(); // Independent of overallScale
            rightLeg.xScale = rightLegScale;
            rightLeg.yScale = rightLegScale;
            rightLeg.zScale = rightLegScale;
            
            // Adjust leg pivot when scaling to keep attachment point correct
            adjustPivotPosition(rightLeg, "rightLeg", rightLegScale, 0.0f, 0.0f, 0.0f);
            
            // Logging removed - too verbose for per-frame calls
        }
    }
    
    /**
     * Adjusts the pivot position of a ModelPart when scaling.
     * Based on the soniccycle mod approach - they adjust pivotY and pivotZ to reposition parts.
     * 
     * @param part The model part to adjust
     * @param partName The name of the part (for logging)
     * @param scale The scale being applied
     * @param pivotXOffset Additional X offset to apply
     * @param pivotYOffset Additional Y offset to apply
     * @param pivotZOffset Additional Z offset to apply
     */
    private void adjustPivotPosition(net.minecraft.client.model.geom.ModelPart part, String partName, 
                                     float scale, float pivotXOffset, float pivotYOffset, float pivotZOffset) {
        if (part == null || scale == 1.0f) {
            // No adjustment needed if scale is 1.0
            return;
        }
        
        try {
            // Try to access pivot properties using reflection
            // Different Minecraft versions may use different field names
            String[] pivotXNames = {"pivotX", "x", "f_pivotX"};
            String[] pivotYNames = {"pivotY", "y", "f_pivotY"};
            String[] pivotZNames = {"pivotZ", "z", "f_pivotZ"};
            
            java.lang.reflect.Field pivotXField = null;
            java.lang.reflect.Field pivotYField = null;
            java.lang.reflect.Field pivotZField = null;
            
            // Find pivot fields
            for (String fieldName : pivotXNames) {
                try {
                    pivotXField = part.getClass().getDeclaredField(fieldName);
                    pivotXField.setAccessible(true);
                    break;
                } catch (NoSuchFieldException ignored) {
                    // Try next name
                }
            }
            
            for (String fieldName : pivotYNames) {
                try {
                    pivotYField = part.getClass().getDeclaredField(fieldName);
                    pivotYField.setAccessible(true);
                    break;
                } catch (NoSuchFieldException ignored) {
                    // Try next name
                }
            }
            
            for (String fieldName : pivotZNames) {
                try {
                    pivotZField = part.getClass().getDeclaredField(fieldName);
                    pivotZField.setAccessible(true);
                    break;
                } catch (NoSuchFieldException ignored) {
                    // Try next name
                }
            }
            
            // If we found pivot fields, adjust them
            // Note: For now, we're not automatically calculating offsets based on scale
            // This is a placeholder for future enhancement if needed
            // The offsets can be calculated based on the scale difference if needed
            if (pivotXField != null && pivotXOffset != 0.0f) {
                float currentX = pivotXField.getFloat(part);
                pivotXField.setFloat(part, currentX + pivotXOffset);
            }
            
            if (pivotYField != null && pivotYOffset != 0.0f) {
                float currentY = pivotYField.getFloat(part);
                pivotYField.setFloat(part, currentY + pivotYOffset);
            }
            
            if (pivotZField != null && pivotZOffset != 0.0f) {
                float currentZ = pivotZField.getFloat(part);
                pivotZField.setFloat(part, currentZ + pivotZOffset);
            }
            
            // Log if we're adjusting pivots (only when offsets are non-zero)
            if ((pivotXOffset != 0.0f || pivotYOffset != 0.0f || pivotZOffset != 0.0f) && 
                (pivotXField != null || pivotYField != null || pivotZField != null)) {
                LOGGER.debug("[HumanoidModelMixin] adjustPivotPosition: Adjusted {} pivot - X: {}, Y: {}, Z: {}", 
                    partName, pivotXOffset, pivotYOffset, pivotZOffset);
            }
        } catch (Exception e) {
            // Pivot adjustment is optional - if it fails, just continue without it
            LOGGER.debug("[HumanoidModelMixin] adjustPivotPosition: Could not adjust pivot for {}: {}", partName, e.getMessage());
        }
    }
}

