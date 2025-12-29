package at.koopro.spells_n_squares.features.contracts;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

/**
 * Contract parchment item for creating and managing contracts.
 */
public class ContractItem extends Item {
    
    public ContractItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            ContractData.ContractComponent contractData = ContractSystem.getContractData(stack);
            if (contractData == null) {
                // Empty contract - open creation interface
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.contracts.client.ContractCreationScreen(stack));
            } else {
                // Contract with data - view it (could open a view screen too)
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        ContractData.ContractComponent contractData = ContractSystem.getContractData(stack);
        
        if (contractData == null) {
            // Empty contract - creation interface is handled client-side
            return InteractionResult.SUCCESS;
        } else {
            // Contract with data - view it
            viewContract(serverPlayer, contractData);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Views contract content.
     */
    private void viewContract(ServerPlayer player, ContractData.ContractComponent contract) {
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.title"));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.type", contract.contractType()));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.parties", 
            String.join(", ", contract.partyNames())));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.terms", contract.terms()));
        
        if (contract.isUnbreakableVow()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.unbreakable_vow"));
        }
        
        if (contract.isViolated()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.violated"));
        }
        
        if (!contract.conditions().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.contract.conditions"));
            for (ContractData.ContractCondition condition : contract.conditions()) {
                player.sendSystemMessage(Component.literal("  - " + condition.description()));
            }
        }
    }
    
    /**
     * Creates a contract on this item stack.
     * Called by command or GUI.
     */
    public static void createContract(ItemStack stack, String contractType, List<UUID> parties,
                                     List<String> partyNames, String terms,
                                     List<ContractData.ContractCondition> conditions,
                                     boolean isUnbreakableVow) {
        ContractData.ContractComponent contract = ContractSystem.createContract(
            contractType, parties, partyNames, terms, conditions, isUnbreakableVow);
        
        stack.set(ContractData.CONTRACT_DATA.get(), contract);
    }
    
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        ContractData.ContractComponent contractData = ContractSystem.getContractData(stack);
        if (contractData != null) {
            tooltip.add(Component.translatable("message.spells_n_squares.contract.type", contractData.contractType()));
            tooltip.add(Component.translatable("message.spells_n_squares.contract.parties", 
                String.join(", ", contractData.partyNames())));
            if (contractData.isUnbreakableVow()) {
                tooltip.add(Component.translatable("message.spells_n_squares.contract.unbreakable_vow"));
            }
            if (contractData.isViolated()) {
                tooltip.add(Component.translatable("message.spells_n_squares.contract.violated"));
            }
        } else {
            tooltip.add(Component.translatable("message.spells_n_squares.contract.empty"));
        }
    }
}















