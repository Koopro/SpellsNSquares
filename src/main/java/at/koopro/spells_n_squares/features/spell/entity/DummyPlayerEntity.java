package at.koopro.spells_n_squares.features.spell.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Simple standing humanoid dummy entity for testing spells.
 * Has health and can be damaged, but no AI/movement.
 */
public class DummyPlayerEntity extends Mob {

    public DummyPlayerEntity(EntityType<? extends DummyPlayerEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // No AI – dummy just stands still.
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void doPush(net.minecraft.world.entity.Entity entity) {
        // Keep default small push behaviour if needed
        super.doPush(entity);
    }
}
