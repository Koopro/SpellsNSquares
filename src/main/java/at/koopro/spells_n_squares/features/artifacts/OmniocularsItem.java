package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Omnioculars - sports viewing tool from Quidditch World Cup.
 * Allows viewing entities from a distance with zoom.
 */
public class OmniocularsItem extends Item {
    
    private static final int USE_DURATION = 72000; // Can be held indefinitely
    private static final double VIEW_RANGE = 64.0;
    
    public OmniocularsItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }
    
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Find nearby entities to view
            Vec3 playerPos = player.position();
            AABB searchArea = new AABB(
                playerPos.x - VIEW_RANGE, playerPos.y - VIEW_RANGE, playerPos.z - VIEW_RANGE,
                playerPos.x + VIEW_RANGE, playerPos.y + VIEW_RANGE, playerPos.z + VIEW_RANGE
            );
            
            var entities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
                entity -> entity != player && entity.isAlive());
            
            if (!entities.isEmpty()) {
                // Show info about nearest entity
                Entity nearest = null;
                double nearestDist = Double.MAX_VALUE;
                
                for (Entity entity : entities) {
                    double dist = player.distanceToSqr(entity);
                    if (dist < nearestDist) {
                        nearest = entity;
                        nearestDist = dist;
                    }
                }
                
                if (nearest != null && nearest instanceof LivingEntity livingEntity) {
                    // Show detailed entity statistics
                    double distance = Math.sqrt(nearestDist);
                    float health = livingEntity.getHealth();
                    float maxHealth = livingEntity.getMaxHealth();
                    String entityType = livingEntity.getType().getDescription().getString();
                    
                    serverPlayer.sendSystemMessage(Component.translatable(
                        "message.spells_n_squares.omnioculars.viewing",
                        nearest.getDisplayName(),
                        String.format("%.1f", distance)
                    ));
                    
                    serverPlayer.sendSystemMessage(Component.translatable(
                        "message.spells_n_squares.omnioculars.entity_type",
                        entityType
                    ));
                    
                    serverPlayer.sendSystemMessage(Component.translatable(
                        "message.spells_n_squares.omnioculars.health",
                        String.format("%.1f", health),
                        String.format("%.1f", maxHealth)
                    ));
                    
                    // Show position
                    serverPlayer.sendSystemMessage(Component.translatable(
                        "message.spells_n_squares.omnioculars.position",
                        String.format("%.0f", livingEntity.getX()),
                        String.format("%.0f", livingEntity.getY()),
                        String.format("%.0f", livingEntity.getZ())
                    ));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.translatable(
                    "message.spells_n_squares.omnioculars.no_entities"));
            }
            
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPYGLASS_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }
    
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player && !level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPYGLASS_STOP_USING, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return true;
    }
}







