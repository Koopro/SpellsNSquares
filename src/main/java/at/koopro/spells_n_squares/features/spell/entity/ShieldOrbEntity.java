package at.koopro.spells_n_squares.features.spell.entity;

import at.koopro.spells_n_squares.core.base.entity.BaseModEntity;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Shield orb entity for Protego spell.
 * Provides collision detection, damage absorption, and visual feedback.
 */
public class ShieldOrbEntity extends BaseModEntity {
    
    // Owner UUID stored in memory (not synched to reduce network traffic)
    private UUID ownerUUID;
    
    private static final int LIFETIME = 160; // 8 seconds (matches ProtegoSpell.EFFECT_DURATION)
    private static final float SHIELD_RADIUS = 1.5f;
    private static final float FOLLOW_DISTANCE = 0.5f;
    private static final float DAMAGE_ABSORPTION = 0.6f; // Absorb 60% of damage
    
    private int age = 0;
    private float health = 100.0f; // Shield health (can be depleted by damage)
    private float maxHealth = 100.0f;
    
    public ShieldOrbEntity(EntityType<? extends ShieldOrbEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setInvulnerable(true); // Shield itself is invulnerable, but can absorb damage
    }
    
    @Override
    protected void defineCustomSynchedData(SynchedEntityData.Builder builder) {
        // No synched data needed - owner UUID is stored in memory only
    }
    
    /**
     * Sets the owner player for this shield.
     */
    public void setOwner(Player player) {
        if (player != null) {
            this.ownerUUID = player.getUUID();
        } else {
            this.ownerUUID = null;
        }
    }
    
    /**
     * Gets the owner player if available.
     */
    public Player getOwner() {
        if (ownerUUID == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getPlayerByUUID(ownerUUID);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level().isClientSide()) {
            // Client-side visual effects
            if (this.random.nextInt(5) == 0) {
                this.level().addParticle(
                    ParticleTypes.ENCHANT,
                    this.getX() + (this.random.nextDouble() - 0.5) * SHIELD_RADIUS,
                    this.getY() + (this.random.nextDouble() - 0.5) * SHIELD_RADIUS,
                    this.getZ() + (this.random.nextDouble() - 0.5) * SHIELD_RADIUS,
                    0, 0, 0
                );
            }
            return;
        }
        
        // Server-side logic
        age++;
        
        // Remove if expired
        if (age >= LIFETIME || health <= 0) {
            this.discard();
            return;
        }
        
        // Follow owner
        Player owner = getOwner();
        if (owner != null && owner.isAlive()) {
            Vec3 ownerPos = owner.position().add(0, owner.getBbHeight() * 0.5, 0);
            Vec3 currentPos = this.position();
            Vec3 direction = ownerPos.subtract(currentPos);
            double distance = direction.length();
            
            if (distance > FOLLOW_DISTANCE) {
                // Move towards owner
                Vec3 movement = direction.normalize().scale(Math.min(distance * 0.1, 0.3));
                this.setDeltaMovement(movement);
                Vec3 newPos = currentPos.add(movement);
                this.setPos(newPos.x, newPos.y, newPos.z);
            } else {
                // Stay near owner
                this.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            // No owner or owner is dead, remove shield
            this.discard();
            return;
        }
        
        // Check for collisions with projectiles and entities
        checkCollisions();
    }
    
    /**
     * Checks for collisions with projectiles and other entities that could damage the owner.
     */
    private void checkCollisions() {
        if (this.level().isClientSide()) {
            return;
        }
        
        AABB shieldBounds = this.getBoundingBox().inflate(SHIELD_RADIUS);
        Player owner = getOwner();
        if (owner == null) {
            return;
        }
        
        // Check for projectiles
        for (Entity entity : this.level().getEntitiesOfClass(Projectile.class, shieldBounds)) {
            if (entity != this && !entity.isRemoved()) {
                // Check if projectile is targeting the owner
                Entity shooter = null;
                if (entity instanceof Projectile projectile) {
                    shooter = projectile.getOwner();
                }
                
                // Don't block projectiles from the owner
                if (shooter == owner) {
                    continue;
                }
                
                // Block the projectile and create visual feedback
                blockProjectile(entity);
            }
        }
        
        // Check for other entities that might be attacking
        for (Entity entity : this.level().getEntitiesOfClass(Entity.class, shieldBounds)) {
            if (entity != this && entity != owner && !entity.isRemoved()) {
                // Check if entity is attacking the owner (Mob has getTarget method)
                if (entity instanceof Mob mob && mob.getTarget() == owner) {
                    // Entity is targeting owner, create shield feedback
                    createShieldFeedback(entity.position());
                }
            }
        }
    }
    
    /**
     * Blocks a projectile and creates visual feedback.
     */
    private void blockProjectile(Entity projectile) {
        if (this.level().isClientSide()) {
            return;
        }
        
        // Create visual feedback
        createShieldFeedback(projectile.position());
        
        // Check if this is a spell projectile and trigger clash effect
        if (projectile instanceof Projectile && this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            Player projectileCaster = null;
            if (projectile instanceof Projectile proj) {
                Entity owner = proj.getOwner();
                if (owner instanceof Player player) {
                    projectileCaster = player;
                }
            }
            
            Player shieldOwner = getOwner();
            if (projectileCaster != null && shieldOwner != null && projectileCaster != shieldOwner) {
                // Trigger clash effect on shield
                net.minecraft.world.phys.Vec3 wand1Pos = projectileCaster.getEyePosition()
                    .add(projectileCaster.getLookAngle().scale(0.5));
                net.minecraft.world.phys.Vec3 wand2Pos = shieldOwner.getEyePosition()
                    .add(shieldOwner.getLookAngle().scale(0.5));
                net.minecraft.world.phys.Vec3 collisionPoint = projectile.position();
                
                at.koopro.spells_n_squares.features.spell.clash.SpellClashVisuals.createClashEffect(
                    serverLevel, wand1Pos, wand2Pos, collisionPoint, null, null, 0.8);
            }
        }
        
        // Reduce projectile damage or remove it
        if (projectile instanceof Projectile) {
            // Remove the projectile
            projectile.discard();
        }
        
        // Reduce shield health
        health -= 10.0f;
        if (health <= 0) {
            this.discard();
        }
    }
    
    /**
     * Creates visual and audio feedback when shield is hit.
     */
    private void createShieldFeedback(Vec3 hitPos) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Sound effect
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
        
        // Enhanced shield impact effect using shield pattern
        at.koopro.spells_n_squares.core.fx.patterns.SpellFxPatterns.shield()
            .center(hitPos)
            .particle(ParticleTypes.ENCHANT)
            .count(20)
            .radius(0.5)
            .impactRing(true)
            .play(serverLevel);
        
        // Use protego templates
        at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry.ParticleEffectTemplate impactRing = 
            at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry.get(
                net.minecraft.resources.Identifier.fromNamespaceAndPath("spells_n_squares", "protego_impact_ring"));
        if (impactRing != null) {
            impactRing.spawn(serverLevel, hitPos, 1.0);
        }
        
        ParticlePool.queueParticle(
            serverLevel,
            ParticleTypes.ELECTRIC_SPARK,
            hitPos,
            10,
            0.3, 0.3, 0.3,
            0.1
        );
    }
    
    @Override
    protected boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        // Shield absorbs damage
        Player owner = getOwner();
        if (owner != null && source.getEntity() != owner) {
            // Create visual feedback
            createShieldFeedback(this.position());
            
            // Reduce shield health
            health -= amount * DAMAGE_ABSORPTION;
            if (health <= 0) {
                this.discard();
            }
            
            // Shield itself doesn't take damage, but absorbs it
            return false;
        }
        
        return false;
    }
    
    @Override
    protected void saveCustomData(ValueOutput output) {
        if (ownerUUID != null) {
            output.store("OwnerUUID", UUIDUtil.CODEC, ownerUUID);
        }
        output.store("Age", Codec.INT, age);
        output.store("Health", Codec.FLOAT, health);
    }
    
    @Override
    protected void loadCustomData(ValueInput input) {
        this.ownerUUID = input.read("OwnerUUID", UUIDUtil.CODEC).orElse(null);
        this.age = input.read("Age", Codec.INT).orElse(0);
        this.health = input.read("Health", Codec.FLOAT).orElse(maxHealth);
    }
    
    @Override
    public void refreshDimensions() {
        // Shield has a larger bounding box for collision detection
        super.refreshDimensions();
        AABB currentBB = this.getBoundingBox();
        AABB expandedBB = currentBB.inflate(SHIELD_RADIUS);
        this.setBoundingBox(expandedBB);
    }
    
    /**
     * Gets the current shield health.
     */
    public float getHealth() {
        return health;
    }
    
    /**
     * Gets the maximum shield health.
     */
    public float getMaxHealth() {
        return maxHealth;
    }
}
