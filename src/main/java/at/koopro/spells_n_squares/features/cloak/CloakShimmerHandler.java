package at.koopro.spells_n_squares.features.cloak;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles shimmer visual effects for invisibility cloaks.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class CloakShimmerHandler {
    
    // Spawn shimmer particles every 5 ticks when moving
    private static final int SHIMMER_INTERVAL = 5;
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        
        boolean hasCloak = chestArmor.getItem() == CloakRegistry.DEMIGUISE_CLOAK.get() ||
                          chestArmor.getItem() == CloakRegistry.DEATHLY_HALLOW_CLOAK.get();
        
        if (!hasCloak || !player.isInvisible()) {
            return;
        }
        
        // Only spawn shimmer when moving
        if (player.getDeltaMovement().lengthSqr() < 0.01) {
            return;
        }
        
        // Spawn shimmer particles periodically
        if (player.tickCount % SHIMMER_INTERVAL == 0 && player.level() instanceof ServerLevel serverLevel) {
            // Spawn outline particles around the player
            double x = player.getX();
            double y = player.getY() + player.getBbHeight() / 2;
            double z = player.getZ();
            
            // Spawn particles in a ring around the player
            for (int i = 0; i < 8; i++) {
                double angle = (i / 8.0) * Math.PI * 2;
                double radius = player.getBbWidth() / 2 + 0.2;
                double px = x + Math.cos(angle) * radius;
                double pz = z + Math.sin(angle) * radius;
                
                serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    px, y, pz,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
                );
            }
        }
    }
}

