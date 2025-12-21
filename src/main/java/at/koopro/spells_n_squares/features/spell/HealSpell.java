package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Heal spell that restores player health.
 */
public class HealSpell implements Spell {
    
    private static final int COOLDOWN = 60; // 3 seconds
    private static final float HEAL_AMOUNT = 8.0f; // 4 hearts
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("heal");
    }
    
    @Override
    public String getName() {
        return "Heal";
    }
    
    @Override
    public String getDescription() {
        return "Restores 4 hearts of health";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (player.getHealth() >= player.getMaxHealth()) {
            return false; // Already at full health
        }
        
        player.heal(HEAL_AMOUNT);
        
        // Visual and audio feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.2f);
        
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1.0, player.getZ(),
                5, 0.5, 0.5, 0.5, 0.1);
        }
        
        // Screen effect for healing
        if (level.isClientSide()) {
            at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerOverlay(
                0x00FF00, 0.1f, 10,
                at.koopro.spells_n_squares.features.fx.ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
        
        return true;
    }
}
