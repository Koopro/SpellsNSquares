package at.koopro.spells_n_squares.core.util.debug;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.text.StringUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Utility class for collecting debug data about items, blocks, entities, and players.
 * Used by the debug tooltip system to gather comprehensive information.
 */
public final class DebugDataCollector {
    private DebugDataCollector() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Data structure for item debug information.
     */
    public static class ItemDebugData {
        public final String itemName;
        public final String itemId;
        public final int count;
        public final int damage;
        public final boolean hasNBT;
        public final List<ComponentInfo> dataComponents;
        public final @Nullable CompoundTag nbtData;
        
        public ItemDebugData(String itemName, String itemId, int count, int damage,
                           boolean hasNBT, List<ComponentInfo> dataComponents, @Nullable CompoundTag nbtData) {
            this.itemName = itemName;
            this.itemId = itemId;
            this.count = count;
            this.damage = damage;
            this.hasNBT = hasNBT;
            this.dataComponents = dataComponents;
            this.nbtData = nbtData;
        }
    }
    
    /**
     * Data structure for block debug information.
     */
    public static class BlockDebugData {
        public final String blockName;
        public final String blockId;
        public final BlockPos position;
        public final List<PropertyInfo> properties;
        public final @Nullable BlockEntityInfo blockEntity;
        
        public BlockDebugData(String blockName, String blockId, BlockPos position,
                            List<PropertyInfo> properties, @Nullable BlockEntityInfo blockEntity) {
            this.blockName = blockName;
            this.blockId = blockId;
            this.position = position;
            this.properties = properties;
            this.blockEntity = blockEntity;
        }
    }
    
    /**
     * Data structure for entity debug information.
     */
    public static class EntityDebugData {
        public final String entityType;
        public final String entityId;
        public final Vec3 position;
        public final Vec3 motion;
        public final float rotationYaw;
        public final float rotationPitch;
        public final boolean isAlive;
        public final @Nullable CompoundTag nbtData;
        public final Map<Integer, Object> synchedData;
        
        public EntityDebugData(String entityType, String entityId, Vec3 position,
                             Vec3 motion, float rotationYaw, float rotationPitch, boolean isAlive,
                             @Nullable CompoundTag nbtData, Map<Integer, Object> synchedData) {
            this.entityType = entityType;
            this.entityId = entityId;
            this.position = position;
            this.motion = motion;
            this.rotationYaw = rotationYaw;
            this.rotationPitch = rotationPitch;
            this.isAlive = isAlive;
            this.nbtData = nbtData;
            this.synchedData = synchedData;
        }
    }
    
    /**
     * Data structure for player debug information.
     */
    public static class PlayerDebugData {
        public final String playerName;
        public final Vec3 position;
        public final float health;
        public final float maxHealth;
        public final int foodLevel;
        public final float saturation;
        public final @Nullable CompoundTag playerDataNBT;
        public final List<String> persistentDataKeys;
        
        public PlayerDebugData(String playerName, Vec3 position, float health, float maxHealth,
                             int foodLevel, float saturation, @Nullable CompoundTag playerDataNBT,
                             List<String> persistentDataKeys) {
            this.playerName = playerName;
            this.position = position;
            this.health = health;
            this.maxHealth = maxHealth;
            this.foodLevel = foodLevel;
            this.saturation = saturation;
            this.playerDataNBT = playerDataNBT;
            this.persistentDataKeys = persistentDataKeys;
        }
    }
    
    /**
     * Information about a data component.
     */
    public static class ComponentInfo {
        public final String componentId;
        public final String valueString;
        
        public ComponentInfo(String componentId, String valueString) {
            this.componentId = componentId;
            this.valueString = valueString;
        }
    }
    
    /**
     * Information about a block property.
     */
    public static class PropertyInfo {
        public final String propertyName;
        public final String value;
        
        public PropertyInfo(String propertyName, String value) {
            this.propertyName = propertyName;
            this.value = value;
        }
    }
    
    /**
     * Information about a block entity.
     */
    public static class BlockEntityInfo {
        public final String blockEntityType;
        public final @Nullable CompoundTag nbtData;
        
        public BlockEntityInfo(String blockEntityType, @Nullable CompoundTag nbtData) {
            this.blockEntityType = blockEntityType;
            this.nbtData = nbtData;
        }
    }
    
    /**
     * Collects debug data for an item stack.
     */
    public static ItemDebugData collectItemData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        
        String itemName = stack.getDisplayName().getString();
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int count = stack.getCount();
        int damage = stack.getDamageValue();
        
        // Collect data components
        List<ComponentInfo> components = CollectionFactory.createList();
        DataComponentMap componentMap = stack.getComponents();
        if (componentMap != null) {
            // Iterate over component map
            for (var typedComponent : componentMap) {
                DataComponentType<?> type = typedComponent.type();
                Object value = typedComponent.value();
                String componentId = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type).toString();
                String valueStr = value != null ? value.toString() : "null";
                components.add(new ComponentInfo(componentId, valueStr));
            }
        }
        
        // Get NBT data using codec
        CompoundTag nbt = null;
        try {
            var result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack);
            if (result.result().isPresent()) {
                var tag = result.result().get();
                if (tag instanceof CompoundTag compoundTag) {
                    nbt = compoundTag;
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        boolean hasNBT = nbt != null && !nbt.isEmpty();
        
        return new ItemDebugData(itemName, itemId, count, damage, hasNBT, components, nbt);
    }
    
    /**
     * Collects debug data for a block.
     */
    public static BlockDebugData collectBlockData(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }
        
        BlockState state = level.getBlockState(pos);
        String blockName = state.getBlock().getName().getString();
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        
        // Collect block properties
        List<PropertyInfo> properties = CollectionFactory.createList();
        for (Property<?> prop : state.getProperties()) {
            String value = state.getValue(prop).toString();
            properties.add(new PropertyInfo(prop.getName(), value));
        }
        
        // Get block entity data if present
        BlockEntityInfo blockEntityInfo = null;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            String beType = blockEntity.getClass().getSimpleName();
            // Note: BlockEntity.saveWithId() requires a registry provider, so we'll just note the type
            // For full NBT, we'd need access to the registry provider
            blockEntityInfo = new BlockEntityInfo(beType, null);
        }
        
        return new BlockDebugData(blockName, blockId, pos, properties, blockEntityInfo);
    }
    
    /**
     * Collects debug data for an entity.
     */
    public static EntityDebugData collectEntityData(Entity entity) {
        if (entity == null) {
            return null;
        }
        
        String entityType = entity.getType().getDescription().getString();
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        Vec3 position = entity.position();
        Vec3 motion = entity.getDeltaMovement();
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        boolean isAlive = entity.isAlive();
        
        // Get NBT data - Entity.save() requires ValueOutput, not CompoundTag
        // We'll create a simplified representation
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entityType", entityId);
        nbt.putDouble("x", position.x);
        nbt.putDouble("y", position.y);
        nbt.putDouble("z", position.z);
        
        // Collect synched data (simplified - just count)
        Map<Integer, Object> synchedData = new java.util.HashMap<>();
        // Note: SynchedEntityData is not easily accessible, so we'll just note its presence
        // The actual synched data would require reflection or access to internal fields
        
        return new EntityDebugData(entityType, entityId, position, motion, yaw, pitch, isAlive, nbt, synchedData);
    }
    
    /**
     * Collects debug data for a player.
     */
    public static PlayerDebugData collectPlayerData(Player player) {
        if (player == null) {
            return null;
        }
        
        String playerName = player.getName().getString();
        Vec3 position = player.position();
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int foodLevel = player.getFoodData().getFoodLevel();
        float saturation = player.getFoodData().getSaturationLevel();
        
        // Get player data with full details
        CompoundTag playerDataNBT = new CompoundTag();
        List<String> persistentDataKeys = CollectionFactory.createList();
        
        try {
            var playerData = PlayerDataHelper.get(player);
            if (playerData != null) {
                // Add spell data info
                var spellData = playerData.spells();
                if (spellData != null) {
                    playerDataNBT.putInt("learnedSpellsCount", spellData.learnedSpells() != null ? spellData.learnedSpells().size() : 0);
                    playerDataNBT.putInt("cooldownsCount", spellData.cooldowns() != null ? spellData.cooldowns().size() : 0);
                    if (spellData.activeHoldSpell().isPresent()) {
                        playerDataNBT.putString("activeHoldSpell", spellData.activeHoldSpell().get().toString());
                    }
                    playerDataNBT.putInt("masteryUsesCount", spellData.masteryUses() != null ? spellData.masteryUses().size() : 0);
                    playerDataNBT.putInt("recentCastsCount", spellData.recentCasts() != null ? spellData.recentCasts().size() : 0);
                    
                    // Add slot info
                    var slots = spellData.slots();
                    if (slots != null) {
                        var topSlot = slots.getSpellInSlot(at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_TOP);
                        var bottomSlot = slots.getSpellInSlot(at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_BOTTOM);
                        var leftSlot = slots.getSpellInSlot(at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_LEFT);
                        var rightSlot = slots.getSpellInSlot(at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_RIGHT);
                        
                        playerDataNBT.putString("slotTop", topSlot.isPresent() ? topSlot.get().toString() : "none");
                        playerDataNBT.putString("slotBottom", bottomSlot.isPresent() ? bottomSlot.get().toString() : "none");
                        playerDataNBT.putString("slotLeft", leftSlot.isPresent() ? leftSlot.get().toString() : "none");
                        playerDataNBT.putString("slotRight", rightSlot.isPresent() ? rightSlot.get().toString() : "none");
                    }
                }
                
                // Add wand data info
                var wandData = playerData.wandData();
                if (wandData != null) {
                    playerDataNBT.putString("wandCore", StringUtils.isNotEmpty(wandData.coreId()) ? wandData.coreId() : "none");
                    playerDataNBT.putString("wandWood", StringUtils.isNotEmpty(wandData.woodId()) ? wandData.woodId() : "none");
                    playerDataNBT.putBoolean("wandAttuned", wandData.attuned());
                }
                
                // Add identity data info
                var identity = playerData.identity();
                if (identity != null) {
                    playerDataNBT.putString("bloodStatus", identity.bloodStatus().name());
                    playerDataNBT.putString("magicalType", identity.magicalType().name());
                }
                
                persistentDataKeys.add("Player data loaded");
            }
        } catch (Exception e) {
            // Ignore errors
            playerDataNBT.putString("error", "Failed to load player data: " + e.getMessage());
        }
        
        // Get persistent data info
        var persistentData = player.getPersistentData();
        if (persistentData != null) {
            persistentDataKeys.add("Persistent data container present");
        }
        
        return new PlayerDebugData(playerName, position, health, maxHealth, foodLevel, saturation,
                                 playerDataNBT, persistentDataKeys);
    }
}

