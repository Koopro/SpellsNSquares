package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModBlocks;
// Future feature registries (commented out until features are implemented)
// When implementing a feature, uncomment the corresponding registry import and add it to the list below
// import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
// import at.koopro.spells_n_squares.features.building.BuildingRegistry;
// import at.koopro.spells_n_squares.features.combat.CombatRegistry;
// import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
// import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
// import at.koopro.spells_n_squares.features.education.EducationRegistry;
// import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates blockstate and model JSON files for non-tree mod blocks.
 */
public class ModBlockModelProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    
    public ModBlockModelProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Collect all block registries from feature registries
        List<DeferredRegister<Block>> blockRegistries = List.of(
            ModBlocks.BLOCKS,  // Generic blocks (currently empty)
            StorageRegistry.BLOCKS
            // Future feature registries (uncomment when features are implemented):
            // AutomationRegistry.BLOCKS,
            // BuildingRegistry.BLOCKS,
            // CombatRegistry.BLOCKS,
            // CommunicationRegistry.BLOCKS,
            // EconomyRegistry.BLOCKS,
            // EducationRegistry.BLOCKS,
            // EnchantmentsRegistry.BLOCKS
        );
        
        // Generate models for all blocks from all registries
        for (DeferredRegister<Block> registry : blockRegistries) {
            registry.getEntries().forEach(holder -> {
                // Get the registry name - try holder.getId() first, then fall back to getting from registry
                String blockName;
                try {
                    blockName = holder.getId().getPath();
                } catch (Exception e) {
                    // Fallback: get from the block's registry key
                    Block block = holder.get();
                    blockName = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
                }
                
                Block block = holder.get();
                
                // Determine block type and generate appropriate models
                String blockType = determineBlockType(blockName, block);
                
                if (blockType != null) {
                    switch (blockType) {
                        case "cube_all" -> futures.add(generateSimpleCube(cache, block, blockName));
                        case "cross" -> futures.add(generateCross(cache, block, blockName));
                        case "cube_column" -> futures.add(generateCubeColumn(cache, block, blockName));
                        default -> futures.add(generateSimpleCube(cache, block, blockName));
                    }
                }
            });
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private String determineBlockType(String blockName, Block block) {
        // Plant blocks use cross model
        if (blockName.contains("plant") || 
            blockName.equals("devils_snare") ||
            blockName.equals("venomous_tentacula")) {
            return "cross";
        }
        
        // Whomping willow might be a column-like block
        if (blockName.equals("whomping_willow")) {
            return "cube_column";
        }
        
        // Most other blocks use simple cube_all
        return "cube_all";
    }
    
    private CompletableFuture<?> generateSimpleCube(CachedOutput cache, Block block, String name) {
        String texture = modId + ":block/" + name;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("model", modId + ":block/" + name);
        variants.add("", model);
        blockstate.add("variants", variants);
        
        // Block model
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", texture);
        blockModel.add("textures", textures);
        
        // Item model (references block model)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        
        return CompletableFuture.allOf(
            saveJson(cache, blockstate, getBlockstatePath(name)),
            saveJson(cache, blockModel, getBlockModelPath(name)),
            saveJson(cache, itemModel, getItemModelPath(name))
        );
    }
    
    private CompletableFuture<?> generateCross(CachedOutput cache, Block block, String name) {
        String texture = modId + ":block/" + name;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("model", modId + ":block/" + name);
        variants.add("", model);
        blockstate.add("variants", variants);
        
        // Block model
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cross");
        blockModel.addProperty("render_type", "minecraft:cutout");
        JsonObject textures = new JsonObject();
        textures.addProperty("cross", texture);
        blockModel.add("textures", textures);
        
        // Item model (flat)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", texture);
        itemModel.add("textures", itemTextures);
        
        return CompletableFuture.allOf(
            saveJson(cache, blockstate, getBlockstatePath(name)),
            saveJson(cache, blockModel, getBlockModelPath(name)),
            saveJson(cache, itemModel, getItemModelPath(name))
        );
    }
    
    private CompletableFuture<?> generateCubeColumn(CachedOutput cache, Block block, String name) {
        String sideTexture = modId + ":block/" + name;
        String endTexture = modId + ":block/" + name + "_top";
        
        // Blockstate with axis rotation
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        JsonObject modelY = new JsonObject();
        modelY.addProperty("model", modId + ":block/" + name);
        variants.add("axis=y", modelY);
        
        JsonObject modelZ = new JsonObject();
        modelZ.addProperty("model", modId + ":block/" + name + "_horizontal");
        modelZ.addProperty("x", 90);
        variants.add("axis=z", modelZ);
        
        JsonObject modelX = new JsonObject();
        modelX.addProperty("model", modId + ":block/" + name + "_horizontal");
        modelX.addProperty("x", 90);
        modelX.addProperty("y", 90);
        variants.add("axis=x", modelX);
        
        blockstate.add("variants", variants);
        
        // Block model (vertical)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cube_column");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", sideTexture);
        textures.addProperty("end", endTexture);
        blockModel.add("textures", textures);
        
        // Block model (horizontal)
        JsonObject blockModelH = new JsonObject();
        blockModelH.addProperty("parent", "minecraft:block/cube_column_horizontal");
        JsonObject texturesH = new JsonObject();
        texturesH.addProperty("side", sideTexture);
        texturesH.addProperty("end", endTexture);
        blockModelH.add("textures", texturesH);
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        
        return CompletableFuture.allOf(
            saveJson(cache, blockstate, getBlockstatePath(name)),
            saveJson(cache, blockModel, getBlockModelPath(name)),
            saveJson(cache, blockModelH, getBlockModelPath(name + "_horizontal")),
            saveJson(cache, itemModel, getItemModelPath(name))
        );
    }
    
    // ===== PATH HELPERS =====
    
    private Path getBlockstatePath(String name) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("blockstates").resolve(name + ".json");
    }
    
    private Path getBlockModelPath(String name) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("models").resolve("block").resolve(name + ".json");
    }
    
    private Path getItemModelPath(String name) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("models").resolve("item").resolve(name + ".json");
    }
    
    private CompletableFuture<?> saveJson(CachedOutput cache, JsonObject json, Path path) {
        return DataProvider.saveStable(cache, json, path);
    }
    
    @Override
    public String getName() {
        return "Block Models - " + modId;
    }
}

















