package at.koopro.spells_n_squares.features.artifact.events;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the Rubedo stage of the Magnum Opus: Beacon ritual to create Philosopher's Stone.
 * Requires White Stone, Nether Star, and Totem of Undying in beacon beam.
 * 
 * Optimized to track active beacons instead of searching the entire world.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class RitualEvents {
    // Track known beacons per level (BlockPos -> last check time)
    private static final Map<net.minecraft.resources.ResourceKey<Level>, Map<BlockPos, Long>> KNOWN_BEACONS = CollectionFactory.createMap();
    // Track beacons that were recently active (within last 5 seconds)
    private static final Map<net.minecraft.resources.ResourceKey<Level>, Set<BlockPos>> ACTIVE_BEACONS = CollectionFactory.createMap();
    
    // Check frequency: every second (20 ticks)
    private static final int CHECK_INTERVAL = 20;
    // Beacon cache refresh: every 5 seconds (100 ticks)
    private static final int BEACON_CACHE_REFRESH = 100;
    // Inactive beacon timeout: 30 seconds (600 ticks)
    private static final long INACTIVE_TIMEOUT = 600;
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        
        // Optimize: Check every second (20 ticks)
        if (level.getGameTime() % CHECK_INTERVAL != 0) {
            return;
        }
        
        if (level.isClientSide()) {
            return;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            long currentTime = level.getGameTime();
            net.minecraft.resources.ResourceKey<Level> dimensionKey = level.dimension();
            
            // Refresh beacon cache periodically
            if (currentTime % BEACON_CACHE_REFRESH == 0) {
                refreshBeaconCache(serverLevel, dimensionKey, currentTime);
            }
            
            // Get active beacons for this dimension
            Set<BlockPos> activeBeacons = ACTIVE_BEACONS.getOrDefault(dimensionKey, CollectionFactory.createSet());
            if (activeBeacons.isEmpty()) {
                return; // No active beacons, skip search
            }
            
            // Only search for White Stones near active beacons
            for (BlockPos beaconPos : activeBeacons) {
                // Check if beacon is still valid and loaded
                if (!isBeaconValid(level, beaconPos)) {
                    continue;
                }
                
                // Search for White Stones in beacon beam area (up to 50 blocks above beacon)
                BlockPos searchStart = beaconPos.above();
                BlockPos searchEnd = beaconPos.above(50);
                AABB searchBox = new AABB(
                    searchStart.getX() - 1, searchStart.getY(), searchStart.getZ() - 1,
                    searchEnd.getX() + 1, searchEnd.getY(), searchEnd.getZ() + 1
                );
                
                List<ItemEntity> itemsInBeam = level.getEntitiesOfClass(ItemEntity.class, searchBox);
                
                for (ItemEntity whiteStoneEntity : itemsInBeam) {
                    // Check if this is a White Stone
                    if (!whiteStoneEntity.getItem().is(ArtifactRegistry.WHITE_STONE.get())) {
                        continue;
                    }
                    
                    BlockPos pos = whiteStoneEntity.blockPosition();
                    
                    // Verify item is actually in the beam (straight up from beacon)
                    if (!isInBeaconBeam(pos, beaconPos)) {
                        continue;
                    }
                    
                    // Scan for Nether Star and Totem of Undying nearby (2 block radius)
                    AABB nearbyBox = new AABB(pos).inflate(2.0);
                    List<ItemEntity> nearby = level.getEntitiesOfClass(ItemEntity.class, nearbyBox);
                    
                    ItemEntity netherStarEntity = null;
                    ItemEntity totemEntity = null;
                    
                    for (ItemEntity nearbyEntity : nearby) {
                        if (nearbyEntity == whiteStoneEntity) {
                            continue; // Skip the white stone itself
                        }
                        ItemStack nearbyStack = nearbyEntity.getItem();
                        if (nearbyStack.is(Items.NETHER_STAR) && netherStarEntity == null) {
                            netherStarEntity = nearbyEntity;
                        } else if (nearbyStack.is(Items.TOTEM_OF_UNDYING) && totemEntity == null) {
                            totemEntity = nearbyEntity;
                        }
                    }
                    
                    if (netherStarEntity != null && totemEntity != null) {
                        // All ingredients present - perform ritual
                        performRubedoRitual(serverLevel, pos, whiteStoneEntity, netherStarEntity, totemEntity);
                        return; // Only process one ritual per tick
                    }
                }
            }
        }, "checking ritual events", "level " + event.getLevel().dimension());
    }
    
    /**
     * Refreshes the beacon cache by scanning loaded chunks for beacons.
     */
    private static void refreshBeaconCache(ServerLevel level, net.minecraft.resources.ResourceKey<Level> dimensionKey, long currentTime) {
        Map<BlockPos, Long> knownBeacons = KNOWN_BEACONS.computeIfAbsent(dimensionKey, k -> CollectionFactory.createMap());
        Set<BlockPos> activeBeacons = ACTIVE_BEACONS.computeIfAbsent(dimensionKey, k -> CollectionFactory.createSet());
        
        // Remove inactive beacons
        knownBeacons.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > INACTIVE_TIMEOUT) {
                activeBeacons.remove(entry.getKey());
                return true;
            }
            return false;
        });
        
        // Scan loaded chunks for beacons
        // Iterate through chunk positions in a reasonable area around players
        // This is more efficient than scanning all chunks
        int chunkRadius = 8; // Check chunks within 8 chunks of any player
        for (net.minecraft.server.level.ServerPlayer player : level.players()) {
            BlockPos playerPos = player.blockPosition();
            int chunkX = playerPos.getX() >> 4;
            int chunkZ = playerPos.getZ() >> 4;
            
            for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                    int checkChunkX = chunkX + dx;
                    int checkChunkZ = chunkZ + dz;
                    
                    if (level.hasChunk(checkChunkX, checkChunkZ)) {
                        LevelChunk chunk = level.getChunk(checkChunkX, checkChunkZ);
                        if (chunk != null) {
                            // Check chunk for beacon block entities
                            chunk.getBlockEntities().forEach((pos, blockEntity) -> {
                                if (blockEntity instanceof BeaconBlockEntity && isBeaconValid(level, pos)) {
                                    knownBeacons.put(pos, currentTime);
                                    activeBeacons.add(pos);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Checks if a beacon is valid (exists and has active beam).
     */
    private static boolean isBeaconValid(Level level, BlockPos beaconPos) {
        if (!level.isLoaded(beaconPos)) {
            return false;
        }
        if (!level.getBlockState(beaconPos).is(Blocks.BEACON)) {
            return false;
        }
        return level.getBlockEntity(beaconPos) instanceof BeaconBlockEntity;
    }
    
    /**
     * Checks if a position is in a beacon's beam (straight up from beacon).
     */
    private static boolean isInBeaconBeam(BlockPos pos, BlockPos beaconPos) {
        int dx = Math.abs(pos.getX() - beaconPos.getX());
        int dz = Math.abs(pos.getZ() - beaconPos.getZ());
        return dx <= 1 && dz <= 1 && pos.getY() > beaconPos.getY();
    }
    
    /**
     * Performs the Rubedo ritual: merges items and creates Philosopher's Stone.
     */
    private static void performRubedoRitual(ServerLevel level, BlockPos pos,
                                     ItemEntity whiteStoneEntity,
                                     ItemEntity netherStarEntity,
                                     ItemEntity totemEntity) {
        // Consume ingredients
        whiteStoneEntity.getItem().shrink(1);
        if (whiteStoneEntity.getItem().isEmpty()) {
            whiteStoneEntity.discard();
        }
        
        netherStarEntity.getItem().shrink(1);
        if (netherStarEntity.getItem().isEmpty()) {
            netherStarEntity.discard();
        }
        
        totemEntity.getItem().shrink(1);
        if (totemEntity.getItem().isEmpty()) {
            totemEntity.discard();
        }
        
        // Create Philosopher's Stone
        ItemStack stone = new ItemStack(ArtifactRegistry.PHILOSOPHERS_STONE.get());
        ItemEntity stoneEntity = new ItemEntity(level,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            stone);
        stoneEntity.setDefaultPickUpDelay();
        level.addFreshEntity(stoneEntity);
        
        // Lightning strike (visual only - no damage, no fire)
        net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(
            level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (lightning != null) {
            lightning.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            lightning.setVisualOnly(true); // Make it visual only - no damage, no fire
            level.addFreshEntity(lightning);
        }
        
        // Visual effects
        level.sendParticles(ParticleTypes.FLAME,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            50, 0.5, 0.5, 0.5, 0.1);
        level.sendParticles(ParticleTypes.ENCHANT,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            100, 0.5, 0.5, 0.5, 0.1);
        level.sendParticles(ParticleTypes.END_ROD,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            30, 0.3, 0.3, 0.3, 0.05);
        
        // Sound effects
        level.playSound(null, pos,
            net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER,
            net.minecraft.sounds.SoundSource.WEATHER, 1.0f, 1.0f);
        level.playSound(null, pos,
            net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,
            net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 2.0f);
    }
}

