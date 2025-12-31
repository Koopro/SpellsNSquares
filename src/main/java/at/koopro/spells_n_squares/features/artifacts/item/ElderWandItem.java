package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.features.artifacts.ElderWandData;
import at.koopro.spells_n_squares.features.wand.WandItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Elder Wand - The most powerful wand artifact.
 * Provides significant cooldown reduction and spell power bonuses.
 */
public class ElderWandItem extends WandItem {
    
    public ElderWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ElderWandData.ElderWandComponent component = getElderWandData(stack);
            
            // If no owner, set the current player as owner
            if (component.ownerId().isEmpty()) {
                component = component.withOwner(serverPlayer.getUUID(), serverPlayer.getName().getString());
                stack.set(ElderWandData.ELDER_WAND_DATA.get(), component);
                
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.elder_wand.claimed"));
            } else {
                // Check if owner changed (wand was won in combat)
                UUID currentOwnerId = component.ownerId().get();
                if (!currentOwnerId.equals(serverPlayer.getUUID())) {
                    // Transfer ownership (wand chooses the stronger wizard)
                    component = component.withOwner(serverPlayer.getUUID(), serverPlayer.getName().getString());
                    stack.set(ElderWandData.ELDER_WAND_DATA.get(), component);
                    
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.elder_wand.transferred"));
                } else {
                    // Increase mastery with use
                    component = component.increaseMastery();
                    stack.set(ElderWandData.ELDER_WAND_DATA.get(), component);
                }
            }
            
            // Visual effects
            if (level instanceof ServerLevel serverLevel) {
                Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    pos.x, pos.y, pos.z,
                    30, 0.5, 0.5, 0.5, 0.1
                );
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z,
                    20, 0.3, 0.3, 0.3, 0.05
                );
            }
        }
        
        return InteractionResult.SUCCESS;
    }
    
    // Note: Tooltip API varies with mappings; using helper method instead of override
    public void appendElderWandTooltip(ItemStack stack, java.util.List<Component> tooltip) {
        ElderWandData.ElderWandComponent component = getElderWandData(stack);
        
        if (component.ownerId().isPresent()) {
            tooltip.add(Component.translatable("tooltip.spells_n_squares.elder_wand.owner", component.ownerName()));
            tooltip.add(Component.translatable("tooltip.spells_n_squares.elder_wand.mastery", component.masteryLevel()));
        }
        
        tooltip.add(Component.translatable("tooltip.spells_n_squares.elder_wand.power"));
        tooltip.add(Component.translatable("tooltip.spells_n_squares.elder_wand.cooldown"));
    }
    
    /**
     * Gets the Elder Wand data component from an item stack.
     */
    public static ElderWandData.ElderWandComponent getElderWandData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ElderWandItem)) {
            return new ElderWandData.ElderWandComponent();
        }
        
        return at.koopro.spells_n_squares.core.data.DataComponentHelper.getOrCreateData(
            stack,
            ElderWandData.ELDER_WAND_DATA.get(),
            ElderWandData.ElderWandComponent::new
        );
    }
    
    /**
     * Checks if a player is holding the Elder Wand.
     */
    public static boolean isHoldingElderWand(Player player) {
        Item mainItem = player.getMainHandItem().getItem();
        Item offItem = player.getOffhandItem().getItem();
        return ElderWandItem.class.isAssignableFrom(mainItem.getClass()) ||
               ElderWandItem.class.isAssignableFrom(offItem.getClass());
    }
    
    /**
     * Gets the cooldown reduction multiplier for the Elder Wand.
     */
    public static float getCooldownReduction(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        Item mainItem = mainHand.getItem();
        Item offItem = offHand.getItem();
        if (ElderWandItem.class.isAssignableFrom(mainItem.getClass())) {
            return getElderWandData(mainHand).getCooldownReduction();
        }
        if (ElderWandItem.class.isAssignableFrom(offItem.getClass())) {
            return getElderWandData(offHand).getCooldownReduction();
        }
        
        return 1.0f; // No reduction
    }
    
    /**
     * Gets the spell power multiplier for the Elder Wand.
     */
    public static float getPowerMultiplier(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        Item mainItem = mainHand.getItem();
        Item offItem = offHand.getItem();
        if (ElderWandItem.class.isAssignableFrom(mainItem.getClass())) {
            return getElderWandData(mainHand).getPowerMultiplier();
        }
        if (ElderWandItem.class.isAssignableFrom(offItem.getClass())) {
            return getElderWandData(offHand).getPowerMultiplier();
        }
        
        return 1.0f; // No multiplier
    }
}
