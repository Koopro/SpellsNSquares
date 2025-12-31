package at.koopro.spells_n_squares.features.debug.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.combat.data.CombatStatsData;
import at.koopro.spells_n_squares.features.economy.data.CurrencyData;
import at.koopro.spells_n_squares.features.education.system.HousePointsSystem;
import at.koopro.spells_n_squares.features.debug.DebugConfig;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles player data debugging functionality.
 * Displays comprehensive player information including persistent data components.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class PlayerDebuggerHandler {
    
    /**
     * Handles keybind press to show player debug info.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Check if player debug keybind is pressed (will be added in keybind task)
        // For now, this is a placeholder
    }
    
    /**
     * Gets all player debug data.
     */
    public static List<Component> getPlayerDebugData(Player player) {
        List<Component> output = new ArrayList<>();
        
        output.add(Component.literal("§8§m─────────────────"));
        output.add(Component.literal("§6[Player Debug Info]"));
        output.add(Component.literal(""));
        
        // Basic player info
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_ITEM_PROPERTIES)) {
            output.add(Component.literal("§7--- Player Properties ---"));
            output.add(Component.literal("§7Name: §f" + player.getName().getString()));
            output.add(Component.literal("§7UUID: §f" + player.getUUID()));
            output.add(Component.literal("§7Position: §f" + String.format("%.2f, %.2f, %.2f", 
                player.getX(), player.getY(), player.getZ())));
            output.add(Component.literal("§7Dimension: §f" + player.level().dimension()));
            output.add(Component.literal("§7Game Mode: §f" + (player.isCreative() ? "Creative" : 
                (player.isSpectator() ? "Spectator" : "Survival"))));
            output.add(Component.literal("§7Health: §f" + String.format("%.1f / %.1f", 
                player.getHealth(), player.getMaxHealth())));
            output.add(Component.literal("§7Food Level: §f" + player.getFoodData().getFoodLevel()));
            output.add(Component.literal("§7Experience Level: §f" + player.experienceLevel));
            output.add(Component.literal(""));
        }
        
        // House Points
        try {
            var housePoints = HousePointsSystem.getHousePoints(player);
            if (housePoints != null && !housePoints.points().isEmpty()) {
                output.add(Component.literal("§7--- House Points ---"));
                for (var entry : housePoints.points().entrySet()) {
                    output.add(Component.literal("§7  " + entry.getKey() + ": §f" + entry.getValue()));
                }
                output.add(Component.literal(""));
            }
        } catch (Exception e) {
            // Ignore if house points not available
        }
        
        // Combat Stats
        try {
            var combatStats = CombatStatsData.getCombatStats(player);
            if (combatStats != null) {
                output.add(Component.literal("§7--- Combat Stats ---"));
                output.add(Component.literal("§7Accuracy: §f" + String.format("%.2f%%", combatStats.accuracy() * 100)));
                output.add(Component.literal("§7Dodge Chance: §f" + String.format("%.2f%%", combatStats.dodgeChance() * 100)));
                output.add(Component.literal("§7Critical Hit Chance: §f" + String.format("%.2f%%", combatStats.criticalHitChance() * 100)));
                output.add(Component.literal("§7Spell Resistance: §f" + String.format("%.2f%%", combatStats.spellResistance() * 100)));
                output.add(Component.literal("§7Duels Won: §f" + combatStats.duelsWon()));
                output.add(Component.literal("§7Duels Lost: §f" + combatStats.duelsLost()));
                output.add(Component.literal(""));
            }
        } catch (Exception e) {
            // Ignore if combat stats not available
        }
        
        // Currency
        try {
            var currency = CurrencyData.getCurrencyData(player);
            if (currency != null) {
                output.add(Component.literal("§7--- Currency ---"));
                output.add(Component.literal("§7Galleons: §f" + currency.galleons()));
                output.add(Component.literal("§7Sickles: §f" + currency.sickles()));
                output.add(Component.literal("§7Knuts: §f" + currency.knuts()));
                output.add(Component.literal(""));
            }
        } catch (Exception e) {
            // Ignore if currency not available
        }
        
        // Spell Slots
        output.add(Component.literal("§7--- Spell Slots ---"));
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            Identifier spellId = SpellManager.getSpellInSlot(player, i);
            if (spellId != null) {
                var spell = SpellRegistry.get(spellId);
                String spellName = spell != null ? spell.getName() : spellId.toString();
                output.add(Component.literal("§7  Slot " + i + ": §f" + spellName));
            } else {
                output.add(Component.literal("§7  Slot " + i + ": §8Empty"));
            }
        }
        output.add(Component.literal(""));
        
        // Inventory
        output.add(Component.literal("§7--- Inventory ---"));
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                output.add(Component.literal("§7  Slot " + i + ": §f" + itemName + " x" + stack.getCount()));
            }
        }
        output.add(Component.literal(""));
        
        // Equipment
        output.add(Component.literal("§7--- Equipment ---"));
        for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            ItemStack item = player.getItemBySlot(slot);
            if (!item.isEmpty()) {
                String itemName = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
                output.add(Component.literal("§7  " + slot.getName() + ": §f" + itemName));
            }
        }
        output.add(Component.literal(""));
        
        // Status Effects
        var effects = player.getActiveEffects();
        if (!effects.isEmpty()) {
            output.add(Component.literal("§7--- Status Effects ---"));
            for (var effect : effects) {
                var effectHolder = effect.getEffect();
                String effectName = effectHolder.isBound() ? 
                    BuiltInRegistries.MOB_EFFECT.getKey(effectHolder.value()).toString() : 
                    "unknown";
                int amplifier = effect.getAmplifier();
                int duration = effect.getDuration();
                output.add(Component.literal("§7  §f" + effectName + " §7(Level " + (amplifier + 1) + ", " + (duration / 20) + "s)"));
            }
            output.add(Component.literal(""));
        }
        
        return output;
    }
    
    /**
     * Formats player data for display in chat or GUI.
     */
    public static void formatPlayerData(Player player) {
        List<Component> data = getPlayerDebugData(player);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            for (Component component : data) {
                serverPlayer.sendSystemMessage(component);
            }
        }
    }
}

