package at.koopro.spells_n_squares.features.environment;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

/**
 * Handles creature migrations - rare creature appearance events.
 * Migrations occur randomly and spawn rare creatures in the world.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class CreatureMigrationHandler {
    
    // Track active migrations per level
    private static final Map<ServerLevel, MigrationEvent> activeMigrations = new HashMap<>();
    
    // Chance of migration spawning per tick (1 in 50000 = very rare)
    private static final int MIGRATION_SPAWN_CHANCE = 50000;
    
    // Migration duration in ticks (10 minutes)
    private static final int MIGRATION_DURATION = 12000;
    
    // Rare creatures that can appear in migrations
    // TODO: Re-enable when entity classes are implemented
    private static final List<MigrationCreature> RARE_CREATURES = List.of(
        // new MigrationCreature(ModEntities.PHOENIX, "Phoenix", 0.1f, 1, 2), // Very rare, 1-2 spawn
        // new MigrationCreature(ModEntities.THUNDERBIRD, "Thunderbird", 0.15f, 1, 2), // Rare, 1-2 spawn
        // new MigrationCreature(ModEntities.GRAPHORN, "Graphorn", 0.2f, 1, 3), // Rare, 1-3 spawn
        // new MigrationCreature(ModEntities.ZOUWU, "Zouwu", 0.2f, 1, 3), // Rare, 1-3 spawn
        // new MigrationCreature(ModEntities.OCCAMY, "Occamy", 0.25f, 2, 4), // Uncommon, 2-4 spawn
        // new MigrationCreature(ModEntities.NIFFLER, "Niffler", 0.3f, 3, 6), // Common, 3-6 spawn
        // new MigrationCreature(ModEntities.MOONCALF, "Mooncalf", 0.3f, 3, 6), // Common, 3-6 spawn
        // new MigrationCreature(ModEntities.KNEAZLE, "Kneazle", 0.25f, 2, 5) // Uncommon, 2-5 spawn
    );
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Update existing migrations
        MigrationEvent migration = activeMigrations.get(serverLevel);
        if (migration != null) {
            if (migration.tick(serverLevel)) {
                // Migration still active
                return;
            } else {
                // Migration ended
                activeMigrations.remove(serverLevel);
                return;
            }
        }
        
        // Try to spawn new migration
        if (serverLevel.getRandom().nextInt(MIGRATION_SPAWN_CHANCE) == 0) {
            spawnMigration(serverLevel);
        }
    }
    
    /**
     * Spawns a creature migration event in the level.
     */
    public static void spawnMigration(ServerLevel level) {
        if (activeMigrations.containsKey(level)) {
            return; // Already has an active migration
        }
        
        // Select a random rare creature
        MigrationCreature selectedCreature = selectRandomCreature(level.getRandom());
        if (selectedCreature == null) {
            return;
        }
        
        MigrationEvent migration = new MigrationEvent(selectedCreature, level.getRandom());
        activeMigrations.put(level, migration);
        
        // Spawn creatures near players
        spawnCreaturesForMigration(level, selectedCreature);
        
        // Notify players
        for (Player player : level.players()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.migration.started", 
                    selectedCreature.name()));
            }
        }
        
        // Play sound near players
        if (!level.players().isEmpty()) {
            Player firstPlayer = level.players().get(0);
            level.playSound(null, firstPlayer.getX(), 200, firstPlayer.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.3f, 1.0f);
        }
    }
    
    /**
     * Selects a random creature based on spawn weights.
     */
    private static MigrationCreature selectRandomCreature(net.minecraft.util.RandomSource random) {
        float roll = random.nextFloat();
        float cumulative = 0.0f;
        
        for (MigrationCreature creature : RARE_CREATURES) {
            cumulative += creature.weight();
            if (roll <= cumulative) {
                return creature;
            }
        }
        
        // Fallback to last creature
        return RARE_CREATURES.get(RARE_CREATURES.size() - 1);
    }
    
    /**
     * Spawns creatures for a migration event.
     */
    private static void spawnCreaturesForMigration(ServerLevel level, MigrationCreature creature) {
        int count = creature.minSpawn() + level.getRandom().nextInt(creature.maxSpawn() - creature.minSpawn() + 1);
        
        // Spawn near players
        for (Player player : level.players()) {
            if (level.getRandom().nextFloat() < 0.5f) { // 50% chance per player
                double x = player.getX() + (level.getRandom().nextDouble() - 0.5) * 30.0;
                double y = player.getY();
                double z = player.getZ() + (level.getRandom().nextDouble() - 0.5) * 30.0;
                
                // Find a safe spawn position
                net.minecraft.core.BlockPos spawnPos = new net.minecraft.core.BlockPos(
                    (int) x, (int) y, (int) z);
                spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, spawnPos);
                
                net.minecraft.world.entity.Entity entity = creature.entityType().get().create(level, net.minecraft.world.entity.EntitySpawnReason.NATURAL);
                if (entity != null) {
                    entity.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    level.addFreshEntity(entity);
                    
                    // Visual effect
                    level.sendParticles(ParticleTypes.ENCHANT,
                        spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                        20, 1.0, 1.0, 1.0, 0.1);
                    
                    level.playSound(null, spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.2f);
                }
            }
        }
    }
    
    /**
     * Represents an active migration event.
     */
    private static class MigrationEvent {
        private int remainingTicks;
        private final MigrationCreature creature;
        
        public MigrationEvent(MigrationCreature creature, net.minecraft.util.RandomSource random) {
            this.remainingTicks = MIGRATION_DURATION;
            this.creature = creature;
        }
        
        /**
         * Ticks the migration event.
         * @return true if migration is still active, false if it ended
         */
        public boolean tick(ServerLevel level) {
            if (remainingTicks <= 0) {
                return false;
            }
            
            remainingTicks--;
            
            // Spawn additional creatures occasionally during migration
            if (remainingTicks % 2000 == 0 && level.getRandom().nextFloat() < 0.3f) {
                spawnCreaturesForMigration(level, creature);
            }
            
            return true;
        }
    }
    
    /**
     * Represents a creature that can appear in migrations.
     */
    private record MigrationCreature(
        net.neoforged.neoforge.registries.DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>> entityType,
        String name,
        float weight,
        int minSpawn,
        int maxSpawn
    ) {}
    
    /**
     * Manually triggers a migration event (for testing or special events).
     */
    public static void triggerMigration(ServerLevel level) {
        spawnMigration(level);
    }
    
    /**
     * Manually triggers a migration for a specific creature type.
     */
    public static void triggerMigration(ServerLevel level, String creatureName) {
        for (MigrationCreature creature : RARE_CREATURES) {
            if (creature.name().equalsIgnoreCase(creatureName)) {
                MigrationEvent migration = new MigrationEvent(creature, level.getRandom());
                activeMigrations.put(level, migration);
                spawnCreaturesForMigration(level, creature);
                return;
            }
        }
    }
}




