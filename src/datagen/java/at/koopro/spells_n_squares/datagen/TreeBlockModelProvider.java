package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.environment.block.TreeBlockSet;
import at.koopro.spells_n_squares.core.registry.ModTreeBlocks;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Custom data provider that generates blockstate and model JSON files for tree blocks.
 * This bypasses NeoForge's ModelProvider validation that requires ALL items to have models.
 */
public class TreeBlockModelProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    
    public TreeBlockModelProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        for (TreeBlockSet set : ModTreeBlocks.getAllTreeSets()) {
            futures.addAll(generateTreeBlockModels(cache, set));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private List<CompletableFuture<?>> generateTreeBlockModels(CachedOutput cache, TreeBlockSet set) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String woodId = set.getWoodId();
        
        // Log blocks
        futures.add(generateRotatedPillar(cache, set.log().get(), woodId + "_log"));
        futures.add(generateRotatedPillar(cache, set.strippedLog().get(), "stripped_" + woodId + "_log"));
        futures.add(generateWood(cache, set.wood().get(), woodId + "_wood", woodId + "_log"));
        futures.add(generateWood(cache, set.strippedWood().get(), "stripped_" + woodId + "_wood", "stripped_" + woodId + "_log"));
        
        // Planks - simple cube
        futures.add(generateSimpleCube(cache, set.planks().get(), woodId + "_planks"));
        
        // Leaves - simple cube with cutout render type
        futures.add(generateLeaves(cache, set.leaves().get(), woodId + "_leaves"));
        
        // Sapling - cross model
        futures.add(generateCross(cache, set.sapling().get(), woodId + "_sapling"));
        
        // Door
        futures.add(generateDoor(cache, set.door().get(), woodId + "_door"));
        
        // Trapdoor
        futures.add(generateTrapdoor(cache, set.trapdoor().get(), woodId + "_trapdoor"));
        
        // Stairs
        futures.add(generateStairs(cache, set.stairs().get(), woodId + "_stairs", woodId + "_planks"));
        
        // Slab
        futures.add(generateSlab(cache, set.slab().get(), woodId + "_slab", woodId + "_planks"));
        
        // Fence
        futures.add(generateFence(cache, set.fence().get(), woodId + "_fence", woodId + "_planks"));
        
        // Fence Gate
        futures.add(generateFenceGate(cache, set.fenceGate().get(), woodId + "_fence_gate", woodId + "_planks"));
        
        // Pressure Plate
        futures.add(generatePressurePlate(cache, set.pressurePlate().get(), woodId + "_pressure_plate", woodId + "_planks"));
        
        // Button
        futures.add(generateButton(cache, set.button().get(), woodId + "_button", woodId + "_planks"));
        
        return futures;
    }
    
    // ===== BLOCKSTATE GENERATORS =====
    
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
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        
        return CompletableFuture.allOf(
            saveJson(cache, blockstate, getBlockstatePath(name)),
            saveJson(cache, blockModel, getBlockModelPath(name)),
            saveJson(cache, itemModel, getItemModelPath(name))
        );
    }
    
    private CompletableFuture<?> generateLeaves(CachedOutput cache, Block block, String name) {
        String texture = modId + ":block/" + name;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("model", modId + ":block/" + name);
        variants.add("", model);
        blockstate.add("variants", variants);
        
        // Block model with cutout render type
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/leaves");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", texture);
        blockModel.add("textures", textures);
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        
        return CompletableFuture.allOf(
            saveJson(cache, blockstate, getBlockstatePath(name)),
            saveJson(cache, blockModel, getBlockModelPath(name)),
            saveJson(cache, itemModel, getItemModelPath(name))
        );
    }
    
    private CompletableFuture<?> generateRotatedPillar(CachedOutput cache, Block block, String name) {
        String sideTexture = modId + ":block/" + name;
        String endTexture = modId + ":block/" + name + "_top";
        
        // Blockstate with axis rotation
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        JsonObject modelX = new JsonObject();
        modelX.addProperty("model", modId + ":block/" + name);
        variants.add("axis=y", modelX);
        
        JsonObject modelY = new JsonObject();
        modelY.addProperty("model", modId + ":block/" + name + "_horizontal");
        modelY.addProperty("x", 90);
        variants.add("axis=z", modelY);
        
        JsonObject modelZ = new JsonObject();
        modelZ.addProperty("model", modId + ":block/" + name + "_horizontal");
        modelZ.addProperty("x", 90);
        modelZ.addProperty("y", 90);
        variants.add("axis=x", modelZ);
        
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
    
    private CompletableFuture<?> generateWood(CachedOutput cache, Block block, String name, String logName) {
        String texture = modId + ":block/" + logName;
        
        // Blockstate with axis rotation
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        JsonObject modelY = new JsonObject();
        modelY.addProperty("model", modId + ":block/" + name);
        variants.add("axis=y", modelY);
        
        JsonObject modelZ = new JsonObject();
        modelZ.addProperty("model", modId + ":block/" + name);
        modelZ.addProperty("x", 90);
        variants.add("axis=z", modelZ);
        
        JsonObject modelX = new JsonObject();
        modelX.addProperty("model", modId + ":block/" + name);
        modelX.addProperty("x", 90);
        modelX.addProperty("y", 90);
        variants.add("axis=x", modelX);
        
        blockstate.add("variants", variants);
        
        // Block model (all sides same texture)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cube_column");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", texture);
        textures.addProperty("end", texture);
        blockModel.add("textures", textures);
        
        // Item model
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
    
    private CompletableFuture<?> generateDoor(CachedOutput cache, Block block, String name) {
        String bottomTexture = modId + ":block/" + name + "_bottom";
        String topTexture = modId + ":block/" + name + "_top";
        
        // Complex blockstate for doors
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        // Generate all door states
        String[] halves = {"lower", "upper"};
        String[] facings = {"east", "north", "south", "west"};
        int[] rotations = {0, 270, 90, 180};
        boolean[] opens = {false, true};
        String[] hinges = {"left", "right"};
        
        for (int f = 0; f < facings.length; f++) {
            String facing = facings[f];
            int rotation = rotations[f];
            
            for (String half : halves) {
                for (boolean open : opens) {
                    for (String hinge : hinges) {
                        String key = "facing=" + facing + ",half=" + half + ",hinge=" + hinge + ",open=" + open;
                        JsonObject m = new JsonObject();
                        
                        String modelSuffix;
                        int y = rotation;
                        
                        if (!open) {
                            modelSuffix = hinge.equals("left") ? "_bottom_left" : "_bottom_right";
                            if (half.equals("upper")) {
                                modelSuffix = hinge.equals("left") ? "_top_left" : "_top_right";
                            }
                        } else {
                            if (hinge.equals("left")) {
                                modelSuffix = half.equals("lower") ? "_bottom_left_open" : "_top_left_open";
                                y = (rotation + 90) % 360;
                            } else {
                                modelSuffix = half.equals("lower") ? "_bottom_right_open" : "_top_right_open";
                                y = (rotation + 270) % 360;
                            }
                        }
                        
                        m.addProperty("model", modId + ":block/" + name + modelSuffix);
                        if (y != 0) m.addProperty("y", y);
                        variants.add(key, m);
                    }
                }
            }
        }
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Door models
        String[] doorModels = {"_bottom_left", "_bottom_left_open", "_bottom_right", "_bottom_right_open",
                              "_top_left", "_top_left_open", "_top_right", "_top_right_open"};
        String[] parentModels = {"door_bottom_left", "door_bottom_left_open", "door_bottom_right", "door_bottom_right_open",
                                "door_top_left", "door_top_left_open", "door_top_right", "door_top_right_open"};
        
        for (int i = 0; i < doorModels.length; i++) {
            JsonObject doorModel = new JsonObject();
            doorModel.addProperty("parent", "minecraft:block/" + parentModels[i]);
            doorModel.addProperty("render_type", "minecraft:cutout");
            JsonObject textures = new JsonObject();
            textures.addProperty("bottom", bottomTexture);
            textures.addProperty("top", topTexture);
            doorModel.add("textures", textures);
            futures.add(saveJson(cache, doorModel, getBlockModelPath(name + doorModels[i])));
        }
        
        // Item model (flat)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", modId + ":item/" + name);
        itemModel.add("textures", itemTextures);
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateTrapdoor(CachedOutput cache, Block block, String name) {
        String texture = modId + ":block/" + name;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        String[] facings = {"east", "north", "south", "west"};
        int[] rotations = {90, 0, 180, 270};
        String[] halves = {"bottom", "top"};
        boolean[] opens = {false, true};
        
        for (int f = 0; f < facings.length; f++) {
            String facing = facings[f];
            int baseRotation = rotations[f];
            
            for (String half : halves) {
                for (boolean open : opens) {
                    String key = "facing=" + facing + ",half=" + half + ",open=" + open;
                    JsonObject m = new JsonObject();
                    
                    String modelSuffix;
                    int x = 0, y = baseRotation;
                    
                    if (!open) {
                        modelSuffix = half.equals("bottom") ? "_bottom" : "_top";
                    } else {
                        modelSuffix = "_open";
                    }
                    
                    m.addProperty("model", modId + ":block/" + name + modelSuffix);
                    if (x != 0) m.addProperty("x", x);
                    if (y != 0) m.addProperty("y", y);
                    variants.add(key, m);
                }
            }
        }
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Bottom model
        JsonObject bottomModel = new JsonObject();
        bottomModel.addProperty("parent", "minecraft:block/template_orientable_trapdoor_bottom");
        bottomModel.addProperty("render_type", "minecraft:cutout");
        JsonObject bottomTextures = new JsonObject();
        bottomTextures.addProperty("texture", texture);
        bottomModel.add("textures", bottomTextures);
        futures.add(saveJson(cache, bottomModel, getBlockModelPath(name + "_bottom")));
        
        // Top model
        JsonObject topModel = new JsonObject();
        topModel.addProperty("parent", "minecraft:block/template_orientable_trapdoor_top");
        topModel.addProperty("render_type", "minecraft:cutout");
        JsonObject topTextures = new JsonObject();
        topTextures.addProperty("texture", texture);
        topModel.add("textures", topTextures);
        futures.add(saveJson(cache, topModel, getBlockModelPath(name + "_top")));
        
        // Open model
        JsonObject openModel = new JsonObject();
        openModel.addProperty("parent", "minecraft:block/template_orientable_trapdoor_open");
        openModel.addProperty("render_type", "minecraft:cutout");
        JsonObject openTextures = new JsonObject();
        openTextures.addProperty("texture", texture);
        openModel.add("textures", openTextures);
        futures.add(saveJson(cache, openModel, getBlockModelPath(name + "_open")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name + "_bottom");
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateStairs(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate - matching vanilla Minecraft stairs pattern
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        // Stairs rotation logic based on vanilla Minecraft
        // Each facing has a base rotation, then shapes add specific rotations
        String[] facings = {"east", "north", "south", "west"};
        String[] halves = {"bottom", "top"};
        String[] shapes = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
        
        for (String facing : facings) {
            for (String half : halves) {
                for (String shape : shapes) {
                    String key = "facing=" + facing + ",half=" + half + ",shape=" + shape;
                    JsonObject m = new JsonObject();
                    
                    String modelName = name;
                    int x = 0;
                    int y = 0;
                    boolean uvlock = false;
                    
                    // Determine model type
                    if (shape.equals("inner_left") || shape.equals("inner_right")) {
                        modelName = name + "_inner";
                    } else if (shape.equals("outer_left") || shape.equals("outer_right")) {
                        modelName = name + "_outer";
                    }
                    
                    // Calculate rotations based on facing, half, and shape
                    // This matches vanilla Minecraft's stairs blockstate pattern
                    switch (facing) {
                        case "east" -> {
                            if (half.equals("bottom")) {
                                switch (shape) {
                                    case "straight" -> { y = 0; }
                                    case "outer_right" -> { y = 0; }
                                    case "outer_left" -> { y = 270; uvlock = true; }
                                    case "inner_right" -> { y = 0; }
                                    case "inner_left" -> { y = 270; uvlock = true; }
                                }
                            } else { // top
                                x = 180;
                                uvlock = true;
                                switch (shape) {
                                    case "straight" -> { y = 0; }
                                    case "outer_right" -> { y = 90; }
                                    case "outer_left" -> { y = 0; }
                                    case "inner_right" -> { y = 90; }
                                    case "inner_left" -> { y = 0; }
                                }
                            }
                        }
                        case "north" -> {
                            if (half.equals("bottom")) {
                                switch (shape) {
                                    case "straight" -> { y = 270; uvlock = true; }
                                    case "outer_right" -> { y = 270; uvlock = true; }
                                    case "outer_left" -> { y = 180; uvlock = true; }
                                    case "inner_right" -> { y = 270; uvlock = true; }
                                    case "inner_left" -> { y = 180; uvlock = true; }
                                }
                            } else { // top
                                x = 180;
                                uvlock = true;
                                switch (shape) {
                                    case "straight" -> { y = 90; }
                                    case "outer_right" -> { y = 0; }
                                    case "outer_left" -> { y = 90; }
                                    case "inner_right" -> { y = 0; }
                                    case "inner_left" -> { y = 90; }
                                }
                            }
                        }
                        case "south" -> {
                            if (half.equals("bottom")) {
                                switch (shape) {
                                    case "straight" -> { y = 90; uvlock = true; }
                                    case "outer_right" -> { y = 90; uvlock = true; }
                                    case "outer_left" -> { y = 0; }
                                    case "inner_right" -> { y = 90; uvlock = true; }
                                    case "inner_left" -> { y = 0; }
                                }
                            } else { // top
                                x = 180;
                                uvlock = true;
                                switch (shape) {
                                    case "straight" -> { y = 270; }
                                    case "outer_right" -> { y = 180; }
                                    case "outer_left" -> { y = 270; }
                                    case "inner_right" -> { y = 180; }
                                    case "inner_left" -> { y = 270; }
                                }
                            }
                        }
                        case "west" -> {
                            if (half.equals("bottom")) {
                                switch (shape) {
                                    case "straight" -> { y = 180; uvlock = true; }
                                    case "outer_right" -> { y = 180; uvlock = true; }
                                    case "outer_left" -> { y = 90; uvlock = true; }
                                    case "inner_right" -> { y = 180; uvlock = true; }
                                    case "inner_left" -> { y = 90; uvlock = true; }
                                }
                            } else { // top
                                x = 180;
                                uvlock = true;
                                switch (shape) {
                                    case "straight" -> { y = 0; }
                                    case "outer_right" -> { y = 270; }
                                    case "outer_left" -> { y = 0; }
                                    case "inner_right" -> { y = 270; }
                                    case "inner_left" -> { y = 0; }
                                }
                            }
                        }
                    }
                    
                    m.addProperty("model", modId + ":block/" + modelName);
                    if (x != 0) m.addProperty("x", x);
                    if (y != 0) m.addProperty("y", y);
                    if (uvlock) m.addProperty("uvlock", true);
                    variants.add(key, m);
                }
            }
        }
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Stairs model
        JsonObject stairsModel = new JsonObject();
        stairsModel.addProperty("parent", "minecraft:block/stairs");
        JsonObject textures = new JsonObject();
        textures.addProperty("bottom", texture);
        textures.addProperty("top", texture);
        textures.addProperty("side", texture);
        stairsModel.add("textures", textures);
        futures.add(saveJson(cache, stairsModel, getBlockModelPath(name)));
        
        // Inner stairs model
        JsonObject innerModel = new JsonObject();
        innerModel.addProperty("parent", "minecraft:block/inner_stairs");
        innerModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, innerModel, getBlockModelPath(name + "_inner")));
        
        // Outer stairs model
        JsonObject outerModel = new JsonObject();
        outerModel.addProperty("parent", "minecraft:block/outer_stairs");
        outerModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, outerModel, getBlockModelPath(name + "_outer")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateSlab(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        JsonObject bottomModel = new JsonObject();
        bottomModel.addProperty("model", modId + ":block/" + name);
        variants.add("type=bottom", bottomModel);
        
        JsonObject topModel = new JsonObject();
        topModel.addProperty("model", modId + ":block/" + name + "_top");
        variants.add("type=top", topModel);
        
        JsonObject doubleModel = new JsonObject();
        doubleModel.addProperty("model", modId + ":block/" + planksName);
        variants.add("type=double", doubleModel);
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Slab model (bottom)
        JsonObject slabModel = new JsonObject();
        slabModel.addProperty("parent", "minecraft:block/slab");
        JsonObject textures = new JsonObject();
        textures.addProperty("bottom", texture);
        textures.addProperty("top", texture);
        textures.addProperty("side", texture);
        slabModel.add("textures", textures);
        futures.add(saveJson(cache, slabModel, getBlockModelPath(name)));
        
        // Slab model (top)
        JsonObject slabTopModel = new JsonObject();
        slabTopModel.addProperty("parent", "minecraft:block/slab_top");
        slabTopModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, slabTopModel, getBlockModelPath(name + "_top")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateFence(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate with multipart
        JsonObject blockstate = new JsonObject();
        com.google.gson.JsonArray multipart = new com.google.gson.JsonArray();
        
        // Post (always)
        JsonObject postPart = new JsonObject();
        JsonObject postApply = new JsonObject();
        postApply.addProperty("model", modId + ":block/" + name + "_post");
        postPart.add("apply", postApply);
        multipart.add(postPart);
        
        // Side variants
        String[] directions = {"north", "east", "south", "west"};
        int[] rotations = {0, 90, 180, 270};
        
        for (int i = 0; i < directions.length; i++) {
            JsonObject sidePart = new JsonObject();
            JsonObject when = new JsonObject();
            when.addProperty(directions[i], "true");
            sidePart.add("when", when);
            
            JsonObject sideApply = new JsonObject();
            sideApply.addProperty("model", modId + ":block/" + name + "_side");
            if (rotations[i] != 0) {
                sideApply.addProperty("y", rotations[i]);
                sideApply.addProperty("uvlock", true);
            }
            sidePart.add("apply", sideApply);
            multipart.add(sidePart);
        }
        
        blockstate.add("multipart", multipart);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Post model
        JsonObject postModel = new JsonObject();
        postModel.addProperty("parent", "minecraft:block/fence_post");
        JsonObject textures = new JsonObject();
        textures.addProperty("texture", texture);
        postModel.add("textures", textures);
        futures.add(saveJson(cache, postModel, getBlockModelPath(name + "_post")));
        
        // Side model
        JsonObject sideModel = new JsonObject();
        sideModel.addProperty("parent", "minecraft:block/fence_side");
        sideModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, sideModel, getBlockModelPath(name + "_side")));
        
        // Inventory model
        JsonObject invModel = new JsonObject();
        invModel.addProperty("parent", "minecraft:block/fence_inventory");
        invModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, invModel, getBlockModelPath(name + "_inventory")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name + "_inventory");
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateFenceGate(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        String[] facings = {"east", "north", "south", "west"};
        int[] rotations = {90, 0, 180, 270};
        boolean[] opens = {false, true};
        boolean[] inWalls = {false, true};
        
        for (int f = 0; f < facings.length; f++) {
            for (boolean open : opens) {
                for (boolean inWall : inWalls) {
                    String key = "facing=" + facings[f] + ",in_wall=" + inWall + ",open=" + open;
                    JsonObject m = new JsonObject();
                    
                    String modelSuffix = "";
                    if (open) modelSuffix = "_open";
                    if (inWall) modelSuffix = "_wall" + (open ? "_open" : "");
                    
                    m.addProperty("model", modId + ":block/" + name + modelSuffix);
                    if (rotations[f] != 0) {
                        m.addProperty("y", rotations[f]);
                        m.addProperty("uvlock", true);
                    }
                    variants.add(key, m);
                }
            }
        }
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Fence gate models
        String[][] models = {
            {"", "minecraft:block/template_fence_gate"},
            {"_open", "minecraft:block/template_fence_gate_open"},
            {"_wall", "minecraft:block/template_fence_gate_wall"},
            {"_wall_open", "minecraft:block/template_fence_gate_wall_open"}
        };
        
        for (String[] modelInfo : models) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", modelInfo[1]);
            JsonObject textures = new JsonObject();
            textures.addProperty("texture", texture);
            model.add("textures", textures);
            futures.add(saveJson(cache, model, getBlockModelPath(name + modelInfo[0])));
        }
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generatePressurePlate(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        JsonObject upModel = new JsonObject();
        upModel.addProperty("model", modId + ":block/" + name);
        variants.add("powered=false", upModel);
        
        JsonObject downModel = new JsonObject();
        downModel.addProperty("model", modId + ":block/" + name + "_down");
        variants.add("powered=true", downModel);
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Unpressed model
        JsonObject upPressure = new JsonObject();
        upPressure.addProperty("parent", "minecraft:block/pressure_plate_up");
        JsonObject textures = new JsonObject();
        textures.addProperty("texture", texture);
        upPressure.add("textures", textures);
        futures.add(saveJson(cache, upPressure, getBlockModelPath(name)));
        
        // Pressed model
        JsonObject downPressure = new JsonObject();
        downPressure.addProperty("parent", "minecraft:block/pressure_plate_down");
        downPressure.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, downPressure, getBlockModelPath(name + "_down")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name);
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<?> generateButton(CachedOutput cache, Block block, String name, String planksName) {
        String texture = modId + ":block/" + planksName;
        
        // Blockstate (simplified)
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        
        String[] faces = {"ceiling", "floor", "wall"};
        String[] facings = {"east", "north", "south", "west"};
        boolean[] powereds = {false, true};
        
        for (String face : faces) {
            for (String facing : facings) {
                for (boolean powered : powereds) {
                    String key = "face=" + face + ",facing=" + facing + ",powered=" + powered;
                    JsonObject m = new JsonObject();
                    
                    String modelSuffix = powered ? "_pressed" : "";
                    int x = 0, y = 0;
                    
                    switch (face) {
                        case "ceiling" -> x = 180;
                        case "wall" -> x = 90;
                    }
                    
                    switch (facing) {
                        case "east" -> y = 90;
                        case "south" -> y = 180;
                        case "west" -> y = 270;
                    }
                    
                    m.addProperty("model", modId + ":block/" + name + modelSuffix);
                    if (x != 0) m.addProperty("x", x);
                    if (y != 0) m.addProperty("y", y);
                    m.addProperty("uvlock", true);
                    variants.add(key, m);
                }
            }
        }
        
        blockstate.add("variants", variants);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(saveJson(cache, blockstate, getBlockstatePath(name)));
        
        // Button model
        JsonObject buttonModel = new JsonObject();
        buttonModel.addProperty("parent", "minecraft:block/button");
        JsonObject textures = new JsonObject();
        textures.addProperty("texture", texture);
        buttonModel.add("textures", textures);
        futures.add(saveJson(cache, buttonModel, getBlockModelPath(name)));
        
        // Button pressed model
        JsonObject pressedModel = new JsonObject();
        pressedModel.addProperty("parent", "minecraft:block/button_pressed");
        pressedModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, pressedModel, getBlockModelPath(name + "_pressed")));
        
        // Inventory model
        JsonObject invModel = new JsonObject();
        invModel.addProperty("parent", "minecraft:block/button_inventory");
        invModel.add("textures", textures.deepCopy());
        futures.add(saveJson(cache, invModel, getBlockModelPath(name + "_inventory")));
        
        // Item model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", modId + ":block/" + name + "_inventory");
        futures.add(saveJson(cache, itemModel, getItemModelPath(name)));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
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
        return "Tree Block Models - " + modId;
    }
}



















