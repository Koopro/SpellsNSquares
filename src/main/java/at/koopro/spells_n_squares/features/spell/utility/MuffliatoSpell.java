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
 * Muffliato - A spell that creates a buzzing noise to prevent eavesdropping.
 */
public class MuffliatoSpell implements Spell {
    
    private static final int COOLDOWN = 30; // 1.5 seconds
    private static final double RANGE = 8.0;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("muffliato");
    }
    
    @Override
    public String getName() {
        return "Muffliato";
    }
    
    @Override
    public String getDescription() {
        return "Creates a buzzing noise to prevent eavesdropping";
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
        
        Vec3 pos = player.position();
        
        // Create buzzing sound effect
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.BEE_LOOP, SoundSource.PLAYERS, 0.5f, 0.5f);
        
        // Visual effect - buzzing particles
        serverLevel.sendParticles(
            ParticleTypes.NOTE,
            pos.x, pos.y + player.getEyeHeight(), pos.z,
            20,
            1.0, 1.0, 1.0,
            0.05
        );
        
        // TODO: Store muffling area effect in level data
        // For now, just indicate success
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.muffliato.active"));
        }
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.2f;
    }
}
