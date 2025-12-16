package at.koopro.spells_n_squares.features.artifacts;

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

import java.util.Random;
import java.util.UUID;

/**
 * Sorting Hat - assigns players to Hogwarts houses.
 */
public class SortingHatItem extends Item {
    
    public SortingHatItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            SortingHatData.SortingHatComponent component = getSortingHatData(stack);
            UUID playerId = serverPlayer.getUUID();
            
            // Check if already sorted
            if (component.isSorted(playerId)) {
                SortingHatData.House house = component.getHouse(playerId).orElse(SortingHatData.House.GRYFFINDOR);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.sorting_hat.already_sorted", house.getName()));
                return InteractionResult.SUCCESS;
            }
            
            // Sort into a house (random selection)
            SortingHatData.House house = sortIntoHouse(serverPlayer);
            SortingHatData.SortingHatComponent newComponent = component.assignHouse(playerId, house);
            stack.set(SortingHatData.SORTING_HAT_DATA.get(), newComponent);
            
            // Announce the sorting
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.sorting_hat.sorted", house.getName()));
            
            // Visual effects
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    50, 0.5, 0.5, 0.5, 0.1);
                
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
            }
        }
        
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
     */
    public static SortingHatData.SortingHatComponent getSortingHatData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof SortingHatItem)) {
            return new SortingHatData.SortingHatComponent();
        }
        
        SortingHatData.SortingHatComponent data = stack.get(SortingHatData.SORTING_HAT_DATA.get());
        if (data == null) {
            data = new SortingHatData.SortingHatComponent();
            stack.set(SortingHatData.SORTING_HAT_DATA.get(), data);
        }
        return data;
    }
}
