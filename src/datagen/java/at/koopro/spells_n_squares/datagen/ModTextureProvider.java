package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModBlocks;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.features.artifacts.ArtifactsRegistry;
import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
import at.koopro.spells_n_squares.features.building.BuildingRegistry;
import at.koopro.spells_n_squares.features.cloak.CloakRegistry;
// import at.koopro.spells_n_squares.features.combat.CombatRegistry; // TODO: Re-enable when CombatRegistry is implemented
// import at.koopro.spells_n_squares.features.communication.CommunicationRegistry; // TODO: Re-enable when CommunicationRegistry is implemented
import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
// import at.koopro.spells_n_squares.features.education.EducationRegistry; // TODO: Re-enable when EducationRegistry is implemented
import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry;
import at.koopro.spells_n_squares.features.navigation.NavigationRegistry;
import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
// import at.koopro.spells_n_squares.features.quidditch.QuidditchRegistry; // TODO: Re-enable when QuidditchRegistry is implemented
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates placeholder textures for items and blocks that are missing textures.
 * Creates minimal 16x16 PNG files with colored backgrounds and borders.
 */
public class ModTextureProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    private final Path sourceResourcesPath;
    
    // Tree block suffixes to skip (they already have textures)
    private static final Set<String> TREE_BLOCK_SUFFIXES = Set.of(
        "_log", "_planks", "_leaves", "_sapling", "_door", "_trapdoor",
        "_stairs", "_slab", "_fence", "_fence_gate", "_button", "_pressure_plate",
        "_sign", "_wall_sign", "_hanging_sign", "_wall_hanging_sign", "_wood"
    );
    
    public ModTextureProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
        
        // Initialize the datagen config
        ItemDatagenConfig.initialize();
        
        // Determine source resources path
        Path resourcesPath;
        try {
            Path currentDir = Paths.get("").toAbsolutePath();
            Path projectRoot = currentDir.getParent(); // Go up from run/ to project root
            resourcesPath = projectRoot.resolve("src/main/resources");
        } catch (Exception e) {
            // Fallback to current directory
            resourcesPath = Paths.get("src/main/resources");
        }
        this.sourceResourcesPath = resourcesPath;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Generate textures for items
        futures.addAll(generateItemTextures(cache));
        
        // Generate textures for blocks
        futures.addAll(generateBlockTextures(cache));
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private List<CompletableFuture<?>> generateItemTextures(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Collect all item registries from feature registries
        List<DeferredRegister.Items> itemRegistries = List.of(
            ModItems.ITEMS,  // Generic items
            FlashlightRegistry.ITEMS,
            WandRegistry.ITEMS,
            CloakRegistry.ITEMS,
            ArtifactsRegistry.ITEMS,
            StorageRegistry.ITEMS,
            TransportationRegistry.ITEMS,
            // CommunicationRegistry.ITEMS, // TODO: Re-enable when CommunicationRegistry is implemented
            AutomationRegistry.ITEMS,
            BuildingRegistry.ITEMS,
            NavigationRegistry.ITEMS,
            RobesRegistry.ITEMS,
            PotionsRegistry.ITEMS,
            // QuidditchRegistry.ITEMS, // TODO: Re-enable when QuidditchRegistry is implemented
            EconomyRegistry.ITEMS,
            // EducationRegistry.ITEMS, // TODO: Re-enable when EducationRegistry is implemented
            // CombatRegistry.ITEMS, // TODO: Re-enable when CombatRegistry is implemented
            EnchantmentsRegistry.ITEMS
        );
        
        Path itemTextureDir = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("textures").resolve("item");
        
        // Generate textures for all items from all registries
        for (DeferredRegister.Items registry : itemRegistries) {
            registry.getEntries().forEach(holder -> {
                try {
                    // Get the registry name
                    String itemName;
                    try {
                        itemName = holder.getId().getPath();
                    } catch (Exception e) {
                        Item item = holder.get();
                        itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getPath();
                    }
                    
                    // Skip items that already have custom models (GeckoLib or manual JSON)
                    if (!ItemDatagenConfig.shouldGenerateModel(itemName)) {
                        return;
                    }
                    
                    // Skip block items - they use block textures, not item textures
                    Item item = holder.get();
                    if (item instanceof net.minecraft.world.item.BlockItem) {
                        return;
                    }
                    
                    // Check if texture already exists
                    Path sourceTexture = sourceResourcesPath.resolve("assets").resolve(modId)
                        .resolve("textures").resolve("item").resolve(itemName + ".png");
                    
                    if (Files.exists(sourceTexture)) {
                        return; // Texture already exists, skip
                    }
                    
                    // Generate placeholder texture
                    futures.add(generateTexture(cache, itemName, itemTextureDir, false));
                } catch (Exception e) {
                    System.err.println("Failed to generate texture for item: " + holder.getId() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        
        return futures;
    }
    
    private List<CompletableFuture<?>> generateBlockTextures(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Collect all block registries from feature registries
        List<DeferredRegister<Block>> blockRegistries = List.of(
            ModBlocks.BLOCKS,  // Generic blocks (currently empty)
            StorageRegistry.BLOCKS,
            // CommunicationRegistry.BLOCKS, // TODO: Re-enable when CommunicationRegistry is implemented
            AutomationRegistry.BLOCKS,
            BuildingRegistry.BLOCKS,
            // CombatRegistry.BLOCKS, // TODO: Re-enable when CombatRegistry is implemented
            EconomyRegistry.BLOCKS,
            // EducationRegistry.BLOCKS, // TODO: Re-enable when EducationRegistry is implemented
            EnchantmentsRegistry.BLOCKS
        );
        
        Path blockTextureDir = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("textures").resolve("block");
        
        // Generate textures for all blocks from all registries
        for (DeferredRegister<Block> registry : blockRegistries) {
            registry.getEntries().forEach(holder -> {
                try {
                    // Get the registry name
                    String blockName;
                    try {
                        blockName = holder.getId().getPath();
                    } catch (Exception e) {
                        Block block = holder.get();
                        blockName = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
                    }
                    
                    // Skip tree blocks (they already have textures)
                    if (isTreeBlock(blockName)) {
                        return;
                    }
                    
                    // Check if texture already exists
                    Path sourceTexture = sourceResourcesPath.resolve("assets").resolve(modId)
                        .resolve("textures").resolve("block").resolve(blockName + ".png");
                    
                    if (Files.exists(sourceTexture)) {
                        return; // Texture already exists, skip
                    }
                    
                    // Generate placeholder texture
                    futures.add(generateTexture(cache, blockName, blockTextureDir, true));
                } catch (Exception e) {
                    System.err.println("Failed to generate texture for block: " + holder.getId() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        
        return futures;
    }
    
    private boolean isTreeBlock(String blockName) {
        // Check if block name contains any tree block suffix
        return TREE_BLOCK_SUFFIXES.stream().anyMatch(blockName::endsWith) ||
               blockName.startsWith("stripped_");
    }
    
    private CompletableFuture<?> generateTexture(CachedOutput cache, String name, Path textureDir, boolean isBlock) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Ensure directory exists
                Files.createDirectories(textureDir);
                
                // Create placeholder image
                BufferedImage image = createPlaceholderImage(name, isBlock);
                
                // Convert image to bytes
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                ImageIO.write(image, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();
                
                // Compute hash for cache
                com.google.common.hash.HashCode hashCode = com.google.common.hash.Hashing.sha256().hashBytes(imageBytes);
                
                // Save image using cache
                Path texturePath = textureDir.resolve(name + ".png");
                cache.writeIfNeeded(texturePath, imageBytes, hashCode);
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate texture for " + name, e);
            }
        });
    }
    
    private BufferedImage createPlaceholderImage(String name, boolean isBlock) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Enable anti-aliasing for smoother borders
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Generate color from name hash
        Color bgColor = getColorForName(name, isBlock);
        Color borderColor = bgColor.darker().darker();
        
        // Fill background
        g.setColor(bgColor);
        g.fillRect(0, 0, 16, 16);
        
        // Draw border (1 pixel)
        g.setColor(borderColor);
        g.drawRect(0, 0, 15, 15);
        
        // Add a subtle inner highlight for depth
        g.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 100));
        g.drawLine(1, 1, 14, 1);
        g.drawLine(1, 1, 1, 14);
        
        g.dispose();
        return img;
    }
    
    private Color getColorForName(String name, boolean isBlock) {
        // Generate consistent color from name hash
        int hash = name.hashCode();
        
        // Use different hue ranges for items vs blocks
        // Items: warmer colors (reds, oranges, yellows) - hue 0-60
        // Blocks: cooler colors (blues, greens, purples) - hue 120-300
        
        float hue;
        if (isBlock) {
            // Blocks: hue range 120-300 (green to purple)
            hue = ((hash & 0x7FFFFFFF) % 180 + 120) / 360f;
        } else {
            // Items: hue range 0-60 (red to yellow)
            hue = ((hash & 0x7FFFFFFF) % 60) / 360f;
        }
        
        // Saturation: 0.5-0.9 (moderately saturated)
        float saturation = 0.5f + ((hash >> 8) & 0xFF) / 255f * 0.4f;
        
        // Lightness: 0.4-0.7 (not too dark, not too light)
        float lightness = 0.4f + ((hash >> 16) & 0xFF) / 255f * 0.3f;
        
        return Color.getHSBColor(hue, saturation, lightness);
    }
    
    @Override
    public String getName() {
        return "Placeholder Textures - " + modId;
    }
}












