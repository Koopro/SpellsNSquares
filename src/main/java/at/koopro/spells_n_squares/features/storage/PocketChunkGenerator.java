package at.koopro.spells_n_squares.features.storage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.biome.BiomeManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Chunk generator for pocket dimensions.
 * Creates a void-like space with optional platforms.
 */
public class PocketChunkGenerator extends ChunkGenerator {
    public static final MapCodec<PocketChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
        ).apply(instance, PocketChunkGenerator::new)
    );
    
    public PocketChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }
    
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    
    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {
        // No carvers for pocket dimensions
    }
    
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        // Minimal surface generation - mostly void
    }
    
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No mob spawning in pocket dimensions
    }
    
    @Override
    public int getGenDepth() {
        return 256;
    }
    
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        // Generate void chunks - platforms will be created manually by PocketDimensionManager
        return CompletableFuture.completedFuture(chunk);
    }
    
    @Override
    public int getSeaLevel() {
        return 63;
    }
    
    @Override
    public int getMinY() {
        return 0;
    }
    
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType, LevelHeightAccessor level, RandomState randomState) {
        return 64; // Default height for platforms
    }
    
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        // Return mostly air with bedrock at bottom
        BlockState[] states = new BlockState[level.getHeight()];
        for (int y = 0; y < level.getHeight(); y++) {
            if (y == 0) {
                states[y] = Blocks.BEDROCK.defaultBlockState();
            } else {
                states[y] = Blocks.AIR.defaultBlockState();
            }
        }
        return new NoiseColumn(level.getMinY(), states);
    }
    
    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("Pocket Dimension");
    }
}
