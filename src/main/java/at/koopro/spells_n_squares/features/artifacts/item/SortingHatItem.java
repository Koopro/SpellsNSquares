package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.features.artifacts.data.SortingHatData;
import at.koopro.spells_n_squares.features.artifacts.base.BaseArtifactItem;
import at.koopro.spells_n_squares.features.artifacts.util.ArtifactDataHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.UUID;

/**
 * Sorting Hat - assigns players to Hogwarts houses.
 */
public class SortingHatItem extends BaseArtifactItem {
    
    public SortingHatItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onArtifactUse(Level level, ServerPlayer player, InteractionHand hand, ItemStack stack) {
        SortingHatData.SortingHatComponent component = ArtifactDataHelper.getSortingHatData(stack);
        UUID playerId = player.getUUID();
        
        // Check if already sorted
        if (component.isSorted(playerId)) {
            SortingHatData.House house = component.getHouse(playerId).orElse(SortingHatData.House.GRYFFINDOR);
            sendMessage(player, "message.spells_n_squares.sorting_hat.already_sorted", house.getName());
            return InteractionResult.SUCCESS;
        }
        
        // Sort into a house (random selection)
        SortingHatData.House house = sortIntoHouse(player);
        SortingHatData.SortingHatComponent newComponent = component.assignHouse(playerId, house);
        ItemDataHelper.setData(stack, SortingHatData.SORTING_HAT_DATA.get(), newComponent);
        
        // Announce the sorting
        sendMessage(player, "message.spells_n_squares.sorting_hat.sorted", house.getName());
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Sorts a player into a house (simplified random selection).
     */
    private SortingHatData.House sortIntoHouse(ServerPlayer player) {
        Random random = new Random(player.getUUID().getMostSignificantBits());
        SortingHatData.House[] houses = SortingHatData.House.values();
        return houses[random.nextInt(houses.length)];
    }
    
    /**
     * Gets the player's assigned house.
     */
    public static java.util.Optional<SortingHatData.House> getPlayerHouse(ItemStack stack, Player player) {
        SortingHatData.SortingHatComponent component = getSortingHatData(stack);
        return component.getHouse(player.getUUID());
    }
    
    /**
     * Gets the sorting hat data component from an item stack.
     * @deprecated Use {@link ArtifactDataHelper#getSortingHatData(ItemStack)} instead
     */
    @Deprecated(forRemoval = false) // Keep for backward compatibility
    public static SortingHatData.SortingHatComponent getSortingHatData(ItemStack stack) {
        return ArtifactDataHelper.getSortingHatData(stack);
    }
}
