package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.slf4j.Logger;

/**
 * Client-side render handler that modifies the player's pose when riding a broom.
 * Adjusts legs, body, and hand positions for a custom riding pose.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class BroomRiderRenderHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Rotation values for custom pose (in degrees, converted to radians)
    private static final float LEGS_BACKWARD_TILT = -20.0f; // Legs tilt backward
    private static final float BODY_FORWARD_LEAN = 15.0f; // Body leans forward
    private static final float HAND_ROTATION_X = -30.0f; // Hand rotation for holding broomstick
    private static final float HAND_ROTATION_Y = 10.0f;
    
    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre event) {
        // Try to access entity - use reflection as NeoForge API may vary
        LivingEntity entity = null;
        try {
            // Try getEntity() method
            var method = event.getClass().getMethod("getEntity");
            entity = (LivingEntity) method.invoke(event);
        } catch (Exception e1) {
            try {
                // Try entity() method
                var method = event.getClass().getMethod("entity");
                entity = (LivingEntity) method.invoke(event);
            } catch (Exception e2) {
                // If both fail, return early
                return;
            }
        }
        
        if (entity == null || !(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        
        // Check if player is riding a broom
        if (!(player.getVehicle() instanceof BroomEntity)) {
            return;
        }
        
        // Get the model - need to cast to access HumanoidModel
        var renderer = event.getRenderer();
        if (renderer == null) {
            return;
        }
        
        var modelObj = renderer.getModel();
        if (!(modelObj instanceof HumanoidModel)) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        HumanoidModel<?> model = (HumanoidModel<?>) modelObj;
        
        // Apply custom pose transformations to model parts
        // Rotate legs backward (tilt back)
        float legsTiltRad = Mth.DEG_TO_RAD * LEGS_BACKWARD_TILT;
        if (model.leftLeg != null) {
            model.leftLeg.xRot = legsTiltRad;
        }
        if (model.rightLeg != null) {
            model.rightLeg.xRot = legsTiltRad;
        }
        
        // Rotate body forward (lean forward)
        float bodyLeanRad = Mth.DEG_TO_RAD * BODY_FORWARD_LEAN;
        if (model.body != null) {
            model.body.xRot = bodyLeanRad;
        }
        
        // Position and rotate the free hand to hold broomstick
        // Determine which hand is free (prefer off-hand if main hand has item, else main hand)
        boolean mainHandHasItem = !player.getMainHandItem().isEmpty();
        boolean offHandHasItem = !player.getOffhandItem().isEmpty();
        
        // Use off-hand if main hand has item, otherwise use main hand
        boolean useOffHand = mainHandHasItem && !offHandHasItem;
        
        float handRotXRad = Mth.DEG_TO_RAD * HAND_ROTATION_X;
        float handRotYRad = Mth.DEG_TO_RAD * HAND_ROTATION_Y;
        
        if (useOffHand && model.leftArm != null) {
            // Position off-hand to hold broomstick
            model.leftArm.xRot = handRotXRad;
            model.leftArm.yRot = handRotYRad;
        } else if (!useOffHand && model.rightArm != null) {
            // Position main hand to hold broomstick
            model.rightArm.xRot = handRotXRad;
            model.rightArm.yRot = -handRotYRad; // Mirror for right hand
        }
    }
}

