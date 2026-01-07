package at.koopro.spells_n_squares.features.fx.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.fx.FXConfigHelper;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.event.EventUtils;
import at.koopro.spells_n_squares.core.util.player.PlayerItemUtils;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.visual.WandVisualEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles environmental effects (auras, ambient particles, world effects).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class EnvironmentalEffectHandler {
    
    // Aura particle spawn interval (every 10 ticks = 0.5 seconds)
    private static final int AURA_INTERVAL = 10;
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event) || !Config.areEnvironmentalEffectsEnabled()) {
            return;
        }
        
        Player player = event.getEntity();
        
        SafeEventHandler.execute(() -> {
            // Only spawn auras periodically
            if (player.tickCount % AURA_INTERVAL != 0) {
                return;
            }
            
            if (player.level() instanceof ServerLevel serverLevel) {
                // Wand aura
                ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
                if (!wand.isEmpty()) {
                    WandVisualEffects.spawnWandAura(serverLevel, player, wand);
                }
                
                // House robe aura removed
            }
        }, "spawning environmental effects", player);
    }
    
    /**
     * Spawns spell residue particles (lingering effects after spell casts).
     */
    public static void spawnSpellResidue(ServerLevel level, Vec3 position, 
                                        WandCore core, int duration) {
        if (!Config.areEnvironmentalEffectsEnabled()) {
            return;
        }
        
        // Spawn subtle particles that fade over time
        int residueCount = FXConfigHelper.calculateParticleCount(5);
        
        net.minecraft.core.particles.ParticleOptions particle = switch (core) {
            case PHOENIX_FEATHER -> ParticleTypes.END_ROD;
            case DRAGON_HEARTSTRING -> ParticleTypes.SMOKE;
            case UNICORN_HAIR -> ParticleTypes.ELECTRIC_SPARK;
        };
        
        ParticlePool.queueParticle(
            level,
            particle,
            position,
            residueCount,
            0.2, 0.2, 0.2,
            0.01
        );
    }
}
