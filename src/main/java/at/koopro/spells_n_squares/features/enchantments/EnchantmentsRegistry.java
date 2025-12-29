package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentTableBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for enchantments feature blocks.
 */
public class EnchantmentsRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, EnchantmentTableBlock> ENCHANTMENT_TABLE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "enchantment_table",
            id -> new EnchantmentTableBlock(RegistryHelper.createBlockProperties(id).strength(5.0f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







