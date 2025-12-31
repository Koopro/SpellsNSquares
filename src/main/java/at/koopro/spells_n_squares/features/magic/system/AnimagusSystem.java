package at.koopro.spells_n_squares.features.magic.system;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Manages Animagus transformations for players.
 * Handles registration, form selection, and transformation mechanics.
 */
public final class AnimagusSystem {
    private AnimagusSystem() {
    }
    
    // Active animal entities (player UUID -> animal entity) - in-memory only
    private static final Map<UUID, Entity> activeAnimalEntities = new HashMap<>();
    
    /**
     * Represents an Animagus form.
     */
    public record AnimagusForm(
        AnimalForm form,
        long registrationDate,
        String registrationMethod // "spell", "item", "command", etc.
    ) {}
    
    /**
     * Available animal forms for Animagus transformation.
     */
    public enum AnimalForm {
        CAT("Cat", "Transforms into a cat"),
        DOG("Dog", "Transforms into a dog"),
        BIRD("Bird", "Transforms into a bird"),
        RAT("Rat", "Transforms into a rat"),
        STAG("Stag", "Transforms into a stag"),
        OTTER("Otter", "Transforms into an otter");
        
        private final String displayName;
        private final String description;
        
        AnimalForm(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Registers a player as an Animagus.
     */
    public static boolean registerAnimagus(Player player, AnimalForm form, String registrationMethod) {
        if (player == null || form == null || player.level().isClientSide()) {
            return false;
        }
        
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData current = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        
        if (current.isRegistered()) {
            return false; // Already registered
        }
        
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData updated = new at.koopro.spells_n_squares.modules.magic.internal.AnimagusData(
            form.name().toLowerCase(),
            System.currentTimeMillis(),
            registrationMethod,
            false
        );
        
        at.koopro.spells_n_squares.core.data.PlayerDataHelper.setAnimagusData(player, updated);
        return true;
    }
    
    /**
     * Checks if a player is a registered Animagus.
     */
    public static boolean isAnimagus(Player player) {
        if (player == null) {
            return false;
        }
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        return data.isRegistered();
    }
    
    /**
     * Gets the Animagus form for a player.
     */
    public static AnimagusForm getAnimagusForm(Player player) {
        if (player == null) {
            return null;
        }
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        if (!data.isRegistered()) {
            return null;
        }
        
        // Convert string to AnimalForm enum
        try {
            AnimalForm form = AnimalForm.valueOf(data.animalForm().toUpperCase());
            return new AnimagusForm(form, data.registrationDate(), data.registrationMethod());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Transforms a player into their Animagus form.
     */
    public static boolean transform(ServerPlayer player) {
        if (!isAnimagus(player)) {
            return false;
        }
        
        AnimagusForm form = getAnimagusForm(player);
        if (form == null) {
            return false;
        }
        
        UUID playerId = player.getUUID();
        
        // Check if already transformed
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        if (data.isTransformed()) {
            return false;
        }
        
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        // Get entity type for the animal form
        EntityType<?> entityType = getEntityTypeForForm(form.form());
        if (entityType == null) {
            return false;
        }
        
        // Spawn animal entity at player position
        Entity animalEntity = entityType.create(serverLevel, EntitySpawnReason.COMMAND);
        if (animalEntity == null) {
            return false;
        }
        
        animalEntity.setPos(player.getX(), player.getY(), player.getZ());
        animalEntity.setYRot(player.getYRot());
        animalEntity.setXRot(player.getXRot());
        
        // Make animal entity tameable and set owner if it's a tamable animal
        if (animalEntity instanceof TamableAnimal tamable) {
            tamable.tame(player);
            tamable.setTame(true, true);
            tamable.setOrderedToSit(false);
        }
        
        // Set custom name to indicate this is a transformed player
        animalEntity.setCustomName(Component.translatable("entity.spells_n_squares.animagus", player.getName()));
        animalEntity.setCustomNameVisible(true);
        
        // Add entity to level
        serverLevel.addFreshEntity(animalEntity);
        
        // Hide player
        player.setInvisible(true);
        player.setNoGravity(false); // Ensure gravity is normal
        
        // Store transformation state
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData currentData = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData updatedData = currentData.withTransformed(true);
        at.koopro.spells_n_squares.core.data.PlayerDataHelper.setAnimagusData(player, updatedData);
        activeAnimalEntities.put(playerId, animalEntity);
        
        // Send notification
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.animagus.transformed", form.form().getDisplayName()));
        
        return true;
    }
    
    /**
     * Reverts a player from their Animagus form.
     */
    public static boolean revert(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        if (!data.isTransformed()) {
            return false; // Not transformed
        }
        
        // Get and remove animal entity
        Entity animalEntity = activeAnimalEntities.remove(playerId);
        if (animalEntity != null && animalEntity.isAlive()) {
            // Remove entity from world
            animalEntity.remove(Entity.RemovalReason.DISCARDED);
        }
        
        // Show player
        player.setInvisible(false);
        
        // Clear transformation state
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData currentData = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData updatedData = currentData.withTransformed(false);
        at.koopro.spells_n_squares.core.data.PlayerDataHelper.setAnimagusData(player, updatedData);
        
        // Send notification
        AnimagusForm form = getAnimagusForm(player);
        if (form != null) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.animagus.reverted", form.form().getDisplayName()));
        }
        
        return true;
    }
    
    /**
     * Gets the entity type for an animal form.
     */
    private static EntityType<?> getEntityTypeForForm(AnimalForm form) {
        return switch (form) {
            case CAT -> EntityType.CAT;
            case DOG -> EntityType.WOLF; // Wolf is used for dog
            case BIRD -> EntityType.PARROT;
            case RAT -> EntityType.RABBIT; // Using rabbit as closest approximation
            case STAG -> EntityType.HORSE; // Using horse as closest approximation
            case OTTER -> EntityType.RABBIT; // Using rabbit as closest approximation for otter
        };
    }
    
    /**
     * Checks if a player is currently transformed.
     */
    public static boolean isTransformed(Player player) {
        if (player == null) {
            return false;
        }
        at.koopro.spells_n_squares.modules.magic.internal.AnimagusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getAnimagusData(player);
        return data.isTransformed();
    }
    
    /**
     * Clears Animagus data for a player (on disconnect).
     * Only clears in-memory entity references, persistent data remains.
     */
    public static void clearPlayerData(Player player) {
        UUID playerId = player.getUUID();
        
        // Revert transformation if active
        if (isTransformed(player)) {
            net.minecraft.server.level.ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
            if (serverPlayer != null) {
                revert(serverPlayer);
            }
        }
        
        activeAnimalEntities.remove(playerId);
    }
    
    /**
     * Gets the active animal entity for a transformed player.
     */
    public static Entity getActiveAnimalEntity(Player player) {
        return activeAnimalEntities.get(player.getUUID());
    }
}
















