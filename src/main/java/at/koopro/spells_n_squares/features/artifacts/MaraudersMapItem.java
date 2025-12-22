package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.features.artifacts.network.MaraudersMapPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Marauder's Map artifact that shows all players on the map.
 */
public class MaraudersMapItem extends Item {
    
    public MaraudersMapItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        MaraudersMapData.MaraudersMapComponent component = getMaraudersMapData(stack);
        component = component.toggle();
        stack.set(MaraudersMapData.MARAUDERS_MAP_DATA.get(), component);
        
        if (component.isActive()) {
            // Update tracked players
            List<ServerPlayer> allPlayers = serverLevel.getPlayers(p -> true);
            for (ServerPlayer trackedPlayer : allPlayers) {
                Vec3 pos = trackedPlayer.position();
                component = component.updatePlayer(
                    trackedPlayer.getUUID(),
                    trackedPlayer.getName().getString(),
                    pos.x, pos.y, pos.z,
                    serverLevel.getGameTime()
                );
            }
            stack.set(MaraudersMapData.MARAUDERS_MAP_DATA.get(), component);
            
            // Send network packet to open map screen on client
            var locationDataList = component.trackedPlayers().stream()
                .map(loc -> new MaraudersMapPayload.PlayerLocationData(
                    loc.playerId(),
                    loc.playerName(),
                    loc.x(),
                    loc.y(),
                    loc.z(),
                    loc.lastUpdateTick()
                ))
                .toList();
            
            var payload = new MaraudersMapPayload(locationDataList);
            PacketDistributor.sendToPlayer(serverPlayer, payload);
        } else {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.marauders_map.deactivated"));
        }
        
        // Visual and audio feedback
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        serverLevel.sendParticles(ParticleTypes.ENCHANT,
            pos.x, pos.y, pos.z,
            20, 0.5, 0.5, 0.5, 0.1);
        
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.7f, 1.0f);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets the Marauder's Map data component from an item stack.
     */
    public static MaraudersMapData.MaraudersMapComponent getMaraudersMapData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MaraudersMapItem)) {
            return new MaraudersMapData.MaraudersMapComponent();
        }
        
        MaraudersMapData.MaraudersMapComponent data = stack.get(MaraudersMapData.MARAUDERS_MAP_DATA.get());
        if (data == null) {
            data = new MaraudersMapData.MaraudersMapComponent();
            stack.set(MaraudersMapData.MARAUDERS_MAP_DATA.get(), data);
        }
        return data;
    }
}
