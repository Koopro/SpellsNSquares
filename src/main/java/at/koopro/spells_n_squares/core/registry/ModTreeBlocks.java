package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.features.environment.block.TreeBlockSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for all tree blocks.
 * Stores all TreeBlockSets for easy access.
 */
public class ModTreeBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, at.koopro.spells_n_squares.SpellsNSquares.MODID);
    
    private static final List<TreeBlockSet> ALL_TREE_SETS = new ArrayList<>();
    
    /**
     * Gets all registered tree sets.
     * @return A list of all TreeBlockSets
     */
    public static List<TreeBlockSet> getAllTreeSets() {
        return new ArrayList<>(ALL_TREE_SETS);
    }
    
    /**
     * Registers a tree set. Called during initialization.
     * @param treeSet The tree set to register
     */
    public static void registerTreeSet(TreeBlockSet treeSet) {
        ALL_TREE_SETS.add(treeSet);
    }
}









