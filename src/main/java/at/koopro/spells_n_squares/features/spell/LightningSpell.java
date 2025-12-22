package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Lightning spell that fires a deadly beam from the wand tip.
 */
public class LightningSpell implements Spell {
    
    private static final int COOLDOWN = 80; // 4 seconds
    private static final double RANGE = 32.0;
    private static final int BEAM_COLOR = 0xFF80D8FF; // Bluish-white
    private static final int BEAM_LIFETIME = 8; // ticks
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("lightning");
    }
    
    @Override
    public String getName() {
        return "Lightning";
    }
    
    @Override
    public String getDescription() {
        return "Fires a deadly beam from your wand tip";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        // Get block player is looking at (within range)
        HitResult hitResult = player.pick(RANGE, 1.0f, false);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos targetPos = blockHit.getBlockPos().above();
            
            if (level instanceof ServerLevel serverLevel) {
                // Start at wand tip
                Vec3 eye = player.getEyePosition();
                Vec3 look = player.getLookAngle().normalize();
                Vec3 wandTip = eye.add(look.scale(0.6)).add(0, -0.1, 0);
                
                Vec3 end = new Vec3(
                    targetPos.getX() + 0.5,
                    targetPos.getY() + 0.5,
                    targetPos.getZ() + 0.5
                );
                
                LightningBeamEntity beam = new LightningBeamEntity(
                    at.koopro.spells_n_squares.features.spell.SpellEntityRegistry.LIGHTNING_BEAM.get(),
                    level
                );
                serverLevel.addFreshEntity(beam);
                
                // Thunder sound at impact
                level.playSound(null, targetPos,
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.8f, 1.2f);
                
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.8f;
    }
}
