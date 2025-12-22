package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Record containing all block references for a complete tree/wood type.
 * This provides a convenient way to access all blocks for a specific wood type.
 */
public record TreeBlockSet(
    ModWoodType woodType,
    // Core wood blocks
    DeferredHolder<Block, ? extends Block> log,
    DeferredHolder<Block, ? extends Block> strippedLog,
    DeferredHolder<Block, ? extends Block> wood,
    DeferredHolder<Block, ? extends Block> strippedWood,
    DeferredHolder<Block, ? extends Block> planks,
    // Foliage
    DeferredHolder<Block, ? extends Block> leaves,
    DeferredHolder<Block, ? extends Block> sapling,
    // Decorative
    DeferredHolder<Block, ? extends Block> stairs,
    DeferredHolder<Block, ? extends Block> slab,
    DeferredHolder<Block, ? extends Block> fence,
    DeferredHolder<Block, ? extends Block> fenceGate,
    // Utility
    DeferredHolder<Block, ? extends Block> door,
    DeferredHolder<Block, ? extends Block> trapdoor,
    DeferredHolder<Block, ? extends Block> pressurePlate,
    DeferredHolder<Block, ? extends Block> button
) {
    /**
     * Gets the wood type ID.
     */
    public String getWoodId() {
        return woodType.getId();
    }
    
    /**
     * Gets all block holders in an array for iteration.
     */
    @SuppressWarnings("unchecked")
    public DeferredHolder<Block, ? extends Block>[] getAllBlocks() {
        return new DeferredHolder[] {
            log, strippedLog, wood, strippedWood, planks,
            leaves, sapling,
            stairs, slab, fence, fenceGate,
            door, trapdoor, pressurePlate, button
        };
    }
    
    /**
     * Gets all log-type blocks (log, stripped log, wood, stripped wood).
     */
    @SuppressWarnings("unchecked")
    public DeferredHolder<Block, ? extends Block>[] getLogBlocks() {
        return new DeferredHolder[] {
            log, strippedLog, wood, strippedWood
        };
    }
    
    /**
     * Gets all wooden blocks that can be crafted from planks.
     */
    @SuppressWarnings("unchecked")
    public DeferredHolder<Block, ? extends Block>[] getWoodenBlocks() {
        return new DeferredHolder[] {
            planks, stairs, slab, fence, fenceGate,
            door, trapdoor, pressurePlate, button
        };
    }
}








