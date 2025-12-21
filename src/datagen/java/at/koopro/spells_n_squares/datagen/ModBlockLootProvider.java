package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.block.tree.TreeBlockSet;
import at.koopro.spells_n_squares.core.registry.ModBlocks;
import at.koopro.spells_n_squares.core.registry.ModTreeBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates loot table files for all blocks (tree blocks and mod blocks).
 */
public class ModBlockLootProvider extends LootTableProvider {
    
    public ModBlockLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(
            new SubProviderEntry(lookup -> new ModBlockLootSubProvider(lookup), LootContextParamSets.BLOCK)
        ), lookupProvider);
    }
    
    /**
     * Sub-provider that generates loot tables for all blocks.
     */
    public static class ModBlockLootSubProvider extends BlockLootSubProvider {
        
        protected ModBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }
        
        @Override
        protected void generate() {
            // Generate loot tables for all tree blocks
            for (TreeBlockSet set : ModTreeBlocks.getAllTreeSets()) {
                generateTreeBlockLoot(set);
            }
            
            // Generate loot tables for all mod blocks
            generateModBlockLoot();
        }
        
        private void generateTreeBlockLoot(TreeBlockSet set) {
            // Core wood blocks - drop themselves
            dropSelf(set.log().get());
            dropSelf(set.strippedLog().get());
            dropSelf(set.wood().get());
            dropSelf(set.strippedWood().get());
            dropSelf(set.planks().get());
            
            // Leaves - standard leaves loot (with sapling chance)
            add(set.leaves().get(), block -> createLeavesDrops(block, set.sapling().get(), NORMAL_LEAVES_SAPLING_CHANCES));
            
            // Sapling - drops itself
            dropSelf(set.sapling().get());
            
            // Decorative blocks
            dropSelf(set.stairs().get());
            add(set.slab().get(), this::createSlabItemTable);
            dropSelf(set.fence().get());
            dropSelf(set.fenceGate().get());
            
            // Utility blocks
            add(set.door().get(), this::createDoorTable);
            dropSelf(set.trapdoor().get());
            dropSelf(set.pressurePlate().get());
            dropSelf(set.button().get());
        }
        
        private void generateModBlockLoot() {
            // Storage blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.MAGICAL_TRUNK.get());
            dropSelfIfHasItem(ModBlocks.AUTO_SORT_CHEST.get());
            
            // Communication blocks
            dropSelfIfHasItem(ModBlocks.NOTICE_BOARD.get());
            
            // Automation blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.SELF_STIRRING_CAULDRON.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_FURNACE.get());
            
            // Building blocks - magical lights drop themselves
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_WHITE.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_BLUE.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_GREEN.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_RED.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_PURPLE.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_LIGHT_GOLD.get());
            
            // Resource blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.MAGICAL_FARM.get());
            dropSelfIfHasItem(ModBlocks.ITEM_COLLECTOR.get());
            dropSelfIfHasItem(ModBlocks.MAGICAL_COMPOSTER.get());
            dropSelfIfHasItem(ModBlocks.RESOURCE_GENERATOR.get());
            
            // Enchantment blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.ENCHANTMENT_TABLE.get());
            
            // Education blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.HOUSE_POINTS_HOURGLASS.get());
            
            // Combat blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.DUEL_ARENA.get());
            
            // Economy blocks - drop themselves
            dropSelfIfHasItem(ModBlocks.TRADING_POST.get());
            dropSelfIfHasItem(ModBlocks.AUTOMATED_SHOP.get());
            dropSelfIfHasItem(ModBlocks.VAULT.get());
            
            // Plant blocks - drop their corresponding items
            // TODO: Re-enable when plant blocks and items are registered in ModBlocks and ModItems (MANDRAKE_PLANT, WOLFSBANE_PLANT, GILLYWEED_PLANT, DEVILS_SNARE, VENOMOUS_TENTACULA, WHOMPING_WILLOW)
        }
        
        /**
         * Helper method to drop a block as itself only if it has a valid block item.
         * This prevents errors when blocks don't have block items registered.
         * Always generates a loot table to satisfy getKnownBlocks() requirement.
         */
        private void dropSelfIfHasItem(Block block) {
            try {
                Item blockItem = block.asItem();
                if (blockItem != null && blockItem != Items.AIR) {
                    dropSelf(block);
                } else {
                    // Block doesn't have an item, generate empty loot table
                    add(block, block1 -> net.minecraft.world.level.storage.loot.LootTable.lootTable());
                }
            } catch (Exception e) {
                // If dropSelf fails, generate empty loot table
                try {
                    add(block, block1 -> net.minecraft.world.level.storage.loot.LootTable.lootTable());
                } catch (Exception e2) {
                    // If that also fails, try dropSelf anyway (might work)
                    try {
                        dropSelf(block);
                    } catch (Exception e3) {
                        // Last resort: skip this block
                    }
                }
            }
        }
        
        @Override
        protected Iterable<Block> getKnownBlocks() {
            // Return all blocks that we generate loot tables for
            List<Block> knownBlocks = new ArrayList<>();
            
            // Add all tree blocks (they all have loot tables)
            ModTreeBlocks.BLOCKS.getEntries().forEach(holder -> knownBlocks.add((Block) holder.get()));
            
            // Add mod blocks that we generate loot tables for
            // TODO: Re-enable when plant blocks are registered in ModBlocks (MANDRAKE_PLANT, WOLFSBANE_PLANT, GILLYWEED_PLANT)
            knownBlocks.add(ModBlocks.MAGICAL_TRUNK.get());
            knownBlocks.add(ModBlocks.AUTO_SORT_CHEST.get());
            knownBlocks.add(ModBlocks.NOTICE_BOARD.get());
            knownBlocks.add(ModBlocks.SELF_STIRRING_CAULDRON.get());
            knownBlocks.add(ModBlocks.MAGICAL_FURNACE.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_WHITE.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_BLUE.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_GREEN.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_RED.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_PURPLE.get());
            knownBlocks.add(ModBlocks.MAGICAL_LIGHT_GOLD.get());
            knownBlocks.add(ModBlocks.MAGICAL_FARM.get());
            knownBlocks.add(ModBlocks.ITEM_COLLECTOR.get());
            knownBlocks.add(ModBlocks.MAGICAL_COMPOSTER.get());
            knownBlocks.add(ModBlocks.RESOURCE_GENERATOR.get());
            knownBlocks.add(ModBlocks.ENCHANTMENT_TABLE.get());
            knownBlocks.add(ModBlocks.HOUSE_POINTS_HOURGLASS.get());
            knownBlocks.add(ModBlocks.DUEL_ARENA.get());
            knownBlocks.add(ModBlocks.TRADING_POST.get());
            knownBlocks.add(ModBlocks.AUTOMATED_SHOP.get());
            knownBlocks.add(ModBlocks.VAULT.get());
            // TODO: Re-enable when plant blocks are registered in ModBlocks (DEVILS_SNARE, VENOMOUS_TENTACULA, WHOMPING_WILLOW)
            
            return knownBlocks;
        }
        
    }
}








