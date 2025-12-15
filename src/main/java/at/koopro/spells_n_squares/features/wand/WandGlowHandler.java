package at.koopro.spells_n_squares.features.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import at.koopro.spells_n_squares.SpellsNSquares;

/**
 * Client-side handler for wand tip position calculation when Lumos is active.
 * Particle rendering removed - to be implemented later.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class WandGlowHandler {
    // Wand tip offset from hand (in blocks)
    // Based on the wand model: wand is 13 blocks tall, tip is at the end
    private static final double WAND_TIP_OFFSET = 0.65; // Approximately 13/20 blocks
    
    
    /**
     * Calculates the position of the wand tip in world coordinates.
     * @param mc The Minecraft instance
     * @param player The player holding the wand
     * @param isMainHand Whether the wand is in the main hand
     * @param isFirstPerson Whether we're in first person view
     * @return The world position of the wand tip
     */
    private static Vec3 calculateWandTipPosition(Minecraft mc, Player player, boolean isMainHand, boolean isFirstPerson) {
        Vec3 lookVec = player.getLookAngle();
        
        if (isFirstPerson) {
            // First person: use camera position and look direction
            // The wand tip should be relative to the camera/eye position
            Vec3 eyePos = player.getEyePosition();
            
            // Calculate hand position offset from eye
            // In first person, the hand is slightly below and forward from the eye
            double handOffsetX = lookVec.x * 0.3;
            double handOffsetY = -0.2; // Hand is below eye level
            double handOffsetZ = lookVec.z * 0.3;
            
            // Adjust for which hand is holding the wand
            if (!isMainHand) {
                // Off-hand is typically on the left side
                Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
                handOffsetX += rightVec.x * 0.2;
                handOffsetZ += rightVec.z * 0.2;
            } else {
                // Main hand is typically on the right side
                Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
                handOffsetX -= rightVec.x * 0.2;
                handOffsetZ -= rightVec.z * 0.2;
            }
            
            Vec3 handPos = eyePos.add(handOffsetX, handOffsetY, handOffsetZ);
            
            // Calculate tip position: hand position + wand length along look direction
            return handPos.add(lookVec.scale(WAND_TIP_OFFSET));
        } else {
            // Third person: use the player's actual hand position from the model
            // Get the player's body position
            Vec3 bodyPos = player.position();
            
            // Calculate hand position based on player model
            // The hand is typically at shoulder height, slightly forward
            double bodyHeight = player.getBbHeight();
            double handHeight = bodyHeight * 0.6; // Hand is at about 60% of body height
            
            // Calculate hand position relative to body center
            // Use player's yaw for horizontal direction
            float yaw = player.getYRot();
            double yawRad = Math.toRadians(yaw);
            
            // Hand offset from body center (forward and to the side)
            double forwardOffset = 0.3; // Hand is forward from body
            double sideOffset = isMainHand ? -0.3 : 0.3; // Main hand on right, off-hand on left
            
            double handX = bodyPos.x + Math.sin(yawRad) * forwardOffset + Math.cos(yawRad) * sideOffset;
            double handY = bodyPos.y + handHeight;
            double handZ = bodyPos.z - Math.cos(yawRad) * forwardOffset + Math.sin(yawRad) * sideOffset;
            
            Vec3 handPos = new Vec3(handX, handY, handZ);
            
            // Calculate tip position: hand position + wand length along look direction
            return handPos.add(lookVec.scale(WAND_TIP_OFFSET));
        }
    }
    
}

