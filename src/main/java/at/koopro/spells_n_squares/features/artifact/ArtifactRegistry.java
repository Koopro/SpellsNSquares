package at.koopro.spells_n_squares.features.artifact;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.artifact.block.ElixirCauldronBlock;
import at.koopro.spells_n_squares.features.artifact.fluid.ElixirFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for artifact items.
 */
public class ArtifactRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SpellsNSquares.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    // Prima Materia - Stage 1 of Magnum Opus
    public static final DeferredItem<Item> PRIMA_MATERIA = ITEMS.register(
        "prima_materia",
        id -> new Item(RegistryHelper.createItemProperties(id).stacksTo(1))
    );
    
    // White Stone - Stage 2 of Magnum Opus
    public static final DeferredItem<Item> WHITE_STONE = ITEMS.register(
        "white_stone",
        id -> new Item(RegistryHelper.createItemProperties(id).stacksTo(1)) {
            @Override
            public boolean isFoil(ItemStack stack) {
                return true; // Makes it glow faintly
            }
        }
    );
    
    // Philosopher's Stone - Final artifact
    public static final DeferredItem<PhilosophersStoneItem> PHILOSOPHERS_STONE = ITEMS.register(
        "philosophers_stone",
        id -> new PhilosophersStoneItem(RegistryHelper.createItemProperties(id)
            .stacksTo(1)
            .rarity(Rarity.EPIC)
            .fireResistant()) // Make it unburnable like netherite
    );
    
    // Elixir of Life - Consumable potion-like item (kept for backwards compatibility, but will be replaced by fluid)
    public static final DeferredItem<ElixirOfLifeItem> ELIXIR_OF_LIFE = ITEMS.register(
        "elixir_of_life",
        id -> new ElixirOfLifeItem(RegistryHelper.createItemProperties(id)
            .stacksTo(16)
            .rarity(Rarity.EPIC))
    );
    
    // Elixir Base Cauldron - Can hold Elixir Base fluid
    public static final DeferredHolder<Block, ElixirCauldronBlock> ELIXIR_BASE_CAULDRON = BLOCKS.register(
        "elixir_base_cauldron",
        id -> new ElixirCauldronBlock(
            RegistryHelper.createBlockProperties(id).strength(2.0f),
            ElixirCauldronBlock.ElixirType.BASE
        )
    );
    
    // Elixir of Life Cauldron - Can hold Elixir of Life fluid
    public static final DeferredHolder<Block, ElixirCauldronBlock> ELIXIR_OF_LIFE_CAULDRON = BLOCKS.register(
        "elixir_of_life_cauldron",
        id -> new ElixirCauldronBlock(
            RegistryHelper.createBlockProperties(id).strength(2.0f),
            ElixirCauldronBlock.ElixirType.LIFE
        )
    );
    
    // Block items for the cauldrons
    public static final DeferredItem<BlockItem> ELIXIR_BASE_CAULDRON_ITEM = ITEMS.register(
        "elixir_base_cauldron",
        id -> new BlockItem(ELIXIR_BASE_CAULDRON.value(), RegistryHelper.createItemProperties(id))
    );
    
    public static final DeferredItem<BlockItem> ELIXIR_OF_LIFE_CAULDRON_ITEM = ITEMS.register(
        "elixir_of_life_cauldron",
        id -> new BlockItem(ELIXIR_OF_LIFE_CAULDRON.value(), RegistryHelper.createItemProperties(id))
    );
    
    // Immortality MobEffect
    public static final DeferredHolder<MobEffect, ImmortalityEffect> IMMORTALITY_EFFECT = MOB_EFFECTS.register(
        "immortality",
        ImmortalityEffect::new
    );
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        ITEMS.register(modEventBus);
        ElixirFluids.register(modEventBus); // Register fluids, blocks, and buckets
        PhilosophersStoneData.DATA_COMPONENTS.register(modEventBus);
        // ImmortalityData.DATA_COMPONENTS.register(modEventBus); // No longer needed - using MobEffect
    }
}

