package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Handles death prevention for players holding the Time-Turner.
 * When a player with a Time-Turner takes fatal damage, they are rewound to their temporal anchor.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class TimeTurnerDeathHandler {
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (player.level().isClientSide()) {
            return;
        }
        
        if (!(player.level() instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        // Check if player has Time-Turner in main hand or offhand
        ItemStack timeTurnerStack = PlayerItemUtils.findHeldItem(player, ArtifactsRegistry.TIME_TURNER.get())
            .orElse(ItemStack.EMPTY);
        
        if (timeTurnerStack.isEmpty()) {
            return;
        }
        
        // Check cooldown
        long currentTick = serverLevel.getGameTime();
        if (TimeTurnerItem.isOnDeathPreventionCooldown(timeTurnerStack, currentTick)) {
            return;
        }
        
        // Prevent death from void (falling out of world)
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        
        // Cancel death
        event.setCanceled(true);
        
        // Heal player to 2 hearts (4.0 health)
        player.setHealth(4.0f);
        
        // Clear all effects
        player.removeAllEffects();
        
        // Get anchor data
        TimeTurnerItem.TimeTurnerData data = TimeTurnerItem.getTimeTurnerData(timeTurnerStack);
        
        // Teleport to anchor if set, otherwise world spawn
        if (data.hasAnchor()) {
            // Try to teleport to anchor dimension
            ServerLevel targetLevel = serverLevel;
            if (!data.anchorDimension().isEmpty()) {
                try {
                    var dimensionKey = net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        net.minecraft.resources.Identifier.parse(data.anchorDimension())
                    );
                    targetLevel = serverLevel.getServer().getLevel(dimensionKey);
                    if (targetLevel == null) {
                        targetLevel = serverLevel; // Fallback to current level
                    }
                } catch (Exception e) {
                    targetLevel = serverLevel; // Fallback to current level
                }
            }
            
            player.teleportTo(targetLevel, data.anchorX(), data.anchorY(), data.anchorZ(),
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
            
            serverPlayer.displayClientMessage(Component.literal("§eThe timeline rewinds..."), true);
        } else {
            // No anchor set, teleport to world spawn (default: 0, 64, 0)
            ServerLevel overworld = serverLevel.getServer().overworld();
            net.minecraft.core.BlockPos defaultSpawn = new net.minecraft.core.BlockPos(0, 64, 0);
            // Try to find a safe spawn position
            net.minecraft.core.BlockPos safeSpawn = overworld.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, defaultSpawn);
            player.teleportTo(overworld, safeSpawn.getX() + 0.5, safeSpawn.getY(), safeSpawn.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
            
            serverPlayer.displayClientMessage(Component.literal("§cTime Turner activated! No anchor set - teleported to spawn."), false);
        }
        
        // Apply penalties: cooldown and durability damage
        TimeTurnerItem.applyDeathPrevention(timeTurnerStack, player, currentTick);
    }
}

