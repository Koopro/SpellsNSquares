package at.koopro.spells_n_squares.features.ghosts;

import at.koopro.spells_n_squares.features.artifacts.SortingHatData;
import at.koopro.spells_n_squares.features.artifacts.SortingHatItem;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
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
            // Faster spell cooldown recovery (reduce cooldowns by 10% per tick)
            SpellManager.tickCooldowns(nearbyPlayer);
            
            // Apply regeneration effect (subtle health boost)
            nearbyPlayer.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                100, // 5 seconds
                0,   // Level 1
                false,
                false,
                true
            ));
        }
    }
    
    /**
     * Checks if a player is in the specified house.
     */
    private boolean isPlayerInHouse(ServerPlayer player, GhostData.HouseAssociation house) {
        // Check player's house assignment via SortingHatData
        // Note: This requires the Sorting Hat item to be present in the world
        // For a more robust solution, house data should be stored on the player entity
        // For now, we'll check if there's a Sorting Hat item nearby or use a fallback
        
        // Try to find house assignment from any Sorting Hat item in the world
        // This is a simplified check - ideally house data would be on the player
        if (house == GhostData.HouseAssociation.NONE) {
            return false;
        }
        
        // Convert GhostData.HouseAssociation to SortingHatData.House
        SortingHatData.House requiredHouse = switch (house) {
            case GRYFFINDOR -> SortingHatData.House.GRYFFINDOR;
            case SLYTHERIN -> SortingHatData.House.SLYTHERIN;
            case HUFFLEPUFF -> SortingHatData.House.HUFFLEPUFF;
            case RAVENCLAW -> SortingHatData.House.RAVENCLAW;
            case NONE -> null;
        };
        
        if (requiredHouse == null) {
            return false;
        }
        
        // Check if player has been sorted (simplified - would need proper player data storage)
        // For now, return true if house matches (this is a placeholder)
        // In a full implementation, this would check player's persistent house assignment
        return true; // Placeholder - assumes player is in the correct house
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            applyHouseBonuses(serverLevel);
        }
    }
}















