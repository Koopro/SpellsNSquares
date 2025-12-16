package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.FXConfigHelper;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.features.robes.House;
import at.koopro.spells_n_squares.features.robes.HouseRobeBonusHandler;
import at.koopro.spells_n_squares.features.wand.WandCore;
import at.koopro.spells_n_squares.features.wand.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.WandVisualEffects;
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
            
            // House robe aura
            House houseSet = HouseRobeBonusHandler.getWornHouseSet(player);
            if (houseSet != null) {
                spawnHouseRobeAura(serverLevel, player, houseSet);
            }
        }
    }
    
    /**
     * Spawns aura particles around players wearing house robes.
     */
    private static void spawnHouseRobeAura(ServerLevel level, Player player, House house) {
        int auraCount = FXConfigHelper.calculateParticleCount(3);
        
        net.minecraft.core.particles.ParticleOptions particle = switch (house) {
            case GRYFFINDOR -> ParticleTypes.FLAME; // Red/gold
            case SLYTHERIN -> ParticleTypes.ELECTRIC_SPARK; // Green/silver
            case HUFFLEPUFF -> ParticleTypes.END_ROD; // Yellow/black
            case RAVENCLAW -> ParticleTypes.ENCHANT; // Blue/bronze
        };
        
        Vec3 pos = player.position().add(0, player.getBbHeight() / 2, 0);
        
        for (int i = 0; i < auraCount; i++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double radius = 0.4 + level.getRandom().nextDouble() * 0.3;
            double x = pos.x + Math.cos(angle) * radius;
            double y = pos.y + (level.getRandom().nextDouble() - 0.5) * 0.3;
            double z = pos.z + Math.sin(angle) * radius;
            
            Vec3 particlePos = new Vec3(x, y, z);
            if (FXConfigHelper.shouldRenderParticles(player, particlePos)) {
                level.sendParticles(
                    particle,
                    x, y, z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
                );
            }
        }
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
        
        level.sendParticles(
            particle,
            position.x, position.y, position.z,
            residueCount,
            0.2, 0.2, 0.2,
            0.01
        );
    }
}
