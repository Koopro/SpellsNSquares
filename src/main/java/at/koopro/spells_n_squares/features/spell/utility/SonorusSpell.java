package at.koopro.spells_n_squares.features.spell.utility;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Sonorus - A spell that amplifies the caster's voice.
 */
public class SonorusSpell implements Spell {
    
    private static final int COOLDOWN = 20; // 1 second
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("sonorus");
    }
    
    @Override
    public String getName() {
        return "Sonorus";
    }
    
    @Override
    public String getDescription() {
        return "A spell that amplifies your voice";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        // Apply voice amplification effect (stored in player data)
        // For now, just play a sound effect and visual
        Vec3 pos = player.position();
        
        // Visual effect
        serverLevel.sendParticles(
            ParticleTypes.NOTE,
            pos.x, pos.y + player.getEyeHeight(), pos.z,
            30,
            0.5, 0.5, 0.5,
            0.1
        );
        
        // Audio feedback
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 2.0f, 1.0f);
        
        // TODO: Store amplification state in player data component
        // For now, just indicate success
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.sonorus.active"));
        }
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.3f;
    }
}
