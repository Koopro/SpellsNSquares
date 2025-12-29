package at.koopro.spells_n_squares.features.navigation;

import at.koopro.spells_n_squares.features.convenience.WaypointSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Location compass item for waypoint navigation.
 */
public class LocationCompassItem extends Item {
    
    public LocationCompassItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        List<WaypointSystem.Waypoint> waypoints = WaypointSystem.getWaypoints(player.getUUID());
        
        if (waypoints.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.compass.no_waypoints"));
        } else {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.compass.waypoints"));
            for (WaypointSystem.Waypoint waypoint : waypoints) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.compass.waypoint_entry", 
                    waypoint.name(), waypoint.position().getX(), waypoint.position().getY(), waypoint.position().getZ()));
            }
        }
        
        return InteractionResult.SUCCESS;
    }
}




















