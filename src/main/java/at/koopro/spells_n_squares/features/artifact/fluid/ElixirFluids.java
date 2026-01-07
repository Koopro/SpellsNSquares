package at.koopro.spells_n_squares.features.artifact.fluid;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for Elixir fluids, blocks, and buckets.
 */
public class ElixirFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
        net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES, SpellsNSquares.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(
        Registries.FLUID, SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
        Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    // Elixir Base Fluid Type
    public static final DeferredHolder<FluidType, ElixirBaseFluidType> ELIXIR_BASE_TYPE = FLUID_TYPES.register(
        "elixir_base",
        () -> new ElixirBaseFluidType(FluidType.Properties.create()
            .density(1200)
            .viscosity(1500)
            .temperature(300)
            .sound(SoundActions.BUCKET_FILL, net.minecraft.sounds.SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY))
    );
    
    // Elixir Base Fluids
    public static final DeferredHolder<Fluid, FlowingFluid> ELIXIR_BASE = FLUIDS.register(
        "elixir_base",
        () -> new ElixirBaseFluid.Source()
    );
    public static final DeferredHolder<Fluid, FlowingFluid> ELIXIR_BASE_FLOWING = FLUIDS.register(
        "elixir_base_flowing",
        () -> new ElixirBaseFluid.Flowing()
    );
    
    // Elixir Base Block
    public static final DeferredHolder<Block, LiquidBlock> ELIXIR_BASE_BLOCK = BLOCKS.register(
        "elixir_base",
        id -> new LiquidBlock(ELIXIR_BASE.value(), RegistryHelper.createBlockProperties(id)
            .strength(100.0f)
            .noOcclusion()
            .noLootTable()
            .replaceable())
    );
    
    // Elixir Base Bucket
    public static final DeferredHolder<Item, BucketItem> ELIXIR_BASE_BUCKET = ITEMS.register(
        "elixir_base_bucket",
        id -> new BucketItem(ELIXIR_BASE.value(), RegistryHelper.createItemProperties(id)
            .craftRemainder(Items.BUCKET)
            .stacksTo(1))
    );
    
    // Elixir of Life Fluid Type
    public static final DeferredHolder<FluidType, ElixirOfLifeFluidType> ELIXIR_OF_LIFE_TYPE = FLUID_TYPES.register(
        "elixir_of_life",
        () -> new ElixirOfLifeFluidType(FluidType.Properties.create()
            .density(1200)
            .viscosity(1500)
            .temperature(300)
            .sound(SoundActions.BUCKET_FILL, net.minecraft.sounds.SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY))
    );
    
    // Elixir of Life Fluids
    public static final DeferredHolder<Fluid, FlowingFluid> ELIXIR_OF_LIFE = FLUIDS.register(
        "elixir_of_life",
        () -> new ElixirOfLifeFluid.Source()
    );
    public static final DeferredHolder<Fluid, FlowingFluid> ELIXIR_OF_LIFE_FLOWING = FLUIDS.register(
        "elixir_of_life_flowing",
        () -> new ElixirOfLifeFluid.Flowing()
    );
    
    // Elixir of Life Block
    public static final DeferredHolder<Block, LiquidBlock> ELIXIR_OF_LIFE_BLOCK = BLOCKS.register(
        "elixir_of_life",
        id -> new LiquidBlock(ELIXIR_OF_LIFE.value(), RegistryHelper.createBlockProperties(id)
            .strength(100.0f)
            .noOcclusion()
            .noLootTable()
            .replaceable())
    );
    
    // Elixir of Life Bucket
    public static final DeferredHolder<Item, BucketItem> ELIXIR_OF_LIFE_BUCKET = ITEMS.register(
        "elixir_of_life_bucket",
        id -> new BucketItem(ELIXIR_OF_LIFE.value(), RegistryHelper.createItemProperties(id)
            .craftRemainder(Items.BUCKET)
            .stacksTo(1)
            .rarity(net.minecraft.world.item.Rarity.EPIC))
    );
    
    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}

