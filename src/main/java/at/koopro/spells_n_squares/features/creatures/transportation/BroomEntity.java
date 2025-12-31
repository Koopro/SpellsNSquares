package at.koopro.spells_n_squares.features.creatures.transportation;

import at.koopro.spells_n_squares.features.transportation.BroomstickData;
import at.koopro.spells_n_squares.features.transportation.BroomstickItem;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import at.koopro.spells_n_squares.features.transportation.BroomFlightPhysics;
import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Broom entity that can be mounted and ridden with GeckoLib rendering.
 * Spawned from BroomstickItem and converts back to item when dismounted.
 * Uses Mob (LivingEntity) for better movement handling.
 */
public class BroomEntity extends Mob implements GeoEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<ItemStack> DATA_BROOM_ITEM = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.ITEM_STACK);
    
    private BroomstickItem.BroomstickTier tier;
    private BroomstickData.BroomstickDataComponent broomData;
    
    // Movement input from client (updated via network packet)
    private float forwardInput = 0.0f;
    private float strafeInput = 0.0f;
    private boolean jumpInput = false;
    
    public BroomEntity(EntityType<? extends BroomEntity> type, Level level) {
        super(type, level);
        // Disable AI - we control movement manually
        this.setNoAi(true);
        // Make invulnerable
        this.setInvulnerable(true);
        // Enable physics by default so it sits on the ground when not ridden
        this.noPhysics = false;
    }
    
    /**
     * Creates attributes for the broom entity.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 1.0D) // Minimal health since it's invulnerable
            .add(Attributes.MOVEMENT_SPEED, 0.0D) // Movement handled manually
            .add(Attributes.FLYING_SPEED, 0.0D); // Flying handled manually
    }
    
    @Override
    protected void registerGoals() {
        // No AI goals - movement is controlled manually
    }
    
    public BroomEntity(EntityType<? extends BroomEntity> type, Level level, ItemStack broomItem) {
        this(type, level);
        setBroomItem(broomItem);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); // Define Mob/LivingEntity synched data first (values 0-7)
        builder.define(DATA_BROOM_ITEM, ItemStack.EMPTY); // Our custom data (value 8)
    }
    
    /**
     * Sets the broomstick item that spawned this entity.
     */
    public void setBroomItem(ItemStack item) {
        if (item.getItem() instanceof BroomstickItem broomstick) {
            this.tier = broomstick.getTier();
            this.broomData = BroomstickItem.getBroomstickData(item);
            this.entityData.set(DATA_BROOM_ITEM, item.copy());
        }
    }
    
    /**
     * Gets the broomstick item stored in this entity.
     */
    public ItemStack getBroomItem() {
        ItemStack item = this.entityData.get(DATA_BROOM_ITEM);
        if (item.isEmpty() && this.tier != null && this.broomData != null) {
            // Reconstruct item if needed
            item = createItemFromData();
            this.entityData.set(DATA_BROOM_ITEM, item);
        }
        return item;
    }
    
    /**
     * Creates an item stack from the stored tier and data.
     */
    private ItemStack createItemFromData() {
        if (this.tier == null) {
            return ItemStack.EMPTY;
        }
        
        // Find the correct item registry entry
        ItemStack item = switch (this.tier) {
            case DEMO -> new ItemStack(TransportationRegistry.DEMO_BROOM.get());
            case BASIC -> new ItemStack(TransportationRegistry.BROOMSTICK_BASIC.get());
            case BLUEBOTTLE -> new ItemStack(TransportationRegistry.BROOMSTICK_BLUEBOTTLE.get());
            case SHOOTING_STAR -> new ItemStack(TransportationRegistry.BROOMSTICK_SHOOTING_STAR.get());
            case CLEANSWEEP_5 -> new ItemStack(TransportationRegistry.BROOMSTICK_CLEANSWEEP_5.get());
            case CLEANSWEEP_7 -> new ItemStack(TransportationRegistry.BROOMSTICK_CLEANSWEEP_7.get());
            case COMET_140 -> new ItemStack(TransportationRegistry.BROOMSTICK_COMET_140.get());
            case COMET_260 -> new ItemStack(TransportationRegistry.BROOMSTICK_COMET_260.get());
            case NIMBUS_2000 -> new ItemStack(TransportationRegistry.BROOMSTICK_NIMBUS_2000.get());
            case NIMBUS_2001 -> new ItemStack(TransportationRegistry.BROOMSTICK_NIMBUS_2001.get());
            case SILVER_ARROW -> new ItemStack(TransportationRegistry.BROOMSTICK_SILVER_ARROW.get());
            case RACING -> new ItemStack(TransportationRegistry.BROOMSTICK_RACING.get());
            case FIREBOLT -> new ItemStack(TransportationRegistry.BROOMSTICK_FIREBOLT.get());
            case FIREBOLT_SUPREME -> new ItemStack(TransportationRegistry.BROOMSTICK_FIREBOLT_SUPREME.get());
        };
        
        // Restore data if available
        if (this.broomData != null) {
            item.set(BroomstickData.BROOMSTICK_DATA.get(), this.broomData);
        }
        
        return item;
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // Load broom item using Codec
        ItemStack item = input.read("BroomItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        if (!item.isEmpty()) {
            setBroomItem(item);
        }
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        ItemStack item = getBroomItem();
        if (!item.isEmpty()) {
            output.store("BroomItem", ItemStack.OPTIONAL_CODEC, item);
        }
    }
    
    @Override
    public void tick() {
        Player rider = getControllingPassenger();
        
        // Update yaw (horizontal rotation) to match player's look direction
        // Pitch will be calculated after movement is determined
        if (rider != null) {
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
        }
        
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            // Check for shift key dismount (check every tick for responsiveness)
            if (rider != null && rider.isShiftKeyDown()) {
                // Player is holding shift - dismount
                rider.stopRiding();
                if (rider instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(
                        Component.literal("§aDismounted broom!"), 
                        true);
                }
                // Re-enable physics when dismounted
                this.noPhysics = false;
                this.setNoGravity(false); // Re-enable gravity when dismounted
                super.tick(); // Call super.tick() after dismounting
                return;
            }
            
            // Set noPhysics before super.tick() to prevent default physics from interfering
            if (rider != null) {
                this.noPhysics = true; // Allow flying when ridden
                this.setNoGravity(true); // Disable gravity when ridden
                
                // Calculate and set movement BEFORE super.tick() so it applies correctly
                calculateRiddenMovement(rider);
                
                // Store the movement we calculated
                Vec3 calculatedMovement = this.getDeltaMovement();
                
                super.tick();
                
                // When noPhysics is true, we need to manually apply movement
                // Use move() to apply movement properly, which handles collision and position updates
                
                // Check if player has any input
                boolean hasInput = Math.abs(this.forwardInput) > 0.01f || 
                                 Math.abs(this.strafeInput) > 0.01f || 
                                 this.jumpInput;
                
                // When idle, stay still in the air (no movement at all)
                Vec3 finalMovement;
                if (!hasInput) {
                    // No input - zero out all movement and ensure deltaMovement is zero
                    finalMovement = Vec3.ZERO;
                    this.setDeltaMovement(Vec3.ZERO);
                } else {
                    // Has input - use calculated movement
                    finalMovement = calculatedMovement;
                    this.setDeltaMovement(calculatedMovement);
                }
                
                // Apply movement using move() which properly handles position updates
                if (finalMovement.lengthSqr() > 0.0001) {
                    this.move(net.minecraft.world.entity.MoverType.SELF, finalMovement);
                }
                
                // Update rotation to match player's look direction
                this.setYRot(rider.getYRot());
                this.setXRot(rider.getXRot() * 0.5f);
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();
                
                // Align player's body rotation with broom direction (for custom pose)
                rider.setYBodyRot(this.getYRot());
                rider.yBodyRotO = this.getYRot();
                
                // Delta movement is already set correctly above (before movement application)
                // Just ensure it stays zero when idle for next tick
                if (!hasInput) {
                    this.setDeltaMovement(Vec3.ZERO);
                }
            } else {
                this.noPhysics = false; // Use physics when not ridden
                this.setNoGravity(false); // Re-enable gravity when not ridden
                super.tick();
            }
            
            if (rider == null) {
                // No rider - hover 1 block above the ground
                this.noPhysics = false;
                
                // Calculate desired hover position (1 block above ground)
                int blockX = (int) Math.floor(this.getX());
                int blockZ = (int) Math.floor(this.getZ());
                double groundY = serverLevel.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);
                double targetY = groundY + 1.0;
                
                // Get current position
                double currentY = this.getY();
                double diffY = targetY - currentY;
                
                // Smoothly move towards hover position
                if (Math.abs(diffY) > 0.1) {
                    double moveY = Math.signum(diffY) * Math.min(Math.abs(diffY), 0.1); // Max 0.1 blocks per tick
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0).add(0, moveY, 0));
                } else {
                    // Close enough - maintain position
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }
                
                // Apply slight horizontal damping to prevent drifting
                Vec3 horizontalVel = this.getDeltaMovement().multiply(1.0, 0.0, 1.0);
                this.setDeltaMovement(horizontalVel.multiply(0.95, 1.0, 0.95).add(0, this.getDeltaMovement().y, 0));
            }
            
            // Update stamina
            if (this.broomData != null && this.tickCount % 5 == 0) {
                Vec3 velocity = this.getDeltaMovement();
                boolean isMoving = velocity.lengthSqr() > 0.01;
                float deltaTime = 0.25f; // 5 ticks = 0.25 seconds
                
                this.broomData = BroomFlightPhysics.updateStamina(this.broomData, isMoving, deltaTime);
                
                // Update item data
                ItemStack item = getBroomItem();
                if (!item.isEmpty()) {
                    item.set(BroomstickData.BROOMSTICK_DATA.get(), this.broomData);
                    this.entityData.set(DATA_BROOM_ITEM, item);
                }
            }
            
            // Visual effects
            if (this.tickCount % 10 == 0) {
                Vec3 pos = this.position();
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z,
                    2, 0.2, 0.1, 0.2, 0.01);
            }
        } else {
            // Client side - just call super.tick()
            super.tick();
        }
    }
    
    /**
     * Handles player interaction with the broom entity.
     * Right-click to mount, Shift+Right-click to pickup.
     */
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(this.level() instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        // Shift + Right-click: Pick up broom (convert to item)
        if (player.isShiftKeyDown()) {
            // Check if player has space in inventory
            ItemStack broomItem = getBroomItem();
            if (!broomItem.isEmpty()) {
                // Try to add to inventory
                if (player.getInventory().add(broomItem)) {
                    // Successfully added to inventory
                    serverLevel.playSound(null, this.position().x, this.position().y, this.position().z,
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                    serverPlayer.displayClientMessage(
                        Component.literal("§aBroom picked up!"), 
                        true);
                    this.remove(RemovalReason.DISCARDED);
                    return InteractionResult.SUCCESS;
                } else {
                    // Inventory full - drop item
                    dropBroomItem();
                    serverLevel.playSound(null, this.position().x, this.position().y, this.position().z,
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                    serverPlayer.displayClientMessage(
                        Component.literal("§aBroom dropped (inventory full)!"), 
                        true);
                    this.remove(RemovalReason.DISCARDED);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        }
        
        // Right-click: Mount the broom
        // Check that player has at least one empty hand (main or off hand)
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        boolean hasEmptyHand = mainHand.isEmpty() || offHand.isEmpty();
        
        if (!hasEmptyHand) {
            serverPlayer.displayClientMessage(
                Component.literal("§cYou need at least one empty hand to mount the broom!"), 
                true);
            return InteractionResult.FAIL;
        }
        
        if (!this.isVehicle() && !player.isPassenger()) {
            player.startRiding(this);
            serverPlayer.displayClientMessage(
                Component.literal("§aMounted broom! Hold Shift to dismount."), 
                true);
            LOGGER.info("[BroomEntity] Player {} mounted broom", serverPlayer.getName().getString());
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Updates movement input from client.
     * Called when receiving BroomMovementInputPayload.
     */
    public void updateMovementInput(float forward, float strafe, boolean jump) {
        this.forwardInput = forward;
        this.strafeInput = strafe;
        this.jumpInput = jump;
    }
    
    /**
     * Calculates and sets movement delta when ridden.
     * Called BEFORE super.tick() so the movement is applied during super.tick().
     */
    private void calculateRiddenMovement(Player rider) {
        if (this.level().isClientSide()) {
            return;
        }
        
        // Get current velocity
        Vec3 currentVelocity = this.getDeltaMovement();
        
        // Calculate new velocity
        ItemStack broomItem = getBroomItem();
        if (!broomItem.isEmpty() && this.broomData != null && this.broomData.currentStamina() > 0) {
            float deltaTime = 0.05f; // 1 tick = 0.05 seconds
            
            // Get flight properties
            BroomstickItem.BroomstickTier tier = ((BroomstickItem) broomItem.getItem()).getTier();
            BroomFlightPhysics.FlightProperties props = BroomFlightPhysics.getProperties(tier);
            
            // Get player's look direction vector (where they're looking)
            Vec3 lookDir = rider.getLookAngle();
            
            // Get forward and right vectors for strafe movement
            Vec3 forwardDir = new Vec3(lookDir.x, 0, lookDir.z).normalize();
            Vec3 rightDir = forwardDir.cross(new Vec3(0, 1, 0)).normalize();
            
            float forward = this.forwardInput;
            float strafe = this.strafeInput;
            
            // Calculate movement direction based on input
            // Forward/backward moves in the direction the player is looking (including pitch)
            // Strafe moves left/right relative to look direction
            Vec3 moveDir = Vec3.ZERO;
            
            if (Math.abs(forward) > 0.01f) {
                // Forward/backward movement follows look direction (3D, includes pitch)
                moveDir = moveDir.add(lookDir.scale(forward));
            }
            
            if (Math.abs(strafe) > 0.01f) {
                // Strafe left/right is horizontal only (perpendicular to look direction)
                moveDir = moveDir.add(rightDir.scale(strafe));
            }
            
            // If no input, don't allow vertical movement - maintain hover instead
            // Vertical movement only happens when there's forward/strafe input
            if (Math.abs(forward) < 0.01f && Math.abs(strafe) < 0.01f) {
                // No horizontal input - no movement direction (will maintain hover)
                moveDir = Vec3.ZERO;
            }
            
            double moveLength = moveDir.length();
            
            Vec3 desiredVelocity = Vec3.ZERO;
            if (moveLength > 0.01) {
                // Normalize the combined direction and scale by max speed
                desiredVelocity = moveDir.normalize().scale(props.maxSpeed);
            }
            
            // Apply acceleration/deceleration
            float currentSpeed = (float) currentVelocity.length();
            float desiredSpeed = (float) desiredVelocity.length();
            float speedChange = 0;
            
            if (desiredSpeed > currentSpeed) {
                speedChange = Math.min(props.acceleration * deltaTime, desiredSpeed - currentSpeed);
            } else if (desiredSpeed < currentSpeed) {
                speedChange = -Math.min(props.deceleration * deltaTime, currentSpeed - desiredSpeed);
            }
            
            // Calculate new velocity direction
            Vec3 newVelocityDir = desiredSpeed > 0.01 ? desiredVelocity.normalize() : 
                                  (currentSpeed > 0.01 ? currentVelocity.normalize() : Vec3.ZERO);
            Vec3 newVelocity = newVelocityDir.scale(currentSpeed + speedChange);
            
            // Check for space key boost (jump key)
            if (this.jumpInput) {
                // Apply extra boost in current movement direction (or up if not moving)
                Vec3 boostDir = moveLength > 0.01 ? moveDir.normalize() : new Vec3(0, 1, 0);
                Vec3 boost = boostDir.scale(0.8); // Boost strength
                newVelocity = newVelocity.add(boost);
            }
            
            // When noPhysics is true (when ridden), don't apply gravity
            // The entity should hover in place when there's no input
            // Only apply deceleration if there's no input
            if (moveLength < 0.01 && !this.jumpInput) {
                // No input - completely zero out all velocity to prevent any falling
                newVelocity = Vec3.ZERO;
            }
            
            this.setDeltaMovement(newVelocity);
        } else {
            // No stamina - apply deceleration and gravity
            Vec3 decelerated = currentVelocity.scale(0.95);
            this.setDeltaMovement(decelerated.add(0, -0.05, 0));
            if (this.tickCount % 20 == 0) {
                LOGGER.warn("[BroomEntity] No stamina! Decelerating.");
            }
        }
        
        // Update rotation to match player's look direction
        this.setYRot(rider.getYRot());
        this.setXRot(rider.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
    
    @Override
    public void travel(Vec3 movementInput) {
        // When noPhysics is true (when ridden), we handle movement ourselves
        // The movement is already calculated in calculateRiddenMovement and set via setDeltaMovement
        // Movement is applied manually in tick() method, so we just need to prevent default physics
        if (this.noPhysics) {
            // Don't apply default physics - movement is handled manually in tick()
            // Check if there's any input - if not, don't apply friction (keep velocity at zero)
            Player rider = getControllingPassenger();
            if (rider != null) {
                boolean hasInput = Math.abs(this.forwardInput) > 0.01f || 
                                 Math.abs(this.strafeInput) > 0.01f || 
                                 this.jumpInput;
                if (!hasInput) {
                    // No input - keep velocity at zero (stay still in air, no falling)
                    this.setDeltaMovement(Vec3.ZERO);
                } else {
                    // Has input - apply slight friction to horizontal movement only
                    Vec3 deltaMovement = this.getDeltaMovement();
                    this.setDeltaMovement(new Vec3(deltaMovement.x * 0.91, deltaMovement.y, deltaMovement.z * 0.91));
                }
            } else {
                // No rider - apply friction normally
                Vec3 deltaMovement = this.getDeltaMovement();
                this.setDeltaMovement(deltaMovement.scale(0.91));
            }
        } else {
            // Use default movement when not ridden
            super.travel(movementInput);
        }
    }
    
    @Override
    public boolean canRiderInteract() {
        return true;
    }
    
    @Override
    public boolean shouldRiderSit() {
        return true; // Player sits on broom
    }
    
    @Override
    protected boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return this.getPassengers().isEmpty() && !this.isRemoved();
    }
    
    @Override
    public boolean isPickable() {
        return true; // Allow player to interact with the entity
    }
    
    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && reason != RemovalReason.DISCARDED) {
            dropBroomItem();
        }
        super.remove(reason);
    }
    
    /**
     * Drops the broom item at the entity's position.
     */
    private void dropBroomItem() {
        ItemStack item = getBroomItem();
        if (!item.isEmpty() && !this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                serverLevel, pos.x, pos.y, pos.z, item
            );
            itemEntity.setDefaultPickUpDelay();
            serverLevel.addFreshEntity(itemEntity);
            
            // Play sound
            serverLevel.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
    }
    
    /**
     * Gets the controlling passenger (rider).
     */
    public Player getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof Player ? (Player) passenger : null;
    }
    
    @Override
    protected void positionRider(net.minecraft.world.entity.Entity passenger, net.minecraft.world.entity.Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            // Position rider lower and forward on the broom for custom pose
            double yOffset = this.getDimensions(this.getPose()).height() * 0.3 + 0.05; // Lower position
            // Add forward offset to position body in front of broom
            Vec3 forward = Vec3.directionFromRotation(0, this.getYRot());
            Vec3 offset = new Vec3(0.0, yOffset, 0.0).add(forward.scale(0.2)); // Forward offset
            moveFunction.accept(passenger, this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z);
            // Don't override player's rotation - let them look around freely
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add idle animation when flying
        // Animation controllers can be added here if needed
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

