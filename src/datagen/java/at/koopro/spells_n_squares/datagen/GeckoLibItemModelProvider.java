package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Copies GeckoLib item JSON files from main resources to generated resources.
 * These files define special GeckoLib models and need to be present in the generated resources.
 */
public class GeckoLibItemModelProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    
    // GeckoLib items that have JSON files in src/main/resources/assets/spells_n_squares/items/
    private static final List<String> GECKOLIB_ITEMS = List.of(
        "rubber_duck",
        "flashlight",
        "demo_wand",
        "demiguise_cloak",
        "deathly_hallow_cloak"
    );
    
    public GeckoLibItemModelProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // Get the source directory (main resources)
        // The working directory during data generation is typically the run/ directory
        // So we need to go up one level to get to the project root
        Path currentDir = Paths.get("").toAbsolutePath();
        Path projectRoot = currentDir.getParent(); // Go up from run/ to project root
        Path sourceDir = projectRoot.resolve("src/main/resources/assets/" + modId + "/items");
        
        // Get the target directory (generated resources)
        Path targetDir = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("items");
        
        // Copy each GeckoLib item JSON file synchronously
        // Note: These files are also copied by the copyGeckoLibItems Gradle task after restoreAssets,
        // but we generate them here during data generation as well for completeness
        try {
            // Ensure target directory exists
            Files.createDirectories(targetDir);
            
            for (String itemName : GECKOLIB_ITEMS) {
                Path sourceFile = sourceDir.resolve(itemName + ".json");
                Path targetFile = targetDir.resolve(itemName + ".json");
                
                if (Files.exists(sourceFile)) {
                    // Copy the file directly (synchronously)
                    Files.copy(sourceFile, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.err.println("Warning: GeckoLib item model not found: " + sourceFile);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to copy GeckoLib item models: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return completed future since we're doing synchronous work
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public String getName() {
        return "GeckoLib Item Models - " + modId;
    }
}




