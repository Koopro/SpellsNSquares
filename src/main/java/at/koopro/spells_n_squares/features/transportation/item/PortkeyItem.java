package at.koopro.spells_n_squares.features.transportation.item;

import at.koopro.spells_n_squares.features.transportation.PortkeyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Portkey item that teleports players to a pre-set location.
 */
public class PortkeyItem extends Item {
    
    private static final int COOLDOWN_TICKS = 60; // 3 seconds
    private static final int SETUP_MODE_DURATION = 100; // 5 seconds to set location
    
    public PortkeyItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        PortkeyData.PortkeyDataComponent data = getPortkeyData(stack);
        
        // Check if portkey is set
        if (data == null || (data.x() == 0 && data.y() == 0 && data.z() == 0)) {
            // Set location mode
            setPortkeyLocation(stack, serverPlayer, serverLevel);
            serverPlayer.sendSystemMessage(Component.literal("Portkey location set! Right-click again to teleport."));
            return InteractionResult.SUCCESS;
        }
        
        // Check cooldown
        long currentTick = serverLevel.getGameTime();
        if (currentTick - data.lastUseTick() < COOLDOWN_TICKS) {
            serverPlayer.sendSystemMessage(Component.literal("Portkey is on cooldown"));
            return InteractionResult.FAIL;
        }
        
        // Teleport player
        teleportPlayer(serverPlayer, data, serverLevel);
        
        // Update last use time
        updateLastUse(stack, currentTick);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Sets the portkey location to the player's current position.
     */
    private void setPortkeyLocation(ItemStack stack, ServerPlayer player, ServerLevel level) {
        BlockPos pos = player.blockPosition();
        PortkeyData.PortkeyDataComponent data = new PortkeyData.PortkeyDataComponent(
            level.dimension(),
            pos.getX() + 0.5,
            pos.getY(),
            pos.getZ() + 0.5,
            0,
            true // Reusable by default
        );
        stack.set(PortkeyData.PORTKEY_DATA.get(), data);
    }
    
    /**
     * Teleports the player to the portkey destination.
     */
    private void teleportPlayer(ServerPlayer player, PortkeyData.PortkeyDataComponent data, ServerLevel level) {
        // Visual effect before teleport
        Vec3 pos = player.position();
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.1);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 20, 0.3, 0.3, 0.3, 0.05);
        
        // Teleport
        ServerLevel targetLevel = level.getServer().getLevel(data.dimension());
        if (targetLevel != null) {
            player.teleportTo(targetLevel, data.x(), data.y(), data.z(),
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
            
            // Visual effect after teleport
            level.sendParticles(ParticleTypes.PORTAL, data.x(), data.y(), data.z(), 30, 0.5, 0.5, 0.5, 0.1);
            level.sendParticles(ParticleTypes.END_ROD, data.x(), data.y(), data.z(), 20, 0.3, 0.3, 0.3, 0.05);
        }
    }
    
    /**
     * Gets or creates portkey data.
     */
    public static PortkeyData.PortkeyDataComponent getPortkeyData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PortkeyItem)) {
            return null;
        }
        
        PortkeyData.PortkeyDataComponent data = stack.get(PortkeyData.PORTKEY_DATA.get());
        if (data == null) {
            data = PortkeyData.PortkeyDataComponent.createUnset();
            stack.set(PortkeyData.PORTKEY_DATA.get(), data);
        }
        return data;
    }
    
    /**
     * Updates the last use time.
     */
    private void updateLastUse(ItemStack stack, long currentTick) {
        PortkeyData.PortkeyDataComponent data = getPortkeyData(stack);
        if (data != null) {
            PortkeyData.PortkeyDataComponent updated = new PortkeyData.PortkeyDataComponent(
                data.dimension(),
                data.x(), data.y(), data.z(),
                currentTick,
                data.reusable()
            );
            stack.set(PortkeyData.PORTKEY_DATA.get(), updated);
        }
    }
}

