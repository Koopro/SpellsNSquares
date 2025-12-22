package at.koopro.spells_n_squares.features.communication;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.communication.block.NoticeBoardBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for communication feature items, blocks, and entities.
 */
public class CommunicationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellsNSquares.MODID);
    
    // Communication items
    public static final DeferredItem<TwoWayMirrorItem> TWO_WAY_MIRROR = ITEMS.register(
            "two_way_mirror", id -> new TwoWayMirrorItem(createItemProperties(id)));
    
    // Communication blocks
    public static final DeferredHolder<Block, NoticeBoardBlock> NOTICE_BOARD = registerBlockWithItem(
            "notice_board",
            id -> new NoticeBoardBlock(createBlockProperties(id).strength(1.5f)));
    
    // Communication entities
    public static final DeferredHolder<EntityType<?>, EntityType<OwlEntity>> OWL = ENTITIES.register(
        "owl",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                ModIdentifierHelper.modId("owl"));
            return EntityType.Builder.<OwlEntity>of(OwlEntity::new, MobCategory.CREATURE)
                .sized(0.5f, 0.9f)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MirrorData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createItemProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    private static BlockBehaviour.Properties createBlockProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
    }
    
    /**
     * Registers a block and its corresponding BlockItem.
     * @param name The block name
     * @param blockSupplier The supplier that creates the block
     * @return The DeferredHolder for the registered block
     */
    private static <T extends Block> DeferredHolder<Block, T> registerBlockWithItem(
            String name,
            java.util.function.Function<Identifier, T> blockSupplier) {
        DeferredHolder<Block, T> block = BLOCKS.register(name, blockSupplier);
        
        // Register corresponding BlockItem
        ITEMS.register(name, id -> new BlockItem(
            block.value(),
            createItemProperties(id)
        ));
        
        return block;
    }
}



