package at.koopro.spells_n_squares.features.spell.entity;

import at.koopro.spells_n_squares.core.registry.ModEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Entity representing a Protego shield orb that protects a player.
 * The shield follows the player and blocks incoming projectiles.
 */
public class ShieldOrbEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(ShieldOrbEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(ShieldOrbEntity.class, EntityDataSerializers.INT);
    
    private int age = 0;
    private static final int MAX_AGE = 160; // 8 seconds (160 ticks)
    
    public ShieldOrbEntity(EntityType<? extends ShieldOrbEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }
    
    public ShieldOrbEntity(Level level, Player owner) {
        this(ModEntities.SHIELD_ORB.get(), level);
        this.setOwner(owner);
        this.setDuration(MAX_AGE);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNER_ID, -1);
        builder.define(DATA_DURATION, MAX_AGE);
    }
    
    public void setOwner(Player owner) {
        this.entityData.set(DATA_OWNER_ID, owner.getId());
    }
    
    public Player getOwner() {
        Entity owner = this.level().getEntity(this.entityData.get(DATA_OWNER_ID));
        if (owner instanceof Player player) {
            return player;
        }
        return null;
    }
    
    public void setDuration(int duration) {
        this.entityData.set(DATA_DURATION, duration);
    }
    
    public int getRemainingDuration() {
        return this.entityData.get(DATA_DURATION) - this.age;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.age++;
        
        Player owner = this.getOwner();
        if (owner == null || !owner.isAlive() || this.age >= MAX_AGE) {
            // Remove shield if owner is gone or duration expired
            this.discard();
            return;
        }
        
        // Follow the player at chest/center level (lower than eye height)
        Vec3 ownerPos = owner.position().add(0, owner.getBbHeight() * 0, 0);
        this.setPos(ownerPos.x, ownerPos.y, ownerPos.z);
        
        // Update duration
        int remaining = getRemainingDuration();
        if (remaining <= 0) {
            this.discard();
            return;
        }
        
        // Visual rendering is handled by ShieldOrbModelRenderer
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.age = input.getIntOr("Age", 0);
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Age", this.age);
    }
    
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        // Shield orbs are invulnerable
        return false;
    }
    
    @Override
    public boolean isPickable() {
        return false;
    }
    
    @Override
    public boolean isNoGravity() {
        return true;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }
    
    /**
     * Checks if a projectile should be blocked by this shield.
     */
    public boolean shouldBlockProjectile(Entity projectile) {
        if (projectile == null || projectile == this.getOwner()) {
            return false;
        }
        
        // Check if projectile is within shield radius
        Vec3 shieldPos = this.position();
        Vec3 projPos = projectile.position();
        double distance = shieldPos.distanceTo(projPos);
        
        return distance <= 2.0; // Shield radius
    }
}
