package at.koopro.spells_n_squares.features.fx.sync;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.SpellCastEvent;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Coordinates visual effects with audio events for synchronized feedback.
 * Note: This class is manually registered with AddonEventBus, not via @EventBusSubscriber.
 */
public class SoundVisualSync {
    
    /**
     * Initialize the handler by registering with AddonEventBus.
     */
    public static void initialize() {
        AddonEventBus.getInstance().register(new SoundVisualSync());
    }
    
    /**
     * Called when a spell is cast - synchronizes visual effects with sound.
     */
    @net.neoforged.bus.api.SubscribeEvent
    public void onSpellCast(SpellCastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Level level = event.getLevel();
        Spell spell = event.getSpell();
        
        SafeEventHandler.execute(() -> {
            // Suppress ALL effects for hold-to-cast spells to avoid annoying animations
            // Hold spells should only have subtle effects during onHoldTick, not on cast
            if (spell != null && spell.isHoldToCast()) {
                return;
            }
            
            // Trigger screen flash synchronized with cast
            if (level.isClientSide()) {
                ScreenEffectManager.triggerSpellFlash();
            }
            
            // Spawn particle burst on sound cue
            if (level instanceof ServerLevel serverLevel) {
                net.minecraft.world.phys.Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.ENCHANT,
                    pos,
                    5,
                    0.2, 0.2, 0.2,
                    0.05
                );
            }
        }, "synchronizing spell cast visual effects", player);
    }
    
    /**
     * Triggers visual effects when a sound is played.
     * Can be called from sound event handlers.
     */
    public static void onSoundPlayed(Level level, Player player, SoundEvent sound, 
                                    net.minecraft.world.phys.Vec3 position) {
        if (level.isClientSide()) {
            return;
        }
        
        // Match sounds to visual effects
        if (sound == ModSounds.LUMOS.value()) {
            // Lumos: bright particle burst
            if (level instanceof ServerLevel serverLevel) {
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.END_ROD,
                    position,
                    10,
                    0.3, 0.3, 0.3,
                    0.1
                );
            }
        } else if (sound == ModSounds.NOX.value()) {
            // Nox: dark particle burst
            if (level instanceof ServerLevel serverLevel) {
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.SMOKE,
                    position,
                    10,
                    0.3, 0.3, 0.3,
                    0.1
                );
            }
        }
    }
    
    /**
     * Triggers artifact activation visuals.
     */
    public static void onArtifactActivated(Level level, Player player, String artifactType) {
        if (level.isClientSide()) {
            return;
        }
        
        if (level instanceof ServerLevel serverLevel) {
            net.minecraft.world.phys.Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
            
            switch (artifactType) {
                case "time_turner":
                    // Time distortion effect
                    ShaderEffectHandler.triggerTimeDistortion();
                    ParticlePool.queueParticle(
                        serverLevel,
                        ParticleTypes.TOTEM_OF_UNDYING,
                        pos,
                        20,
                        0.5, 0.5, 0.5,
                        0.1
                    );
                    break;
                case "sneakoscope":
                    // Alert effect
                    ParticlePool.queueParticle(
                        serverLevel,
                        ParticleTypes.ELECTRIC_SPARK,
                        pos,
                        5,
                        0.2, 0.2, 0.2,
                        0.0
                    );
                    break;
            }
        }
    }
}
