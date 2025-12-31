package at.koopro.spells_n_squares.features.storage.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing pocket dimension information.
 */
public final class PocketDimensionData {
    private PocketDimensionData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PocketDimensionComponent>> POCKET_DIMENSION =
        DATA_COMPONENTS.register("pocket_dimension", () -> DataComponentType.<PocketDimensionComponent>builder()
            .persistent(PocketDimensionComponent.CODEC)
            .build());
    
    /**
     * Type of pocket dimension.
     */
    public enum DimensionType {
        STANDARD,
        NEWTS_CASE
    }
    
    /**
     * Component storing pocket dimension reference.
     */
    public record PocketDimensionComponent(
        ResourceKey<Level> dimensionKey,
        int size,
        UUID dimensionId,
        DimensionType type,
        Optional<ResourceKey<Level>> entryDimension,
        Optional<BlockPos> entryPosition,
        int upgradeLevel,
        boolean isLocked,
        List<UUID> whitelistedPlayers
    ) {
        public static final Codec<PocketDimensionComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PocketDimensionComponent::dimensionKey),
                Codec.INT.fieldOf("size").forGetter(PocketDimensionComponent::size),
                UUIDUtil.CODEC.fieldOf("dimensionId").forGetter(PocketDimensionComponent::dimensionId),
                Codec.STRING.xmap(
                    s -> DimensionType.valueOf(s.toUpperCase()),
                    t -> t.name().toLowerCase()
                ).optionalFieldOf("type", DimensionType.STANDARD).forGetter(PocketDimensionComponent::type),
                ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("entryDimension").forGetter(PocketDimensionComponent::entryDimension),
                BlockPos.CODEC.optionalFieldOf("entryPosition").forGetter(PocketDimensionComponent::entryPosition),
                Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(PocketDimensionComponent::upgradeLevel),
                Codec.BOOL.optionalFieldOf("isLocked", false).forGetter(PocketDimensionComponent::isLocked),
                UUIDUtil.CODEC.listOf().optionalFieldOf("whitelistedPlayers", List.of()).forGetter(PocketDimensionComponent::whitelistedPlayers)
            ).apply(instance, PocketDimensionComponent::new)
        );
        
        public static PocketDimensionComponent createDefault(int size) {
            return createDefault(size, DimensionType.STANDARD);
        }
        
        public static PocketDimensionComponent createDefault(int size, DimensionType type) {
            // Generate unique ID for this pocket dimension
            UUID dimensionId = UUID.randomUUID();
            ResourceKey<Level> dimensionKey = PocketDimensionManager.getOrCreateDimensionKey(dimensionId, type);
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.empty(),
                Optional.empty(),
                0, // upgradeLevel
                false, // isLocked
                List.of() // whitelistedPlayers
            );
        }
        
        public PocketDimensionComponent withEntry(ResourceKey<Level> entryDimension, BlockPos entryPosition) {
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.of(entryDimension),
                Optional.of(entryPosition),
                upgradeLevel,
                isLocked,
                whitelistedPlayers
            );
        }
        
        public PocketDimensionComponent clearEntry() {
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                Optional.empty(),
                Optional.empty(),
                upgradeLevel,
                isLocked,
                whitelistedPlayers
            );
        }
        
        /**
         * Upgrades the dimension, increasing size and unlocking features.
         */
        public PocketDimensionComponent upgrade() {
            int newSize = size + 8; // Increase size by 8 blocks per upgrade
            int newUpgradeLevel = upgradeLevel + 1;
            return new PocketDimensionComponent(
                dimensionKey,
                newSize,
                dimensionId,
                type,
                entryDimension,
                entryPosition,
                newUpgradeLevel,
                isLocked,
                whitelistedPlayers
            );
        }
        
        /**
         * Sets the lock status of the dimension.
         */
        public PocketDimensionComponent setLocked(boolean locked) {
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                entryDimension,
                entryPosition,
                upgradeLevel,
                locked,
                whitelistedPlayers
            );
        }
        
        /**
         * Adds a player to the whitelist.
         */
        public PocketDimensionComponent addWhitelistedPlayer(UUID playerUuid) {
            if (whitelistedPlayers.contains(playerUuid)) {
                return this; // Already whitelisted
            }
            List<UUID> newWhitelist = new java.util.ArrayList<>(whitelistedPlayers);
            newWhitelist.add(playerUuid);
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                entryDimension,
                entryPosition,
                upgradeLevel,
                isLocked,
                newWhitelist
            );
        }
        
        /**
         * Removes a player from the whitelist.
         */
        public PocketDimensionComponent removeWhitelistedPlayer(UUID playerUuid) {
            if (!whitelistedPlayers.contains(playerUuid)) {
                return this; // Not whitelisted
            }
            List<UUID> newWhitelist = new java.util.ArrayList<>(whitelistedPlayers);
            newWhitelist.remove(playerUuid);
            return new PocketDimensionComponent(
                dimensionKey,
                size,
                dimensionId,
                type,
                entryDimension,
                entryPosition,
                upgradeLevel,
                isLocked,
                newWhitelist
            );
        }
        
        /**
         * Checks if a player has access to this dimension.
         */
        public boolean hasAccess(UUID playerUuid) {
            if (!isLocked) {
                return true; // Unlocked - everyone has access
            }
            return whitelistedPlayers.contains(playerUuid);
        }
        
        /**
         * Creates a new Newt's Case pocket dimension component.
         */
        public static PocketDimensionComponent createNewtsCase(int size) {
            return createDefault(size, DimensionType.NEWTS_CASE);
        }
    }
}

