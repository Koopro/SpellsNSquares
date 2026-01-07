package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentBlockEntities;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentTableBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for enchantment feature items and blocks.
 */
public class EnchantmentRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, 
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, EnchantmentTableBlock> ENCHANTMENT_TABLE = BLOCKS.register(
        "enchantment_table",
        id -> new EnchantmentTableBlock(RegistryHelper.createBlockProperties(id).strength(5.0f).requiresCorrectToolForDrops())
    );
    
    public static final DeferredHolder<Item, BlockItem> ENCHANTMENT_TABLE_ITEM = ITEMS.register(
        "enchantment_table",
        id -> new BlockItem(ENCHANTMENT_TABLE.value(), RegistryHelper.createItemProperties(id))
    );
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        EnchantmentBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EnchantmentBlockEntities.initializeEnchantmentTableBlockEntity();
    }
}

