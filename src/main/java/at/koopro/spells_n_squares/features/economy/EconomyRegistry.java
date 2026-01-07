package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.economy.block.EconomyBlockEntities;
import at.koopro.spells_n_squares.features.economy.block.VaultBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for economy feature items and blocks.
 */
public class EconomyRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, 
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, VaultBlock> VAULT = BLOCKS.register(
        "vault",
        id -> new VaultBlock(RegistryHelper.createBlockProperties(id).strength(3.0f).requiresCorrectToolForDrops())
    );
    
    public static final DeferredHolder<Item, BlockItem> VAULT_ITEM = ITEMS.register(
        "vault",
        id -> new BlockItem(VAULT.value(), RegistryHelper.createItemProperties(id))
    );
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        EconomyBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EconomyBlockEntities.initializeVaultBlockEntity();
    }
}

