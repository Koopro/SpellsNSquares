package at.koopro.spells_n_squares.features.spell.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import at.koopro.spells_n_squares.core.registry.ModEntities;

/**
 * Small flying light orb projectile: no damage, just travels forward, emits particles, then expires.
 */
public class LightOrbEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(LightOrbEntity.class, EntityDataSerializers.INT);
    private static final int DEFAULT_LIFETIME = 80; // 4 seconds

    public LightOrbEntity(EntityType<? extends LightOrbEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public LightOrbEntity(Level level, Player owner, Vec3 position, Vec3 velocity, int lifetimeTicks) {
        this(ModEntities.LIGHT_ORB.get(), level);
        this.setPos(position);
        this.setDeltaMovement(velocity);
        this.entityData.set(DATA_LIFETIME, Math.max(20, lifetimeTicks));
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LIFETIME, DEFAULT_LIFETIME);
    }

    @Override
    public void tick() {
        super.tick();

        // Move forward
        this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);

        // Particle trail (server-side spawning)
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 2, 0.04, 0.04, 0.04, 0.01);
            serverLevel.sendParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 1, 0.03, 0.03, 0.03, 0.01);
        }

        // Expire
        int lifetime = this.entityData.get(DATA_LIFETIME);
        if (this.tickCount >= lifetime) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
