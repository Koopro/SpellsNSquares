package at.koopro.spells_n_squares.features.spell.entity;

import at.koopro.spells_n_squares.core.base.entity.BaseModEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Light orb entity for Lumos spell.
 * TODO: Implement light orb functionality
 */
public class LightOrbEntity extends BaseModEntity {
    public LightOrbEntity(EntityType<? extends LightOrbEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
