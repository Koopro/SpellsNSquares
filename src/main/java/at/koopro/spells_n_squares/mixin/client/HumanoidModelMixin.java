package at.koopro.spells_n_squares.mixin.client;

import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
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
    
    // Rotation values for custom pose (in degrees, converted to radians)
    private static final float LEGS_BACKWARD_TILT = -20.0f * Mth.DEG_TO_RAD; // Legs tilt backward
    private static final float BODY_FORWARD_LEAN = 15.0f * Mth.DEG_TO_RAD; // Body leans forward
    private static final float HAND_ROTATION_X = -30.0f * Mth.DEG_TO_RAD; // Hand rotation for holding broomstick
    private static final float HAND_ROTATION_Y = 10.0f * Mth.DEG_TO_RAD;
    
    @Inject(method = "setupAnim", at = @At(value = "RETURN"))
    private void applyBroomRidingPose(HumanoidRenderState renderState, CallbackInfo ci) {
        // Get the entity from the render state - try accessing via reflection
        Entity entity = null;
        try {
            var entityField = EntityRenderState.class.getDeclaredField("entity");
            entityField.setAccessible(true);
            entity = (Entity) entityField.get(renderState);
        } catch (NoSuchFieldException e) {
            // Try alternative field names
            try {
                var entityField = EntityRenderState.class.getDeclaredField("f_entity");
                entityField.setAccessible(true);
                entity = (Entity) entityField.get(renderState);
            } catch (Exception e2) {
                LOGGER.debug("Could not access entity field from EntityRenderState: {}", e2.getMessage());
                return;
            }
        } catch (Exception e) {
            LOGGER.debug("Error accessing entity from render state: {}", e.getMessage());
            return;
        }
        
        if (entity == null) {
            return;
        }
        
        // Only apply to players riding brooms
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        
        if (!(player.getVehicle() instanceof BroomEntity)) {
            return;
        }
        
        LOGGER.debug("Applying custom broom riding pose to player {}", player.getName().getString());
        
        // Apply custom pose transformations to model parts
        // Rotate legs backward (tilt back)
        if (this.leftLeg != null) {
            this.leftLeg.xRot = LEGS_BACKWARD_TILT;
        }
        if (this.rightLeg != null) {
            this.rightLeg.xRot = LEGS_BACKWARD_TILT;
        }
        
        // Rotate body forward (lean forward)
        if (this.body != null) {
            this.body.xRot = BODY_FORWARD_LEAN;
        }
        
        // Position and rotate the free hand to hold broomstick
        // Determine which hand is free (prefer off-hand if main hand has item, else main hand)
        boolean mainHandHasItem = !player.getMainHandItem().isEmpty();
        boolean offHandHasItem = !player.getOffhandItem().isEmpty();
        
        // Use off-hand if main hand has item, otherwise use main hand
        boolean useOffHand = mainHandHasItem && !offHandHasItem;
        
        if (useOffHand && this.leftArm != null) {
            // Position off-hand to hold broomstick
            this.leftArm.xRot = HAND_ROTATION_X;
            this.leftArm.yRot = HAND_ROTATION_Y;
        } else if (!useOffHand && this.rightArm != null) {
            // Position main hand to hold broomstick
            this.rightArm.xRot = HAND_ROTATION_X;
            this.rightArm.yRot = -HAND_ROTATION_Y; // Mirror for right hand
        }
    }
}

