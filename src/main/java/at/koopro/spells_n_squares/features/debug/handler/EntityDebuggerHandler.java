package at.koopro.spells_n_squares.features.debug.handler;

import at.koopro.spells_n_squares.features.debug.DebugConfig;
import at.koopro.spells_n_squares.features.debug.DebugDataFormatter;
import at.koopro.spells_n_squares.features.debug.system.ItemDebuggerManager;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

/**
 * Handles entity debugging functionality.
 * Displays entity data when looking at entities.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class EntityDebuggerHandler {
    
    /**
     * Adds entity debug information when hovering over entities.
     * Note: This is a simplified implementation - full entity tooltip support
     * would require additional client-side entity interaction detection.
     */
    @SubscribeEvent
    public static void onEntityLook(RenderTooltipEvent.Pre event) {
        if (!ItemDebuggerManager.isEnabled()) {
            return;
        }
        
        // Entity debugging would typically require a different approach
        // as tooltips are primarily for items. This is a placeholder for future implementation.
        // Full implementation would require:
        // 1. Entity raycast detection
        // 2. Custom overlay rendering for entity data
        // 3. Or integration with entity nameplate rendering
    }
    
    /**
     * Gets all entity debug data.
     */
    public static List<Component> getEntityData(Entity entity) {
        List<Component> output = new java.util.ArrayList<>();
        
        if (!DebugConfig.shouldShowSection(DebugConfig.SECTION_ITEM_PROPERTIES)) {
            return output;
        }
        
        output.add(Component.literal("§8§m─────────────────"));
        output.add(Component.literal("§6[Entity Debug Info]"));
        output.add(Component.literal(""));
        
        // Basic entity info
        output.add(Component.literal("§7--- Entity Properties ---"));
        output.add(Component.literal("§7Entity Type: §f" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())));
        output.add(Component.literal("§7Entity ID: §f" + entity.getId()));
        output.add(Component.literal("§7UUID: §f" + entity.getUUID()));
        output.add(Component.literal("§7Position: §f" + String.format("%.2f, %.2f, %.2f", 
            entity.getX(), entity.getY(), entity.getZ())));
        output.add(Component.literal("§7Dimension: §f" + entity.level().dimension()));
        output.add(Component.literal("§7Rotation: §f" + String.format("Yaw: %.2f, Pitch: %.2f", 
            entity.getYRot(), entity.getXRot())));
        
        if (entity.getDeltaMovement().lengthSqr() > 0.001) {
            output.add(Component.literal("§7Velocity: §f" + String.format("%.2f, %.2f, %.2f", 
                entity.getDeltaMovement().x, entity.getDeltaMovement().y, entity.getDeltaMovement().z)));
        }
        
        output.add(Component.literal(""));
        
        // Living entity specific
        if (entity instanceof LivingEntity living) {
            output.add(Component.literal("§7--- Living Entity Data ---"));
            output.add(Component.literal("§7Health: §f" + String.format("%.1f / %.1f", 
                living.getHealth(), living.getMaxHealth())));
            output.add(Component.literal("§7Armor: §f" + living.getArmorValue()));
            
            // Status effects
            var effects = living.getActiveEffects();
            if (!effects.isEmpty()) {
                output.add(Component.literal("§7Status Effects: §f" + effects.size()));
            for (var effect : effects) {
                var effectHolder = effect.getEffect();
                String effectName = effectHolder.isBound() ? 
                    BuiltInRegistries.MOB_EFFECT.getKey(effectHolder.value()).toString() : 
                    "unknown";
                int amplifier = effect.getAmplifier();
                int duration = effect.getDuration();
                output.add(Component.literal("§7  §f" + effectName + " §7(Level " + (amplifier + 1) + ", " + (duration / 20) + "s)"));
            }
            }
            
            // Attributes
            if (DebugConfig.shouldShowSection(DebugConfig.SECTION_ATTRIBUTES)) {
                output.add(Component.literal(""));
                output.add(Component.literal("§7--- Attributes ---"));
                
                AttributeInstance health = living.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    output.add(Component.literal("§7  Max Health: §f" + String.format("%.2f", health.getValue())));
                }
                
                AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attack != null) {
                    output.add(Component.literal("§7  Attack Damage: §f" + String.format("%.2f", attack.getValue())));
                }
                
                AttributeInstance speed = living.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null) {
                    output.add(Component.literal("§7  Movement Speed: §f" + String.format("%.2f", speed.getValue())));
                }
            }
            
            // Equipment
            output.add(Component.literal(""));
            output.add(Component.literal("§7--- Equipment ---"));
            for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) {
                ItemStack item = living.getItemBySlot(slot);
                if (!item.isEmpty()) {
                    String itemName = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
                    output.add(Component.literal("§7  " + slot.getName() + ": §f" + itemName + " x" + item.getCount()));
                }
            }
        }
        
        // NBT data
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_NBT_DATA)) {
            output.add(Component.literal(""));
            output.add(Component.literal("§7--- Entity NBT ---"));
            // Note: Entity.saveWithoutId() signature may vary - use fallback
            CompoundTag nbt = new CompoundTag();
            nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
            nbt.putDouble("x", entity.getX());
            nbt.putDouble("y", entity.getY());
            nbt.putDouble("z", entity.getZ());
            if (nbt != null && !nbt.isEmpty()) {
                int[] lineCount = {0};
                DebugDataFormatter.formatNBT(nbt, 0, true, output, lineCount);
            } else {
                output.add(Component.literal("§8No NBT data"));
            }
        }
        
        return output;
    }
    
    /**
     * Formats entity NBT for display.
     */
    public static void formatEntityNBT(Entity entity, List<Component> output) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        nbt.putDouble("x", entity.getX());
        nbt.putDouble("y", entity.getY());
        nbt.putDouble("z", entity.getZ());
        if (nbt != null && !nbt.isEmpty()) {
            int[] lineCount = {0};
            DebugDataFormatter.formatNBT(nbt, 0, true, output, lineCount);
        }
    }
}

