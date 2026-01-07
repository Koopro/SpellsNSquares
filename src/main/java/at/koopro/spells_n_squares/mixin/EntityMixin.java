package at.koopro.spells_n_squares.mixin;

import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for base Entity class to add custom handling for all magical entities.
 * Handles custom collision detection for ghosts and teleportation effect hooks.
 */
@Mixin(Entity.class)
public class EntityMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Shadow fields to access Entity internals
    // Note: These might not exist in all Minecraft versions, but we'll try to use them if available
    // @Shadow private EntityDimensions dimensions;
    // @Shadow private float eyeHeight;
    
    // Ghost entity collision detection removed - ghosts feature removed
    
    /**
     * Hook into teleportation to add apparition effects.
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"))
    private void onTeleportTo(double x, double y, double z, CallbackInfo ci) {
        // Check if this is an apparition teleportation
        // Example: if (isApparating(self)) {
        //     Entity self = (Entity) (Object) this;
        //     Vec3 originPos = self.position();
        //     spawnApparitionEffects(self.level(), originPos);
        // }
    }
    
    /**
     * Modify movement for magical entities.
     */
    @ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vec3 modifyMovement(Vec3 original, net.minecraft.world.entity.MoverType type) {
        // Ghost entity movement modification removed - ghosts feature removed
        
        // Modify movement for other magical entities
        // Example: if (hasLevitationSpell(self)) {
        //     return original.add(0, 0.1, 0); // Add upward movement
        // }
        
        return original;
    }
    
    /**
     * Inject into tick() to handle magical entity behaviors.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        // Test: Log once per second for players to verify mixin is working
        if (self instanceof Player player && self.tickCount % 20 == 0) {
            LOGGER.info("[EntityMixin] onTick: Player {} ticked, mixin is active", player.getName().getString());
        }
        
        // Handle magical entity behaviors
        // Example: if (isMagicalEntity(self)) {
        //     updateMagicalEntityBehavior(self);
        // }
        
        // Handle spell effect updates
        // Example: if (hasActiveSpellEffects(self)) {
        //     updateSpellEffects(self);
        // }
    }
    
    /**
     * Intercept refreshDimensions() to apply scaled dimensions.
     * This is called whenever dimensions need to be recalculated.
     * Based on Pehkui's approach - they modify dimensions during refresh.
     * 
     * We inject at TAIL to ensure the original method completes first,
     * then we override the bounding box with our scaled dimensions.
     */
    @Inject(method = "refreshDimensions()V", at = @At("TAIL"))
    private void onRefreshDimensions(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        
        // Apply to players and dummy players
        if (self instanceof Player player) {
            LOGGER.info("[EntityMixin] onRefreshDimensions: Player {} refreshing dimensions, applying scaling", 
                player.getName().getString());
            
            // Get scaled dimensions for current pose
            Pose currentPose = player.getPose();
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(player, currentPose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] onRefreshDimensions: Got scaled dimensions: {}x{}, recalculating bounding box", 
                    scaledDimensions.width(), scaledDimensions.height());
                
                // Recalculate bounding box with scaled dimensions
                // Use the entity's current position to calculate the bounding box
                net.minecraft.world.phys.Vec3 pos = player.position();
                float halfWidth = scaledDimensions.width() / 2.0f;
                float height = scaledDimensions.height();
                
                // Create new bounding box centered at the entity's position
                net.minecraft.world.phys.AABB newBB = new net.minecraft.world.phys.AABB(
                    pos.x - halfWidth, pos.y, pos.z - halfWidth,
                    pos.x + halfWidth, pos.y + height, pos.z + halfWidth
                );
                
                // Set the bounding box - this should override what refreshDimensions() calculated
                player.setBoundingBox(newBB);
                
                LOGGER.info("[EntityMixin] onRefreshDimensions: Set bounding box to {} (from pos {}, dimensions {}x{})", 
                    newBB, pos, scaledDimensions.width(), scaledDimensions.height());
            }
        } else if (self instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            LOGGER.info("[EntityMixin] onRefreshDimensions: DummyPlayerEntity {} refreshing dimensions, applying scaling", 
                dummyPlayer.getName().getString());
            
            // Get scaled dimensions for current pose
            Pose currentPose = dummyPlayer.getPose();
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(dummyPlayer, currentPose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] onRefreshDimensions: Got scaled dimensions for dummy player: {}x{}, recalculating bounding box", 
                    scaledDimensions.width(), scaledDimensions.height());
                
                // Recalculate bounding box with scaled dimensions
                net.minecraft.world.phys.Vec3 pos = dummyPlayer.position();
                float halfWidth = scaledDimensions.width() / 2.0f;
                float height = scaledDimensions.height();
                
                // Create new bounding box centered at the entity's position
                net.minecraft.world.phys.AABB newBB = new net.minecraft.world.phys.AABB(
                    pos.x - halfWidth, pos.y, pos.z - halfWidth,
                    pos.x + halfWidth, pos.y + height, pos.z + halfWidth
                );
                
                // Set the bounding box
                dummyPlayer.setBoundingBox(newBB);
                
                LOGGER.info("[EntityMixin] onRefreshDimensions: Set bounding box for dummy player to {} (from pos {}, dimensions {}x{})", 
                    newBB, pos, scaledDimensions.width(), scaledDimensions.height());
            }
        }
    }
    
    /**
     * Intercept makeBoundingBox to apply scaled dimensions.
     * This is called when the bounding box needs to be recalculated.
     * This is another point where we can apply scaling.
     * 
     * NOTE: This is called during entity construction, so the player may not be fully initialized yet.
     * We need to check if the player has a valid gameProfile before accessing it.
     */
    @Inject(method = "makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;", 
        at = @At("RETURN"), cancellable = true)
    private void onMakeBoundingBox(net.minecraft.world.phys.Vec3 pos, CallbackInfoReturnable<net.minecraft.world.phys.AABB> cir) {
        Entity self = (Entity) (Object) this;
        
        // Apply to players and dummy players
        if (self instanceof Player player) {
            // Check if player is fully initialized (has a gameProfile)
            // During construction, gameProfile may be null, so we skip scaling in that case
            try {
                // Try to access the gameProfile - if it's null, this will throw NPE
                // We catch it and skip scaling during construction
                if (player.getGameProfile() == null) {
                    // Player is not fully initialized yet, skip scaling
                    return;
                }
            } catch (Exception e) {
                // If we can't access gameProfile, player is not initialized, skip scaling
                return;
            }
            
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(player, player.getPose());
            
            if (scaledDimensions != null) {
                String playerName = "Unknown";
                try {
                    playerName = player.getName().getString();
                } catch (Exception e) {
                    // Player name not available yet, use default
                }
                LOGGER.info("[EntityMixin] onMakeBoundingBox: Player {} making bounding box with scaled dimensions {}x{}", 
                    playerName, scaledDimensions.width(), scaledDimensions.height());
                
                // Recalculate bounding box with scaled dimensions
                float halfWidth = scaledDimensions.width() / 2.0f;
                float height = scaledDimensions.height();
                
                net.minecraft.world.phys.AABB newBB = new net.minecraft.world.phys.AABB(
                    pos.x - halfWidth, pos.y, pos.z - halfWidth,
                    pos.x + halfWidth, pos.y + height, pos.z + halfWidth
                );
                
                cir.setReturnValue(newBB);
                LOGGER.info("[EntityMixin] onMakeBoundingBox: Set bounding box to {}", newBB);
            }
        } else if (self instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(dummyPlayer, dummyPlayer.getPose());
            
            if (scaledDimensions != null) {
                String entityName = "Unknown";
                try {
                    entityName = dummyPlayer.getName().getString();
                } catch (Exception e) {
                    // Entity name not available yet, use default
                }
                LOGGER.info("[EntityMixin] onMakeBoundingBox: DummyPlayerEntity {} making bounding box with scaled dimensions {}x{}", 
                    entityName, scaledDimensions.width(), scaledDimensions.height());
                
                // Recalculate bounding box with scaled dimensions
                float halfWidth = scaledDimensions.width() / 2.0f;
                float height = scaledDimensions.height();
                
                net.minecraft.world.phys.AABB newBB = new net.minecraft.world.phys.AABB(
                    pos.x - halfWidth, pos.y, pos.z - halfWidth,
                    pos.x + halfWidth, pos.y + height, pos.z + halfWidth
                );
                
                cir.setReturnValue(newBB);
                LOGGER.info("[EntityMixin] onMakeBoundingBox: Set bounding box for dummy player to {}", newBB);
            }
        }
    }
    
    /**
     * Modify getDimensions return value to apply player model scaling.
     * Using @Inject at RETURN with cancellable=true to intercept and modify the return value.
     * This should work even if @ModifyReturnValue doesn't.
     */
    @Inject(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", 
        at = @At("RETURN"), cancellable = true)
    private void onGetDimensionsReturn(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Entity self = (Entity) (Object) this;
        EntityDimensions original = cir.getReturnValue();
        
        LOGGER.info("[EntityMixin] onGetDimensionsReturn: entity={}, entityName={}, pose={}, originalDimensions={}x{}, isClientSide={}", 
            self.getClass().getSimpleName(), self.getName().getString(), pose, 
            original.width(), original.height(), self.level().isClientSide());
        
        // Apply scaling for players and dummy players
        if (self instanceof Player player) {
            LOGGER.info("[EntityMixin] onGetDimensionsReturn: Entity is Player: {}, UUID={}, calling getScaledDimensions...", 
                player.getName().getString(), player.getUUID());
            
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(player, pose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] onGetDimensionsReturn: Got scaled dimensions: {}x{} (original was {}x{}), setting return value", 
                    scaledDimensions.width(), scaledDimensions.height(), original.width(), original.height());
                cir.setReturnValue(scaledDimensions);
            } else {
                LOGGER.warn("[EntityMixin] onGetDimensionsReturn: Scaled dimensions is null, keeping original dimensions");
            }
        } else if (self instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            LOGGER.info("[EntityMixin] onGetDimensionsReturn: Entity is DummyPlayerEntity: {}, UUID={}, calling getScaledDimensions...", 
                dummyPlayer.getName().getString(), dummyPlayer.getUUID());
            
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(dummyPlayer, pose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] onGetDimensionsReturn: Got scaled dimensions for dummy player: {}x{} (original was {}x{}), setting return value", 
                    scaledDimensions.width(), scaledDimensions.height(), original.width(), original.height());
                cir.setReturnValue(scaledDimensions);
            }
        }
    }
    
    /**
     * Redirect getType().getDimensions() calls to apply scaling for players.
     * This is a backup approach in case getDimensions() uses getType().getDimensions() internally.
     * Based on Pehkui's approach to entity size modification.
     */
    @Redirect(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", 
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;getDimensions()Lnet/minecraft/world/entity/EntityDimensions;"))
    private EntityDimensions redirectGetTypeDimensions(net.minecraft.world.entity.EntityType<?> entityType) {
        Entity self = (Entity) (Object) this;
        EntityDimensions original = entityType.getDimensions();
        
        // Note: We can't get the pose parameter here directly, so we'll use the current pose
        Pose currentPose = self.getPose();
        
        LOGGER.info("[EntityMixin] redirectGetTypeDimensions: entity={}, entityName={}, currentPose={}, originalDimensions={}x{}, isClientSide={}", 
            self.getClass().getSimpleName(), self.getName().getString(), currentPose, 
            original.width(), original.height(), self.level().isClientSide());
        
        // Apply scaling for players and dummy players
        if (self instanceof Player player) {
            LOGGER.info("[EntityMixin] redirectGetTypeDimensions: Entity is Player: {}, UUID={}, calling getScaledDimensions...", 
                player.getName().getString(), player.getUUID());
            
            // Use current pose - this might not be perfect but should work for most cases
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(player, currentPose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] redirectGetTypeDimensions: Got scaled dimensions: {}x{} (original was {}x{})", 
                    scaledDimensions.width(), scaledDimensions.height(), original.width(), original.height());
                return scaledDimensions;
            } else {
                LOGGER.warn("[EntityMixin] redirectGetTypeDimensions: Scaled dimensions is null, returning original dimensions");
            }
        } else if (self instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            LOGGER.info("[EntityMixin] redirectGetTypeDimensions: Entity is DummyPlayerEntity: {}, UUID={}, calling getScaledDimensions...", 
                dummyPlayer.getName().getString(), dummyPlayer.getUUID());
            
            EntityDimensions scaledDimensions = PlayerModelUtils.getScaledDimensions(dummyPlayer, currentPose);
            
            if (scaledDimensions != null) {
                LOGGER.info("[EntityMixin] redirectGetTypeDimensions: Got scaled dimensions for dummy player: {}x{} (original was {}x{})", 
                    scaledDimensions.width(), scaledDimensions.height(), original.width(), original.height());
                return scaledDimensions;
            }
        }
        
        // Return original dimensions for non-players or if scaling failed
        return original;
    }
    
    /**
     * Modify getEyeHeight() to apply player scale.
     * Eye height should scale with the player's overall scale.
     * This affects camera position for first-person view.
     */
    @Inject(method = "getEyeHeight()F", at = @At("RETURN"), cancellable = true)
    private void onGetEyeHeight(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        
        // Only apply to players
        if (self instanceof Player player) {
            float originalEyeHeight = cir.getReturnValue();
            
            // Get player model data to apply scale
            var modelData = PlayerModelUtils.getModelData(player);
            float overallScale = modelData.scale();
            
            if (overallScale != 1.0f) {
                float scaledEyeHeight = originalEyeHeight * overallScale;
                
                String playerName = "Unknown";
                try {
                    playerName = player.getName().getString();
                } catch (Exception e) {
                    // Player name not available yet, use default
                }
                
                LOGGER.info("[EntityMixin] onGetEyeHeight: Player: {}, originalEyeHeight: {}, overallScale: {}, scaledEyeHeight: {}", 
                    playerName, originalEyeHeight, overallScale, scaledEyeHeight);
                
                cir.setReturnValue(scaledEyeHeight);
            }
        }
    }
    
    /**
     * Modify getEyeHeight(Pose) to apply player scale.
     * Eye height should scale with the player's overall scale for different poses.
     */
    @Inject(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("RETURN"), cancellable = true)
    private void onGetEyeHeightPose(Pose pose, CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        
        // Only apply to players
        if (self instanceof Player player) {
            float originalEyeHeight = cir.getReturnValue();
            
            // Get player model data to apply scale
            var modelData = PlayerModelUtils.getModelData(player);
            float overallScale = modelData.scale();
            
            if (overallScale != 1.0f) {
                float scaledEyeHeight = originalEyeHeight * overallScale;
                
                String playerName = "Unknown";
                try {
                    playerName = player.getName().getString();
                } catch (Exception e) {
                    // Player name not available yet, use default
                }
                
                LOGGER.info("[EntityMixin] onGetEyeHeightPose: Player: {}, pose: {}, originalEyeHeight: {}, overallScale: {}, scaledEyeHeight: {}", 
                    playerName, pose, originalEyeHeight, overallScale, scaledEyeHeight);
                
                cir.setReturnValue(scaledEyeHeight);
            }
        }
    }
    
    /**
     * Intercept getEyePosition() to verify it uses the scaled eye height.
     * This method internally calls getEyeHeight(), so if we scale getEyeHeight(),
     * this should automatically use the scaled value. We add logging here to verify.
     */
    @Inject(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private void onGetEyePosition(CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        
        // Only log for players to verify scaling
        if (self instanceof Player player) {
            Vec3 eyePos = cir.getReturnValue();
            Vec3 entityPos = player.position();
            float eyeHeight = player.getEyeHeight();
            
            // Get player model data to check scale
            var modelData = PlayerModelUtils.getModelData(player);
            float overallScale = modelData.scale();
            
            // Calculate expected eye height based on scale
            // Default player eye height is approximately 1.62 blocks for standing pose
            float defaultEyeHeight = 1.62f;
            float expectedEyeHeight = defaultEyeHeight * overallScale;
            float actualHeightDiff = (float)(eyePos.y - entityPos.y);
            
            String playerName = "Unknown";
            try {
                playerName = player.getName().getString();
            } catch (Exception e) {
                // Player name not available yet, use default
            }
            
            // Log every 20 ticks (once per second) to avoid spam
            if (player.tickCount % 20 == 0) {
                LOGGER.info("[EntityMixin] onGetEyePosition: Player: {}, overallScale: {}, eyeHeight: {}, expectedEyeHeight: {}, entityPos: {}, eyePos: {}, actualHeightDiff: {}, scaleRatio: {}", 
                    playerName, overallScale, eyeHeight, expectedEyeHeight, entityPos, eyePos, actualHeightDiff, 
                    overallScale != 1.0f ? (actualHeightDiff / defaultEyeHeight) : 1.0f);
            }
        }
    }
    
    /**
     * Intercept getEyePosition(float) to verify it uses the scaled eye height with partial tick.
     * This method internally calls getEyeHeight(), so if we scale getEyeHeight(),
     * this should automatically use the scaled value. We add logging here to verify.
     */
    @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private void onGetEyePositionPartial(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        
        // Only log for players to verify scaling
        if (self instanceof Player player) {
            Vec3 eyePos = cir.getReturnValue();
            Vec3 entityPos = player.position();
            float eyeHeight = player.getEyeHeight();
            
            // Get player model data to check scale
            var modelData = PlayerModelUtils.getModelData(player);
            float overallScale = modelData.scale();
            
            // Calculate expected eye height based on scale
            // Default player eye height is approximately 1.62 blocks for standing pose
            float defaultEyeHeight = 1.62f;
            float expectedEyeHeight = defaultEyeHeight * overallScale;
            float actualHeightDiff = (float)(eyePos.y - entityPos.y);
            
            String playerName = "Unknown";
            try {
                playerName = player.getName().getString();
            } catch (Exception e) {
                // Player name not available yet, use default
            }
            
            // Log every 20 ticks (once per second) to avoid spam
            if (player.tickCount % 20 == 0) {
                LOGGER.info("[EntityMixin] onGetEyePositionPartial: Player: {}, overallScale: {}, partialTick: {}, eyeHeight: {}, expectedEyeHeight: {}, entityPos: {}, eyePos: {}, actualHeightDiff: {}, scaleRatio: {}", 
                    playerName, overallScale, partialTick, eyeHeight, expectedEyeHeight, entityPos, eyePos, actualHeightDiff,
                    overallScale != 1.0f ? (actualHeightDiff / defaultEyeHeight) : 1.0f);
            }
        }
    }
    
}

