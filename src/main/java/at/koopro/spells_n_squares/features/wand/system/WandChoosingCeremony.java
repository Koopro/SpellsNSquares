package at.koopro.spells_n_squares.features.wand.system;

import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.player.PlayerItemUtils;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Manages the wand choosing ceremony (Ollivanders-style).
 * Players can interact with a wand to "choose" it, which assigns ownership and provides loyalty bonuses.
 */
public final class WandChoosingCeremony {
    private WandChoosingCeremony() {
    }
    
    private static final Random RANDOM = new Random();
    
    /**
     * Performs the wand choosing ceremony for a player.
     * The wand "chooses" the player based on compatibility.
     * 
     * @param player The player choosing the wand
     * @param wand The wand item stack
     * @param level The level
     * @return true if the wand was successfully chosen
     */
    public static boolean performCeremony(Player player, ItemStack wand, Level level) {
        if (player == null || wand == null || wand.isEmpty() || level == null || level.isClientSide()) {
            return false;
        }
        
        if (!PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).map(wand::equals).orElse(false)) {
            return false; // Wand must be held
        }
        
        WandData.WandDataComponent currentData = WandDataHelper.getWandData(wand);
        if (currentData == null) {
            return false; // Invalid wand
        }
        
        // Check if wand already has an owner
        if (currentData.hasOwner() && !currentData.isOwner(player.getUUID())) {
            // Wand belongs to someone else - may still work but with reduced effectiveness
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("message.spells_n_squares.wand.ceremony.already_owned")
                );
            }
            return false;
        }
        
        // Perform compatibility check (simplified - in full implementation, could use player class, house, etc.)
        boolean compatible = checkCompatibility(player, currentData);
        
        if (compatible) {
            // Wand chooses the player!
            WandData.WandDataComponent newData = currentData.withOwner(player.getUUID());
            wand.set(WandData.WAND_DATA.get(), newData);
            
            // Visual effects
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1
                );
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.05
                );
            }
            
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable(
                        "message.spells_n_squares.wand.ceremony.chosen",
                        currentData.getWood().getId(),
                        currentData.getCore().getId()
                    )
                );
            }
            
            return true;
        } else {
            // Wand doesn't choose the player - may still work but less effectively
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("message.spells_n_squares.wand.ceremony.not_compatible")
                );
            }
            return false;
        }
    }
    
    /**
     * Checks compatibility between a player and a wand.
     * Uses a simplified algorithm - in a full implementation, this could consider:
     * - Player class
     * - Player house
     * - Player spell preferences
     * - Random chance
     * 
     * @param player The player
     * @param wandData The wand data
     * @return true if compatible
     */
    private static boolean checkCompatibility(Player player, WandData.WandDataComponent wandData) {
        // Simplified compatibility: 70% chance for first-time choosing
        // In a full implementation, this could be more sophisticated
        return RANDOM.nextDouble() < 0.7;
    }
    
    /**
     * Generates a random wand for a player during the choosing ceremony.
     * This simulates Ollivanders selecting wands for the player to try.
     * 
     * @param player The player
     * @return A new wand item stack with random core and wood
     */
    public static ItemStack generateRandomWand(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        
        // Get random core and wood
        WandCore[] cores = WandCore.values();
        WandWood[] woods = WandWood.values();
        
        WandCore core = cores[RANDOM.nextInt(cores.length)];
        WandWood wood = woods[RANDOM.nextInt(woods.length)];
        
        // Create wand item
        ItemStack wand = new ItemStack(at.koopro.spells_n_squares.features.wand.registry.WandRegistry.DEMO_WAND.get());
        WandData.WandDataComponent wandData = new WandData.WandDataComponent(
            core.getId(),
            wood.getId(),
            false, // Not attuned yet
            "" // No owner yet
        );
        wand.set(WandData.WAND_DATA.get(), wandData);
        
        return wand;
    }
}


