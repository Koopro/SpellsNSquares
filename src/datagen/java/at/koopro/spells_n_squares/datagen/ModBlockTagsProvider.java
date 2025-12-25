package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.environment.block.TreeBlockSet;
import at.koopro.spells_n_squares.core.registry.ModTreeBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * Generates block tag files for all tree blocks.
 * Uses IntrinsicHolderTagsProvider which is the base class in NeoForge 21.11.
 */
public class ModBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
    
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BLOCK, lookupProvider, 
            block -> block.builtInRegistryHolder().key(),
            SpellsNSquares.MODID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Register all tree block tags
        for (TreeBlockSet set : ModTreeBlocks.getAllTreeSets()) {
            registerTreeBlockTags(set);
        }
    }
    
    private void registerTreeBlockTags(TreeBlockSet set) {
        Block log = set.log().get();
        Block strippedLog = set.strippedLog().get();
        Block wood = set.wood().get();
        Block strippedWood = set.strippedWood().get();
        Block planks = set.planks().get();
        Block leaves = set.leaves().get();
        Block sapling = set.sapling().get();
        Block stairs = set.stairs().get();
        Block slab = set.slab().get();
        Block fence = set.fence().get();
        Block fenceGate = set.fenceGate().get();
        Block door = set.door().get();
        Block trapdoor = set.trapdoor().get();
        Block pressurePlate = set.pressurePlate().get();
        Block button = set.button().get();
        
        // Logs tags
        tag(BlockTags.LOGS).add(log).add(strippedLog).add(wood).add(strippedWood);
        tag(BlockTags.LOGS_THAT_BURN).add(log).add(strippedLog).add(wood).add(strippedWood);
        
        // Other tags
        tag(BlockTags.PLANKS).add(planks);
        tag(BlockTags.LEAVES).add(leaves);
        tag(BlockTags.SAPLINGS).add(sapling);
        
        // Stairs and slabs
        tag(BlockTags.WOODEN_STAIRS).add(stairs);
        tag(BlockTags.STAIRS).add(stairs);
        tag(BlockTags.WOODEN_SLABS).add(slab);
        tag(BlockTags.SLABS).add(slab);
        
        // Fences
        tag(BlockTags.WOODEN_FENCES).add(fence);
        tag(BlockTags.FENCES).add(fence);
        tag(BlockTags.FENCE_GATES).add(fenceGate);
        
        // Doors and trapdoors
        tag(BlockTags.WOODEN_DOORS).add(door);
        tag(BlockTags.DOORS).add(door);
        tag(BlockTags.WOODEN_TRAPDOORS).add(trapdoor);
        tag(BlockTags.TRAPDOORS).add(trapdoor);
        
        // Pressure plates and buttons
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(pressurePlate);
        tag(BlockTags.PRESSURE_PLATES).add(pressurePlate);
        tag(BlockTags.WOODEN_BUTTONS).add(button);
        tag(BlockTags.BUTTONS).add(button);
        
        // Mineable tags
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(log).add(strippedLog).add(wood).add(strippedWood)
            .add(planks).add(stairs).add(slab)
            .add(fence).add(fenceGate).add(door).add(trapdoor)
            .add(pressurePlate).add(button);
        
        tag(BlockTags.MINEABLE_WITH_HOE).add(leaves);
    }
}
















