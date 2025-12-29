package at.koopro.spells_n_squares.features.communication;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.communication.block.NoticeBoardBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
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
            "two_way_mirror", id -> new TwoWayMirrorItem(RegistryHelper.createItemProperties(id)));
    
    // Communication blocks
    public static final DeferredHolder<Block, NoticeBoardBlock> NOTICE_BOARD = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "notice_board",
            id -> new NoticeBoardBlock(RegistryHelper.createBlockProperties(id).strength(1.5f)));
    
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
}







