package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Manages Pensieve memory extraction, viewing, and storage.
 * Integrates with PensieveItem and PensieveData.
 */
public final class PensieveMemorySystem {
    private PensieveMemorySystem() {
    }
    
    /**
     * Extracts a memory from a player's mind.
     * Creates a memory item that can be stored in a Pensieve.
     */
    public static boolean extractMemory(Player player, String description, String location) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        
        // TODO: Create MemoryItem that can be stored in Pensieve
        // For now, we'll add it directly to Pensieve if player is holding one
        
        ItemStack mainHand = serverPlayer.getMainHandItem();
        ItemStack offHand = serverPlayer.getOffhandItem();
        
        // Check if player is holding a Pensieve
        if (mainHand.getItem() instanceof PensieveItem) {
            PensieveData.PensieveComponent component = PensieveItem.getPensieveData(mainHand);
            component = component.addMemory(description, serverPlayer.level().getGameTime(), location);
            mainHand.set(PensieveData.PENSIEVE_DATA.get(), component);
            return true;
        } else if (offHand.getItem() instanceof PensieveItem) {
            PensieveData.PensieveComponent component = PensieveItem.getPensieveData(offHand);
            component = component.addMemory(description, serverPlayer.level().getGameTime(), location);
            offHand.set(PensieveData.PENSIEVE_DATA.get(), component);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets all memories from a Pensieve item.
     */
    public static List<PensieveData.MemorySnapshot> getMemories(ItemStack pensieveStack) {
        if (!(pensieveStack.getItem() instanceof PensieveItem)) {
            return List.of();
        }
        
        PensieveData.PensieveComponent component = PensieveItem.getPensieveData(pensieveStack);
        return component.memories();
    }
    
    /**
     * Clears all memories from a Pensieve.
     */
    public static void clearMemories(ItemStack pensieveStack) {
        if (pensieveStack.getItem() instanceof PensieveItem) {
            pensieveStack.set(PensieveData.PENSIEVE_DATA.get(), new PensieveData.PensieveComponent());
        }
    }
}











