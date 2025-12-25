package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.fx.block.EnergyBallBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for FX feature blocks and items.
 */
public class FxRegistry {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, EnergyBallBlock> ENERGY_BALL = BLOCKS.register(
        "energy_ball",
        id -> new EnergyBallBlock(createBlockProperties(id).strength(0.5f).noOcclusion())
    );
    
    public static final DeferredItem<BlockItem> ENERGY_BALL_ITEM = ITEMS.register(
        "energy_ball",
        id -> new BlockItem(ENERGY_BALL.value(), createItemProperties(id))
    );
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        
        // Register BlockEntity types (must be after blocks are registered)
        at.koopro.spells_n_squares.features.fx.block.FxBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        at.koopro.spells_n_squares.features.fx.block.FxBlockEntities.initializeEnergyBallBlockEntity();
    }
    
    private static Item.Properties createItemProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    private static BlockBehaviour.Properties createBlockProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
    }
}

