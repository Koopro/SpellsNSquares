package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.economy.block.AutomatedShopBlock;
import at.koopro.spells_n_squares.features.economy.block.TradingPostBlock;
import at.koopro.spells_n_squares.features.economy.block.VaultBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for economy feature blocks.
 */
public class EconomyRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, TradingPostBlock> TRADING_POST = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "trading_post",
            id -> new TradingPostBlock(RegistryHelper.createBlockProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, AutomatedShopBlock> AUTOMATED_SHOP = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "automated_shop",
            id -> new AutomatedShopBlock(RegistryHelper.createBlockProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, VaultBlock> VAULT = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "vault",
            id -> new VaultBlock(RegistryHelper.createBlockProperties(id).strength(3.5f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







