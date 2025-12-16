package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.block.automation.MagicalFurnaceBlock;
import at.koopro.spells_n_squares.block.automation.SelfStirringCauldronBlock;
import at.koopro.spells_n_squares.block.building.MagicalLightBlock;
import at.koopro.spells_n_squares.block.resource.ItemCollectorBlock;
import at.koopro.spells_n_squares.block.resource.MagicalComposterBlock;
import at.koopro.spells_n_squares.block.resource.MagicalFarmBlock;
import at.koopro.spells_n_squares.block.resource.ResourceGeneratorBlock;
import at.koopro.spells_n_squares.block.communication.NoticeBoardBlock;
import at.koopro.spells_n_squares.block.storage.AutoSortChestBlock;
import at.koopro.spells_n_squares.block.storage.MagicalTrunkBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod blocks.
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, MagicalTrunkBlock> MAGICAL_TRUNK = BLOCKS.register(
            "magical_trunk", 
            id -> new MagicalTrunkBlock(createProperties(id).strength(2.5f)));
    
    public static final DeferredHolder<Block, AutoSortChestBlock> AUTO_SORT_CHEST = BLOCKS.register(
            "auto_sort_chest",
            id -> new AutoSortChestBlock(createProperties(id).strength(2.5f)));
    
    public static final DeferredHolder<Block, NoticeBoardBlock> NOTICE_BOARD = BLOCKS.register(
            "notice_board",
            id -> new NoticeBoardBlock(createProperties(id).strength(1.5f)));
    
    // Automation blocks
    public static final DeferredHolder<Block, SelfStirringCauldronBlock> SELF_STIRRING_CAULDRON = BLOCKS.register(
            "self_stirring_cauldron",
            id -> new SelfStirringCauldronBlock(createProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, MagicalFurnaceBlock> MAGICAL_FURNACE = BLOCKS.register(
            "magical_furnace",
            id -> new MagicalFurnaceBlock(createProperties(id).strength(3.5f)));
    
    // Building blocks
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_WHITE = BLOCKS.register(
            "magical_light_white",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.WHITE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_BLUE = BLOCKS.register(
            "magical_light_blue",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.BLUE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GREEN = BLOCKS.register(
            "magical_light_green",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GREEN));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_RED = BLOCKS.register(
            "magical_light_red",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.RED));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_PURPLE = BLOCKS.register(
            "magical_light_purple",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.PURPLE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GOLD = BLOCKS.register(
            "magical_light_gold",
            id -> new MagicalLightBlock(createProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GOLD));
    
    // Resource blocks
    public static final DeferredHolder<Block, MagicalFarmBlock> MAGICAL_FARM = BLOCKS.register(
            "magical_farm",
            id -> new MagicalFarmBlock(createProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ItemCollectorBlock> ITEM_COLLECTOR = BLOCKS.register(
            "item_collector",
            id -> new ItemCollectorBlock(createProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, MagicalComposterBlock> MAGICAL_COMPOSTER = BLOCKS.register(
            "magical_composter",
            id -> new MagicalComposterBlock(createProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ResourceGeneratorBlock> RESOURCE_GENERATOR = BLOCKS.register(
            "resource_generator",
            id -> new ResourceGeneratorBlock(createProperties(id).strength(3.0f)));
    
    private static BlockBehaviour.Properties createProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
    }
}
