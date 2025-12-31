package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.features.magic.system.PatronusSystem;
import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Patronus Charm spell.
 * Summons a Patronus to protect against Dementors and send messages.
 */
public class PatronusSpell implements Spell {
    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "patronus");
    }
    
    @Override
    public String getName() {
        return "Patronus";
    }
    
    @Override
    public String getDescription() {
        return "Summons a Patronus to protect against Dementors and send messages";
    }
    
    @Override
    public int getCooldown() {
        return 100; // 5 seconds
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.9f; // High intensity
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        
        // Check if player has discovered their Patronus
        if (!PatronusSystem.hasPatronus(player)) {
            serverPlayer.sendSystemMessage(
                Component.translatable("message.spells_n_squares.patronus.not_discovered")
            );
            return false;
        }
        
        PatronusSystem.PatronusForm form = PatronusSystem.getPatronusForm(player);
        if (form == null) {
            return false;
        }
        
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 lookAngle = player.getLookAngle();
        
        // Spawn Patronus visual effect (bright particles)
        for (int i = 0; i < 50; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = (level.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2.0;
            
            serverLevel.sendParticles(
                ParticleTypes.END_ROD,
                pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                1, 0.0, 0.0, 0.0, 0.1
            );
        }
        
        // Spawn additional bright particles for Patronus effect
        serverLevel.sendParticles(
            ParticleTypes.TOTEM_OF_UNDYING,
            pos.x, pos.y, pos.z,
            30, 1.0, 1.0, 1.0, 0.05
        );
        
        // Play sound effect
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.5f);
        
        // Apply protection effect (resistance to negative effects)
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.RESISTANCE,
            200, // 10 seconds
            1,
            false,
            true,
            true
        ));
        
        // Send message
        serverPlayer.sendSystemMessage(
            Component.translatable("message.spells_n_squares.patronus.cast", form.form().getDisplayName())
        );
        
        return true;
    }
}

