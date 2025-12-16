package at.koopro.spells_n_squares.features.spell.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Dummy player entity for spell testing/targeting.
 * TODO: Implement dummy player functionality
 */
public class DummyPlayerEntity extends Entity {
    public DummyPlayerEntity(EntityType<? extends DummyPlayerEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // Define synced data if needed
    }
    
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // Dummy players can be damaged for testing purposes
        return true;
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // No additional data to save
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // No additional data to load
    }
}

