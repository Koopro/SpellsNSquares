package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Teleport spell that moves the player forward 10 blocks.
 */
public class TeleportSpell implements Spell {
    
    private static final int COOLDOWN = 100; // 5 seconds
    private static final double TELEPORT_DISTANCE = 10.0;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("teleport");
    }
    
    @Override
    public String getName() {
        return "Teleport";
    }
    
    @Override
    public String getDescription() {
        return "Teleports you forward 10 blocks";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 currentPos = player.position();
        Vec3 targetPos = currentPos.add(lookVec.scale(TELEPORT_DISTANCE));
        
        // Check if target position is safe (not in a block)
        if (level.getBlockState(BlockPos.containing(targetPos)).isAir()) {
            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
            
            level.playSound(null, currentPos.x, currentPos.y, currentPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    currentPos.x, currentPos.y + 1.0, currentPos.z,
                    20, 0.5, 0.5, 0.5, 0.1);
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    targetPos.x, targetPos.y + 1.0, targetPos.z,
                    20, 0.5, 0.5, 0.5, 0.1);
            }
            
            return true;
        }
        
        return false; // Can't teleport into a block
    }
}
