package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlock;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockItem;
import at.koopro.spells_n_squares.features.storage.block.StorageBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for storage feature items and blocks (pocket dimensions and Newt's Case only).
 */
public class StorageRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = 
        DeferredRegister.create(Registries.CHUNK_GENERATOR, SpellsNSquares.MODID);
    
    // Storage items
    public static final DeferredItem<PocketDimensionItem> POCKET_DIMENSION = ITEMS.register(
            "pocket_dimension", id -> new PocketDimensionItem(RegistryHelper.createItemProperties(id)));
    
    // Newt's Case block and item
    public static final DeferredHolder<Block, NewtsCaseBlock> NEWTS_CASE = BLOCKS.register(
            "newts_case",
            id -> new NewtsCaseBlock(RegistryHelper.createBlockProperties(id).strength(2.5f).noOcclusion().isRedstoneConductor((state, level, pos) -> false)));

    public static final DeferredItem<NewtsCaseBlockItem> NEWTS_CASE_ITEM = ITEMS.register(
            "newts_case",
            id -> new NewtsCaseBlockItem(NEWTS_CASE.value(), RegistryHelper.createItemProperties(id)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        PocketDimensionData.DATA_COMPONENTS.register(modEventBus);
        
        // Register pocket dimension chunk generator
        CHUNK_GENERATORS.register("pocket_chunk_generator", () -> PocketChunkGenerator.CODEC);
        CHUNK_GENERATORS.register(modEventBus);
        
        // Register BlockEntity types (must be after blocks are registered)
        StorageBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        StorageBlockEntities.initializeNewtsCaseBlockEntity();
    }
}






