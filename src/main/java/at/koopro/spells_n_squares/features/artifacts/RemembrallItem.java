package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Remembrall - glows red when player has forgotten something.
 */
public class RemembrallItem extends Item {
    
    public RemembrallItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            RemembrallData.RemembrallComponent component = getRemembrallData(stack);
            
            if (component.hasForgottenItems()) {
                // Show what was forgotten
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.remembrall.forgotten_items"));
                for (RemembrallData.ForgottenItem item : component.forgottenItems()) {
                    serverPlayer.sendSystemMessage(Component.literal("  - " + item.itemName()));
                }
                
                // Clear forgotten items after checking
                RemembrallData.RemembrallComponent cleared = component.clearForgottenItems();
                stack.set(RemembrallData.REMEMBRALL_DATA.get(), cleared);
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.remembrall.nothing_forgotten"));
            }
            
            // Visual effect
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
            }
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Checks if the remembrall should glow (has forgotten items).
     */
    public static boolean shouldGlow(ItemStack stack) {
        RemembrallData.RemembrallComponent component = getRemembrallData(stack);
        return component.hasForgottenItems();
    }
    
    /**
     * Adds a forgotten item to the remembrall.
     */
    public static void addForgottenItem(ItemStack stack, ItemStack forgottenStack, long currentTick) {
        RemembrallData.RemembrallComponent component = getRemembrallData(stack);
        
        Identifier itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(forgottenStack.getItem());
        String itemName = forgottenStack.getDisplayName().getString();
        
        RemembrallData.ForgottenItem forgottenItem = new RemembrallData.ForgottenItem(
            itemId,
            itemName,
            currentTick
        );
        
        RemembrallData.RemembrallComponent newComponent = component.withForgottenItem(forgottenItem);
        stack.set(RemembrallData.REMEMBRALL_DATA.get(), newComponent);
    }
    
    /**
     * Gets the remembrall data component from an item stack.
     */
    public static RemembrallData.RemembrallComponent getRemembrallData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof RemembrallItem)) {
            return new RemembrallData.RemembrallComponent();
        }
        
        RemembrallData.RemembrallComponent data = stack.get(RemembrallData.REMEMBRALL_DATA.get());
        if (data == null) {
            data = new RemembrallData.RemembrallComponent();
            stack.set(RemembrallData.REMEMBRALL_DATA.get(), data);
        }
        return data;
    }
}
