package at.koopro.spells_n_squares.features.spell.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import at.koopro.spells_n_squares.core.registry.ModEntities;

/**
 * Short-lived beam entity between wand tip and target.
 * Handles damage on the server; visual mesh is rendered client-side.
 */
public class LightningBeamEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(LightningBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_END_X = SynchedEntityData.defineId(LightningBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_END_Y = SynchedEntityData.defineId(LightningBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_END_Z = SynchedEntityData.defineId(LightningBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(LightningBeamEntity.class, EntityDataSerializers.INT);

    private static final int DEFAULT_LIFETIME = 8; // ticks

    private Player owner; // server-side only, for damage filtering
    private boolean appliedDamage = false;

    public LightningBeamEntity(EntityType<? extends LightningBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public LightningBeamEntity(Level level, Player owner, Vec3 start, Vec3 end, int color, int lifetimeTicks) {
        this(ModEntities.LIGHTNING_BEAM.get(), level);
        this.setPos(start);
        this.owner = owner;
        this.entityData.set(DATA_LIFETIME, Math.max(2, lifetimeTicks));
        this.entityData.set(DATA_END_X, (float) end.x);
        this.entityData.set(DATA_END_Y, (float) end.y);
        this.entityData.set(DATA_END_Z, (float) end.z);
        this.entityData.set(DATA_COLOR, color);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LIFETIME, DEFAULT_LIFETIME);
        builder.define(DATA_END_X, 0.0f);
        builder.define(DATA_END_Y, 0.0f);
        builder.define(DATA_END_Z, 0.0f);
        builder.define(DATA_COLOR, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            // Apply damage once near the end point
            if (!appliedDamage) {
                appliedDamage = true;

                Vec3 end = getEnd();
                double radius = 1.0;
                AABB box = new AABB(
                    end.x - radius, end.y - radius, end.z - radius,
                    end.x + radius, end.y + radius, end.z + radius
                );

                for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
                    if (target == owner) {
                        continue;
                    }
                    target.hurtServer(serverLevel, level().damageSources().magic(), 12.0f);
                    break;
                }
            }

            int lifetime = this.entityData.get(DATA_LIFETIME);
            if (this.tickCount >= lifetime) {
                this.discard();
            }
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

    public Vec3 getEnd() {
        return new Vec3(
            this.entityData.get(DATA_END_X),
            this.entityData.get(DATA_END_Y),
            this.entityData.get(DATA_END_Z)
        );
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
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

