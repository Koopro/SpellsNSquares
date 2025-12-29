package at.koopro.spells_n_squares.features.transportation;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Enhanced flight physics system for broomsticks.
 * Handles acceleration, deceleration, turning, and stamina consumption.
 */
public final class BroomFlightPhysics {
    private BroomFlightPhysics() {
    }
    
    /**
     * Flight physics properties for a broomstick.
     */
    public static class FlightProperties {
        public final float maxSpeed;
        public final float acceleration;
        public final float deceleration;
        public final float turnRate;
        public final float maneuverability;
        public final float staminaConsumptionRate;
        public final float staminaRegenRate;
        
        public FlightProperties(float maxSpeed, float acceleration, float deceleration, 
                               float turnRate, float maneuverability, 
                               float staminaConsumptionRate, float staminaRegenRate) {
            this.maxSpeed = maxSpeed;
            this.acceleration = acceleration;
            this.deceleration = deceleration;
            this.turnRate = turnRate;
            this.maneuverability = maneuverability;
            this.staminaConsumptionRate = staminaConsumptionRate;
            this.staminaRegenRate = staminaRegenRate;
        }
    }
    
    /**
     * Gets flight properties for a broomstick tier.
     */
    public static FlightProperties getProperties(BroomstickItem.BroomstickTier tier) {
        return switch (tier) {
            case DEMO -> new FlightProperties(
                0.5f, 0.02f, 0.03f, 2.0f, 0.8f, 0.5f, 0.2f
            );
            case BLUEBOTTLE -> new FlightProperties(
                0.4f, 0.02f, 0.03f, 2.0f, 0.8f, 0.5f, 0.2f
            );
            case CLEANSWEEP_5 -> new FlightProperties(
                0.5f, 0.025f, 0.035f, 2.2f, 0.85f, 0.6f, 0.25f
            );
            case CLEANSWEEP_7 -> new FlightProperties(
                0.6f, 0.03f, 0.04f, 2.4f, 0.9f, 0.7f, 0.3f
            );
            case COMET_140 -> new FlightProperties(
                0.55f, 0.028f, 0.038f, 2.3f, 0.88f, 0.65f, 0.28f
            );
            case COMET_260 -> new FlightProperties(
                0.65f, 0.032f, 0.042f, 2.5f, 0.92f, 0.75f, 0.32f
            );
            case NIMBUS_2000 -> new FlightProperties(
                0.8f, 0.04f, 0.05f, 2.8f, 0.95f, 0.9f, 0.4f
            );
            case NIMBUS_2001 -> new FlightProperties(
                0.85f, 0.042f, 0.052f, 2.9f, 0.96f, 0.95f, 0.42f
            );
            case FIREBOLT -> new FlightProperties(
                1.2f, 0.05f, 0.06f, 3.2f, 1.0f, 1.1f, 0.5f
            );
            case FIREBOLT_SUPREME -> new FlightProperties(
                1.3f, 0.055f, 0.065f, 3.4f, 1.0f, 1.2f, 0.55f
            );
            case SILVER_ARROW -> new FlightProperties(
                0.9f, 0.045f, 0.055f, 3.0f, 0.98f, 1.0f, 0.45f
            );
            case SHOOTING_STAR -> new FlightProperties(
                0.45f, 0.022f, 0.032f, 2.1f, 0.82f, 0.55f, 0.22f
            );
            case BASIC -> new FlightProperties(
                0.5f, 0.02f, 0.03f, 2.0f, 0.8f, 0.5f, 0.2f
            );
            case RACING -> new FlightProperties(
                0.7f, 0.035f, 0.045f, 2.6f, 0.93f, 0.8f, 0.35f
            );
        };
    }
    
    /**
     * Calculates the new velocity for a player flying on a broomstick.
     * 
     * @param player The player flying
     * @param broomstick The broomstick item stack
     * @param currentVelocity Current velocity vector
     * @param inputForward Forward input (-1 to 1)
     * @param inputStrafe Strafe input (-1 to 1)
     * @param deltaTime Time delta in seconds
     * @return New velocity vector
     */
    public static Vec3 calculateVelocity(Player player, ItemStack broomstick, 
                                        Vec3 currentVelocity, float inputForward, 
                                        float inputStrafe, float deltaTime) {
        if (!(broomstick.getItem() instanceof BroomstickItem broomstickItem)) {
            return currentVelocity;
        }
        
        BroomstickData.BroomstickDataComponent data = BroomstickItem.getBroomstickData(broomstick);
        if (data == null || data.currentStamina() <= 0) {
            // No stamina - apply deceleration
            FlightProperties props = getProperties(broomstickItem.getTier());
            return currentVelocity.scale(Math.max(0, 1.0 - props.deceleration * deltaTime));
        }
        
        FlightProperties props = getProperties(broomstickItem.getTier());
        
        // Get player's look direction
        Vec3 lookDir = player.getLookAngle();
        Vec3 forward = new Vec3(lookDir.x, 0, lookDir.z).normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();
        
        // Calculate desired direction
        Vec3 desiredDir = forward.scale(inputForward)
            .add(right.scale(inputStrafe))
            .add(new Vec3(0, player.getDeltaMovement().y > 0 ? 1 : (player.getDeltaMovement().y < 0 ? -1 : 0), 0))
            .normalize();
        
        // Calculate desired speed
        float desiredSpeed = props.maxSpeed * Math.max(Math.abs(inputForward), Math.abs(inputStrafe));
        
        // Apply acceleration/deceleration
        float currentSpeed = (float) currentVelocity.length();
        float speedChange = 0;
        
        if (desiredSpeed > currentSpeed) {
            speedChange = Math.min(props.acceleration * deltaTime, desiredSpeed - currentSpeed);
        } else if (desiredSpeed < currentSpeed) {
            speedChange = -Math.min(props.deceleration * deltaTime, currentSpeed - desiredSpeed);
        }
        
        // Calculate new velocity
        Vec3 newVelocity = desiredDir.scale(currentSpeed + speedChange);
        
        // Apply turning rate limit
        if (currentSpeed > 0.01) {
            Vec3 currentDir = currentVelocity.normalize();
            float turnAngle = (float) Math.acos(Math.max(-1, Math.min(1, currentDir.dot(desiredDir))));
            float maxTurn = props.turnRate * deltaTime;
            
            if (turnAngle > maxTurn) {
                // Interpolate between current and desired direction
                float t = maxTurn / turnAngle;
                Vec3 interpolatedDir = currentDir.scale(1 - t).add(desiredDir.scale(t)).normalize();
                newVelocity = interpolatedDir.scale(newVelocity.length());
            }
        }
        
        return newVelocity;
    }
    
    /**
     * Consumes stamina based on flight activity.
     * 
     * @param data Current broomstick data
     * @param isMoving Whether the player is actively moving
     * @param deltaTime Time delta in seconds
     * @return Updated broomstick data component
     */
    public static BroomstickData.BroomstickDataComponent updateStamina(
            BroomstickData.BroomstickDataComponent data, boolean isMoving, float deltaTime) {
        float newStamina = data.currentStamina();
        
        if (isMoving) {
            // Consume stamina while moving
            newStamina = Math.max(0, newStamina - data.speed() * deltaTime * 0.1f);
        } else {
            // Regenerate stamina while idle
            newStamina = Math.min(data.maxStamina(), newStamina + data.speed() * deltaTime * 0.05f);
        }
        
        return new BroomstickData.BroomstickDataComponent(
            data.tier(),
            newStamina,
            data.maxStamina(),
            data.speed()
        );
    }
}

