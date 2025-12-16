package at.koopro.spells_n_squares.features.ghosts;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * House-specific ghost entity (e.g., Nearly Headless Nick for Gryffindor).
 * House ghosts provide house-specific bonuses when nearby.
 */
public class HouseGhostEntity extends GhostEntity {
    
    public HouseGhostEntity(EntityType<? extends HouseGhostEntity> type, Level level) {
        super(type, level);
    }
    
    /**
     * Gets the house association for this ghost.
     */
    public GhostData.HouseAssociation getHouseAssociation() {
        GhostData.GhostComponent data = getGhostData();
        if (data != null) {
            return data.houseAssociation();
        }
        return GhostData.HouseAssociation.NONE;
    }
    
    /**
     * Applies house-specific bonuses to nearby players.
     * Should be called periodically (e.g., on entity tick).
     */
    public void applyHouseBonuses(ServerLevel level) {
        if (level.isClientSide()) {
            return;
        }
        
        GhostData.GhostComponent data = getGhostData();
        if (data == null || data.houseAssociation() == GhostData.HouseAssociation.NONE) {
            return;
        }
        
        // Find nearby players of the same house
        List<ServerPlayer> nearbyPlayers = level.getPlayers(
            player -> player.distanceTo(this) <= 16.0 && 
                     isPlayerInHouse(player, data.houseAssociation())
        );
        
        for (ServerPlayer nearbyPlayer : nearbyPlayers) {
            // Apply house-specific bonuses
            // TODO: Integrate with house system for bonuses
            // For example: faster spell cooldowns, increased house points, etc.
        }
    }
    
    /**
     * Checks if a player is in the specified house.
     */
    private boolean isPlayerInHouse(ServerPlayer player, GhostData.HouseAssociation house) {
        // TODO: Check player's house assignment
        // This would integrate with the house system
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            applyHouseBonuses(serverLevel);
        }
    }
}



