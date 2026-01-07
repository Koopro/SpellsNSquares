package at.koopro.spells_n_squares.features.spell.entity;

import at.koopro.spells_n_squares.core.base.entity.BaseModEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Lightning beam entity for lightning spells.
 * TODO: Implement lightning beam functionality
 */
public class LightningBeamEntity extends BaseModEntity {
    public LightningBeamEntity(EntityType<? extends LightningBeamEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
