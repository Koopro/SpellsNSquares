package at.koopro.spells_n_squares.block.tree;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

/**
 * Helper class for registering all blocks for a tree/wood type.
 * This reduces code duplication by providing a single method to register
 * all 15 blocks for a given wood type.
 */
public final class TreeBlockRegistration {
    
    private TreeBlockRegistration() {
        // Utility class
    }
    
    /**
     * Registers all blocks for a given wood type.
     * 
     * @param woodType The wood type to register
     * @param blockRegistry The block registry to use
     * @param itemRegistry The item registry to use
     * @return A TreeBlockSet containing all registered blocks
     */
    public static TreeBlockSet registerTree(
            ModWoodType woodType,
            DeferredRegister<Block> blockRegistry,
            DeferredRegister.Items itemRegistry
    ) {
        String id = woodType.getId();
        MapColor planksColor = woodType.getPlanksColor();
        MapColor barkColor = woodType.getBarkColor();
        
        // Core wood blocks
        var log = registerBlockWithItem(blockRegistry, itemRegistry, id + "_log",
            () -> new ModLogBlock(ModLogBlock.createDefaultProperties(planksColor, barkColor)
                .setId(createBlockKey(id + "_log"))));
        
        var strippedLog = registerBlockWithItem(blockRegistry, itemRegistry, "stripped_" + id + "_log",
            () -> new ModLogBlock(ModLogBlock.createStrippedProperties(planksColor)
                .setId(createBlockKey("stripped_" + id + "_log"))));
        
        var wood = registerBlockWithItem(blockRegistry, itemRegistry, id + "_wood",
            () -> new ModWoodBlock(ModWoodBlock.createDefaultProperties(barkColor)
                .setId(createBlockKey(id + "_wood"))));
        
        var strippedWood = registerBlockWithItem(blockRegistry, itemRegistry, "stripped_" + id + "_wood",
            () -> new ModWoodBlock(ModWoodBlock.createDefaultProperties(planksColor)
                .setId(createBlockKey("stripped_" + id + "_wood"))));
        
        var planks = registerBlockWithItem(blockRegistry, itemRegistry, id + "_planks",
            () -> new ModPlanksBlock(ModPlanksBlock.createDefaultProperties(planksColor)
                .setId(createBlockKey(id + "_planks"))));
        
        // Foliage
        var leaves = registerBlockWithItem(blockRegistry, itemRegistry, id + "_leaves",
            () -> new ModLeavesBlock(ModLeavesBlock.createDefaultProperties()
                .setId(createBlockKey(id + "_leaves"))));
        
        // Create a placeholder tree grower (actual tree generation will be added later)
        TreeGrower treeGrower = new TreeGrower(
            SpellsNSquares.MODID + ":" + id,
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
        
        var sapling = registerBlockWithItem(blockRegistry, itemRegistry, id + "_sapling",
            () -> new ModSaplingBlock(treeGrower, ModSaplingBlock.createDefaultProperties()
                .setId(createBlockKey(id + "_sapling"))));
        
        // Decorative blocks
        var stairs = registerBlockWithItem(blockRegistry, itemRegistry, id + "_stairs",
            () -> new ModStairsBlock(
                () -> planks.get().defaultBlockState(),
                createWoodProperties(planksColor).setId(createBlockKey(id + "_stairs"))
            ));
        
        var slab = registerBlockWithItem(blockRegistry, itemRegistry, id + "_slab",
            () -> new ModSlabBlock(createWoodProperties(planksColor)
                .setId(createBlockKey(id + "_slab"))));
        
        var fence = registerBlockWithItem(blockRegistry, itemRegistry, id + "_fence",
            () -> new ModFenceBlock(createWoodProperties(planksColor)
                .setId(createBlockKey(id + "_fence"))));
        
        var fenceGate = registerBlockWithItem(blockRegistry, itemRegistry, id + "_fence_gate",
            () -> new ModFenceGateBlock(WoodType.OAK, createWoodProperties(planksColor)
                .setId(createBlockKey(id + "_fence_gate"))));
        
        // Utility blocks
        var door = registerBlockWithItem(blockRegistry, itemRegistry, id + "_door",
            () -> new ModDoorBlock(BlockSetType.OAK, createDoorProperties(planksColor)
                .setId(createBlockKey(id + "_door"))));
        
        var trapdoor = registerBlockWithItem(blockRegistry, itemRegistry, id + "_trapdoor",
            () -> new ModTrapdoorBlock(BlockSetType.OAK, createTrapdoorProperties(planksColor)
                .setId(createBlockKey(id + "_trapdoor"))));
        
        var pressurePlate = registerBlockWithItem(blockRegistry, itemRegistry, id + "_pressure_plate",
            () -> new ModPressurePlateBlock(BlockSetType.OAK, createPressurePlateProperties(planksColor)
                .setId(createBlockKey(id + "_pressure_plate"))));
        
        var button = registerBlockWithItem(blockRegistry, itemRegistry, id + "_button",
            () -> new ModButtonBlock(BlockSetType.OAK, createButtonProperties()
                .setId(createBlockKey(id + "_button"))));
        
        return new TreeBlockSet(
            woodType,
            log, strippedLog, wood, strippedWood, planks,
            leaves, sapling,
            stairs, slab, fence, fenceGate,
            door, trapdoor, pressurePlate, button
        );
    }
    
    /**
     * Registers a block and its corresponding BlockItem.
     */
    private static <T extends Block> DeferredHolder<Block, T> registerBlockWithItem(
            DeferredRegister<Block> blockRegistry,
            DeferredRegister.Items itemRegistry,
            String name,
            java.util.function.Supplier<T> blockSupplier
    ) {
        DeferredHolder<Block, T> block = blockRegistry.register(name, id -> blockSupplier.get());
        
        // Register corresponding item
        // Note: In Neoforged, items are registered after blocks in a separate RegisterEvent,
        // so blocks should be bound when this lambda executes
        itemRegistry.register(name, id -> new BlockItem(
            block.value(),
            new Item.Properties().setId(createItemKey(name))
        ));
        
        return block;
    }
    
    private static ResourceKey<Block> createBlockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, name));
    }
    
    private static ResourceKey<Item> createItemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, name));
    }
    
    private static BlockBehaviour.Properties createWoodProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(2.0f, 3.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }
    
    private static BlockBehaviour.Properties createDoorProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(3.0f)
            .sound(SoundType.WOOD)
            .noOcclusion()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY);
    }
    
    private static BlockBehaviour.Properties createTrapdoorProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(3.0f)
            .sound(SoundType.WOOD)
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .ignitedByLava();
    }
    
    private static BlockBehaviour.Properties createPressurePlateProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .noCollision()
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY);
    }
    
    private static BlockBehaviour.Properties createButtonProperties() {
        return BlockBehaviour.Properties.of()
            .noCollision()
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.DESTROY);
    }
}








