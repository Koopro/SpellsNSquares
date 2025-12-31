package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Sneakoscope artifact that detects nearby threats.
 */
public class SneakoscopeItem extends Item {
    
    private static final int DETECTION_RADIUS = 12;
    private static final double CONE_ANGLE = Math.toRadians(120);
    
    public SneakoscopeItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @EventBusSubscriber(modid = SpellsNSquares.MODID)
    public static class SneakoscopeHandler {
        
        private static final int CHECK_INTERVAL = 5;
        
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            if (!EventUtils.isServerSide(event)) {
                return;
            }
            
            Player player = event.getEntity();
            
            boolean hasSneakoscope = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof SneakoscopeItem) {
                    hasSneakoscope = true;
                    break;
                }
            }
            
            if (!hasSneakoscope) {
                return;
            }
            
            if (player.tickCount % CHECK_INTERVAL != 0) {
                return;
            }
            
            detectThreats(player);
        }
        
        private static void detectThreats(Player player) {
            AABB searchBox = new AABB(
                player.getX() - DETECTION_RADIUS, player.getY() - DETECTION_RADIUS, player.getZ() - DETECTION_RADIUS,
                player.getX() + DETECTION_RADIUS, player.getY() + DETECTION_RADIUS, player.getZ() + DETECTION_RADIUS
            );
            
            boolean threatFound = false;
            double closestDistance = Double.MAX_VALUE;
            
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, searchBox)) {
                if (entity == player || !entity.isInvisible() && !isHostile(entity)) {
                    continue;
                }
                
                double dx = entity.getX() - player.getX();
                double dz = entity.getZ() - player.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                
                if (distance > DETECTION_RADIUS) {
                    continue;
                }
                
                double angle = Math.atan2(dz, dx) - Math.toRadians(player.getYRot());
                if (Math.abs(angle) > CONE_ANGLE / 2) {
                    continue;
                }
                
                threatFound = true;
                closestDistance = Math.min(closestDistance, distance);
            }
            
            if (threatFound && player.level() instanceof ServerLevel serverLevel) {
                at.koopro.spells_n_squares.features.fx.SoundVisualSync.onArtifactActivated(
                    serverLevel, player, "sneakoscope");
                
                net.minecraft.world.phys.Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                    pos.x, pos.y, pos.z,
                    5, 0.3, 0.3, 0.3, 0.0
                );
            }
        }
        
        private static boolean isHostile(LivingEntity entity) {
            return entity.getLastHurtByMob() == null || 
                   !entity.getLastHurtByMob().equals(entity.level().getNearestPlayer(entity, 50));
        }
    }
}




















