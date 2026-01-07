package at.koopro.spells_n_squares.mixin.client;

import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin for LivingEntityRenderer to add custom rendering for magical entities.
 * Handles ghost transparency, spell effect overlays, and magical creature visual enhancements.
 * Also applies player model scaling transformations.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Inject into submit() to apply overall player model scale transformation.
     * Based on Pehkui's approach - they apply scale transformations at the renderer level.
     * 
     * We inject at HEAD to apply the scale transformation before the renderer processes the model.
     */
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD"))
    private void onSubmit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, 
                         CameraRenderState cameraState, CallbackInfo ci) {
        // Only apply to players
        if (!(renderState instanceof LivingEntityRenderState livingState)) {
            return;
        }
        
        // Try to get the entity from the render state
        LivingEntity entity = null;
        
        // First, try to find a getter method
        try {
            var getEntityMethod = LivingEntityRenderState.class.getMethod("getEntity");
            entity = (LivingEntity) getEntityMethod.invoke(livingState);
        } catch (NoSuchMethodException e) {
            // Try field access with multiple possible field names
            // First try in LivingEntityRenderState, then in parent EntityRenderState
            String[] possibleFieldNames = {"entity", "f_entity", "livingEntity", "f_livingEntity", "f_234567_", "f_234568_"};
            boolean found = false;
            
            // Try fields in LivingEntityRenderState first
            for (String fieldName : possibleFieldNames) {
                try {
                    var entityField = LivingEntityRenderState.class.getDeclaredField(fieldName);
                    entityField.setAccessible(true);
                    Object fieldValue = entityField.get(livingState);
                    if (fieldValue instanceof LivingEntity) {
                        entity = (LivingEntity) fieldValue;
                        found = true;
                        break;
                    }
                } catch (NoSuchFieldException ignored) {
                    // Try next field name
                } catch (Exception e2) {
                    LOGGER.debug("[LivingEntityRendererMixin] onSubmit: Error accessing field '{}' in LivingEntityRenderState: {}", fieldName, e2.getMessage());
                }
            }
            
            // If not found, try in parent EntityRenderState
            if (!found) {
                for (String fieldName : possibleFieldNames) {
                    try {
                        var entityField = EntityRenderState.class.getDeclaredField(fieldName);
                        entityField.setAccessible(true);
                        Object fieldValue = entityField.get(livingState);
                        if (fieldValue instanceof LivingEntity) {
                            entity = (LivingEntity) fieldValue;
                            found = true;
                            break;
                        }
                    } catch (NoSuchFieldException ignored) {
                        // Try next field name
                    } catch (Exception e2) {
                        LOGGER.debug("[LivingEntityRendererMixin] onSubmit: Error accessing field '{}' in EntityRenderState: {}", fieldName, e2.getMessage());
                    }
                }
            }
            
            if (!found) {
                LOGGER.warn("[LivingEntityRendererMixin] onSubmit: Could not access entity field (tried {} field names in both classes)", possibleFieldNames.length);
                // Try to list all fields to find the correct one
                try {
                    var fields = LivingEntityRenderState.class.getDeclaredFields();
                    LOGGER.info("[LivingEntityRendererMixin] onSubmit: Available fields in LivingEntityRenderState:");
                    for (var field : fields) {
                        LOGGER.info("[LivingEntityRendererMixin] onSubmit:   - {}: {} (type: {})", 
                            field.getName(), field.getType().getSimpleName(), field.getType().getName());
                    }
                    var parentFields = EntityRenderState.class.getDeclaredFields();
                    LOGGER.info("[LivingEntityRendererMixin] onSubmit: Available fields in EntityRenderState (parent):");
                    for (var field : parentFields) {
                        LOGGER.info("[LivingEntityRendererMixin] onSubmit:   - {}: {} (type: {})", 
                            field.getName(), field.getType().getSimpleName(), field.getType().getName());
                    }
                } catch (Exception e3) {
                    LOGGER.warn("[LivingEntityRendererMixin] onSubmit: Could not list fields: {}", e3.getMessage());
                }
                return;
            }
        } catch (Exception e) {
            LOGGER.error("[LivingEntityRendererMixin] onSubmit: Error accessing entity via getter: {}", e.getMessage(), e);
            return;
        }
        
        if (entity == null || !(entity instanceof Player player)) {
            return;
        }
        
        // Get player model data
        var modelData = PlayerModelUtils.getModelData(player);
        float overallScale = modelData.scale();
        
        // Only log if scale is non-default (to reduce log spam)
        if (overallScale != 1.0f) {
            LOGGER.debug("[LivingEntityRendererMixin] onSubmit: Applying overall scale {} to player {}", 
                overallScale, player.getName().getString());
        }
        
        // ALWAYS apply overall scale transformation (even if 1.0, to ensure consistency)
        // Individual body parts are scaled independently by HumanoidModelMixin
        // Overall scale affects the entire model via PoseStack transformation
        poseStack.pushPose();
        poseStack.scale(overallScale, overallScale, overallScale);
        
        // We'll pop it at RETURN
    }
    
    /**
     * Inject at RETURN of submit() to pop the pose stack if we pushed one.
     * This ensures proper cleanup of the scale transformation.
     */
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("RETURN"))
    private void onSubmitReturn(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, 
                               CameraRenderState cameraState, CallbackInfo ci) {
        // Check if we applied a scale transformation
        if (!(renderState instanceof LivingEntityRenderState livingState)) {
            return;
        }
        
        LivingEntity entity = null;
        
        // Try getter method first
        try {
            var getEntityMethod = LivingEntityRenderState.class.getMethod("getEntity");
            entity = (LivingEntity) getEntityMethod.invoke(livingState);
        } catch (NoSuchMethodException e) {
            // Try field access with multiple possible field names
            String[] possibleFieldNames = {"entity", "f_entity", "livingEntity", "f_livingEntity"};
            for (String fieldName : possibleFieldNames) {
                try {
                    var entityField = LivingEntityRenderState.class.getDeclaredField(fieldName);
                    entityField.setAccessible(true);
                    Object fieldValue = entityField.get(livingState);
                    if (fieldValue instanceof LivingEntity) {
                        entity = (LivingEntity) fieldValue;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next field name
                }
            }
        } catch (Exception e) {
            return;
        }
        
        if (entity == null || !(entity instanceof Player player)) {
            return;
        }
        
        var modelData = PlayerModelUtils.getModelData(player);
        float overallScale = modelData.scale();
        
        // Always pop the pose we pushed in onSubmit (we always push, even if scale is 1.0)
        // Only log if scale is non-default
        if (overallScale != 1.0f) {
            LOGGER.debug("[LivingEntityRendererMixin] onSubmitReturn: Popping PoseStack for player scale {}", overallScale);
        }
        poseStack.popPose();
    }
}

