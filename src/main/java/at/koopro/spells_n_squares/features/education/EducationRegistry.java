package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.education.block.HousePointsHourglassBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for education feature items and blocks.
 */
public class EducationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    // Education items
    public static final DeferredItem<BestiaryItem> BESTIARY = ITEMS.register(
            "bestiary", 
            id -> new BestiaryItem(RegistryHelper.createItemProperties(id)));
    
    // Education blocks
    public static final DeferredHolder<Block, HousePointsHourglassBlock> HOUSE_POINTS_HOURGLASS = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "house_points_hourglass",
            id -> new HousePointsHourglassBlock(RegistryHelper.createBlockProperties(id).strength(2.0f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







