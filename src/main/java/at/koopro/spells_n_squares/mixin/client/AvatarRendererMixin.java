package at.koopro.spells_n_squares.mixin.client;

import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin for AvatarRenderer (player-specific renderer) to apply player model scaling.
 * Based on CustomPlayerModels' approach - they mix into AvatarRenderer for player-specific rendering.
 * 
 * This is more targeted than LivingEntityRenderer since AvatarRenderer is specifically for players.
 */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Inject into submit() to apply overall player model scale transformation.
     * Based on CustomPlayerModels' approach - they inject at HEAD of submit().
     * 
     * We inject at HEAD to apply the scale transformation before the renderer processes the model.
     */
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD"))
    private void onSubmit(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, 
                         CameraRenderState cameraState, CallbackInfo ci) {
        LOGGER.info("[AvatarRendererMixin] onSubmit: Called, attempting to access player via entity ID...");
        
        // Use entity ID to look up entity from client level (most reliable approach)
        // The 'id' field exists in AvatarRenderState (as shown in logs)
        AbstractClientPlayer player = null;
        
        // Try to find the 'id' field by iterating through all declared fields
        // This handles cases where the field might be in the class or parent classes
        java.lang.reflect.Field idField = null;
        Class<?> currentClass = AvatarRenderState.class;
        
        while (currentClass != null && idField == null) {
            try {
                // Try direct field access first
                idField = currentClass.getDeclaredField("id");
                idField.setAccessible(true);
                LOGGER.info("[AvatarRendererMixin] onSubmit: Found 'id' field in {}", currentClass.getSimpleName());
                break;
            } catch (NoSuchFieldException e) {
                // Try parent class
                currentClass = currentClass.getSuperclass();
                if (currentClass != null && !currentClass.equals(Object.class)) {
                    LOGGER.debug("[AvatarRendererMixin] onSubmit: 'id' field not found in {}, trying parent {}", 
                        currentClass.getSimpleName(), currentClass.getSuperclass() != null ? currentClass.getSuperclass().getSimpleName() : "null");
                }
            }
        }
        
        // If still not found, try iterating through all fields
        if (idField == null) {
            LOGGER.info("[AvatarRendererMixin] onSubmit: 'id' field not found via direct lookup, searching all fields...");
            currentClass = AvatarRenderState.class;
            while (currentClass != null && currentClass != Object.class) {
                for (java.lang.reflect.Field field : currentClass.getDeclaredFields()) {
                    if (field.getType() == int.class && field.getName().equals("id")) {
                        idField = field;
                        idField.setAccessible(true);
                        LOGGER.info("[AvatarRendererMixin] onSubmit: Found 'id' field via iteration in {}", currentClass.getSimpleName());
                        break;
                    }
                }
                if (idField != null) break;
                currentClass = currentClass.getSuperclass();
            }
        }
        
        if (idField != null) {
            try {
                int entityId = idField.getInt(renderState);
                LOGGER.info("[AvatarRendererMixin] onSubmit: Got entity ID from render state: {}", entityId);
                
                // Get the entity from the client level
                var minecraft = net.minecraft.client.Minecraft.getInstance();
                LOGGER.info("[AvatarRendererMixin] onSubmit: Minecraft instance: {}, level: {}", 
                    minecraft != null, minecraft != null && minecraft.level != null);
                
                if (minecraft != null && minecraft.level != null) {
                    var entity = minecraft.level.getEntity(entityId);
                    LOGGER.info("[AvatarRendererMixin] onSubmit: Entity lookup result: entityId={}, entity={}, entityType={}", 
                        entityId, entity != null, entity != null ? entity.getClass().getSimpleName() : "null");
                    
                    if (entity instanceof AbstractClientPlayer) {
                        player = (AbstractClientPlayer) entity;
                        LOGGER.info("[AvatarRendererMixin] onSubmit: Successfully looked up player from entity ID {}: {}", 
                            entityId, player.getName().getString());
                    } else {
                        LOGGER.warn("[AvatarRendererMixin] onSubmit: Entity with ID {} is not a player: {} (type: {})", 
                            entityId, entity != null ? entity.getClass().getSimpleName() : "null",
                            entity != null ? entity.getType().toString() : "N/A");
                    }
                } else {
                    LOGGER.warn("[AvatarRendererMixin] onSubmit: Client level is null, cannot look up entity. Minecraft: {}, Level: {}", 
                        minecraft != null, minecraft != null && minecraft.level != null);
                }
            } catch (Exception e) {
                LOGGER.error("[AvatarRendererMixin] onSubmit: Error accessing entity ID or looking up entity: {}", e.getMessage(), e);
            }
        } else {
            LOGGER.warn("[AvatarRendererMixin] onSubmit: Could not find 'id' field in AvatarRenderState or parent classes");
        }
        
        // Fallback: Try reflection for entity field (in case ID lookup fails)
        if (player == null) {
            LOGGER.info("[AvatarRendererMixin] onSubmit: ID lookup failed, trying reflection fallback...");
            String[] possibleFieldNames = {"entity", "f_entity", "player", "f_player", "livingEntity", "f_livingEntity"};
            
            // Try fields in AvatarRenderState itself
            for (String fieldName : possibleFieldNames) {
                try {
                    var playerField = AvatarRenderState.class.getDeclaredField(fieldName);
                    playerField.setAccessible(true);
                    Object fieldValue = playerField.get(renderState);
                    if (fieldValue instanceof AbstractClientPlayer) {
                        player = (AbstractClientPlayer) fieldValue;
                        LOGGER.info("[AvatarRendererMixin] onSubmit: Successfully accessed player via '{}' field in AvatarRenderState (reflection): {}", 
                            fieldName, player.getName().getString());
                        break;
                    }
                } catch (NoSuchFieldException ignored) {
                    // Try next field name
                } catch (Exception e3) {
                    LOGGER.debug("[AvatarRendererMixin] onSubmit: Error accessing field '{}' in AvatarRenderState: {}", fieldName, e3.getMessage());
                }
            }
            
            // If not found, try parent classes
            if (player == null) {
                Class<?>[] parentClasses = {
                    net.minecraft.client.renderer.entity.state.LivingEntityRenderState.class,
                    net.minecraft.client.renderer.entity.state.EntityRenderState.class
                };
                
                for (Class<?> parentClass : parentClasses) {
                    for (String fieldName : possibleFieldNames) {
                        try {
                            var playerField = parentClass.getDeclaredField(fieldName);
                            playerField.setAccessible(true);
                            Object fieldValue = playerField.get(renderState);
                            if (fieldValue instanceof AbstractClientPlayer) {
                                player = (AbstractClientPlayer) fieldValue;
                                LOGGER.info("[AvatarRendererMixin] onSubmit: Successfully accessed player via '{}' field in {} (reflection): {}", 
                                    fieldName, parentClass.getSimpleName(), player.getName().getString());
                                break;
                            }
                        } catch (NoSuchFieldException ignored) {
                            // Try next field name
                        } catch (Exception e3) {
                            LOGGER.debug("[AvatarRendererMixin] onSubmit: Error accessing field '{}' in {}: {}", fieldName, parentClass.getSimpleName(), e3.getMessage());
                        }
                    }
                    if (player != null) break;
                }
            }
        }
        
        if (player == null) {
            LOGGER.warn("[AvatarRendererMixin] onSubmit: Could not access player (tried entity ID lookup and reflection), skipping scaling");
            return;
        }
        
        // Get player model data
        var modelData = PlayerModelUtils.getModelData(player);
        float overallScale = modelData.scale();
        
        String playerName;
        try {
            playerName = player.getName().getString();
        } catch (Exception e) {
            playerName = "Unknown";
        }
        
        LOGGER.info("[AvatarRendererMixin] onSubmit: Player: {}, overallScale: {}, headScale: {}, bodyScale: {}", 
            playerName, overallScale, modelData.headScale(), modelData.bodyScale());
        
        // ALWAYS apply overall scale transformation (even if 1.0, to ensure consistency)
        // Individual body parts are scaled independently by HumanoidModelMixin
        // Overall scale affects the entire model via PoseStack transformation
        LOGGER.info("[AvatarRendererMixin] onSubmit: Applying overall scale {} to player {} (PoseStack)", 
            overallScale, playerName);
        
        // Apply scale transformation to the entire model
        // Push pose, apply scale, then the model rendering will use this scaled PoseStack
        poseStack.pushPose();
        poseStack.scale(overallScale, overallScale, overallScale);
        LOGGER.info("[AvatarRendererMixin] onSubmit: Scale transformation applied to PoseStack (pushed pose, scale={})", overallScale);
        
        // We'll pop it at RETURN
    }
    
    /**
     * Inject at RETURN of submit() to pop the pose stack if we pushed one.
     * This ensures proper cleanup of the scale transformation.
     */
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("RETURN"))
    private void onSubmitReturn(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, 
                               CameraRenderState cameraState, CallbackInfo ci) {
        // Try to get the player to check if we applied a scale transformation
        AbstractClientPlayer player = null;
        
        // Use entity ID lookup first (most reliable)
        // Find the 'id' field by iterating through classes
        java.lang.reflect.Field idField = null;
        Class<?> currentClass = AvatarRenderState.class;
        
        while (currentClass != null && idField == null) {
            try {
                idField = currentClass.getDeclaredField("id");
                idField.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        
        // If still not found, try iterating through all fields
        if (idField == null) {
            currentClass = AvatarRenderState.class;
            while (currentClass != null && currentClass != Object.class) {
                for (java.lang.reflect.Field field : currentClass.getDeclaredFields()) {
                    if (field.getType() == int.class && field.getName().equals("id")) {
                        idField = field;
                        idField.setAccessible(true);
                        break;
                    }
                }
                if (idField != null) break;
                currentClass = currentClass.getSuperclass();
            }
        }
        
        if (idField != null) {
            try {
                int entityId = idField.getInt(renderState);
                var minecraft = net.minecraft.client.Minecraft.getInstance();
                if (minecraft != null && minecraft.level != null) {
                    var entity = minecraft.level.getEntity(entityId);
                    if (entity instanceof AbstractClientPlayer) {
                        player = (AbstractClientPlayer) entity;
                    }
                }
            } catch (Exception ignored) {
                // Failed to get entity via ID
            }
        }
        
        // Fallback: Try reflection
        if (player == null) {
            String[] possibleFieldNames = {"entity", "f_entity", "player", "f_player"};
            for (String fieldName : possibleFieldNames) {
                try {
                    var playerField = AvatarRenderState.class.getDeclaredField(fieldName);
                    playerField.setAccessible(true);
                    Object fieldValue = playerField.get(renderState);
                    if (fieldValue instanceof AbstractClientPlayer) {
                        player = (AbstractClientPlayer) fieldValue;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next field name
                }
            }
            
            // If not found, try parent classes
            if (player == null) {
                Class<?>[] parentClasses = {
                    net.minecraft.client.renderer.entity.state.LivingEntityRenderState.class,
                    net.minecraft.client.renderer.entity.state.EntityRenderState.class
                };
                
                for (Class<?> parentClass : parentClasses) {
                    for (String fieldName : possibleFieldNames) {
                        try {
                            var playerField = parentClass.getDeclaredField(fieldName);
                            playerField.setAccessible(true);
                            Object fieldValue = playerField.get(renderState);
                            if (fieldValue instanceof AbstractClientPlayer) {
                                player = (AbstractClientPlayer) fieldValue;
                                break;
                            }
                        } catch (Exception ignored) {
                            // Try next field name
                        }
                    }
                    if (player != null) break;
                }
            }
        }
        
        if (player == null) {
            return;
        }
        
                var modelData = PlayerModelUtils.getModelData(player);
                float overallScale = modelData.scale();

                // Always pop the pose we pushed in onSubmit (we always push, even if scale is 1.0)
                LOGGER.info("[AvatarRendererMixin] onSubmitReturn: Popping PoseStack for player scale {}", overallScale);
                poseStack.popPose();
    }
}

