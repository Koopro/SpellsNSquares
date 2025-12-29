package at.koopro.spells_n_squares.features.debug;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.init.client.ModKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * Handles item/block debugging functionality.
 * Displays comprehensive data (NBT, data components, block states, etc.) in tooltips when enabled.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ItemDebuggerHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Handles keybind press to toggle debugger.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Initialize debug config
        DebugConfig.initialize();
        
        // Check if debug keybind is pressed
        if (ModKeybinds.DEBUG_ITEM_TOOLTIP.consumeClick()) {
            boolean newState = ItemDebuggerManager.toggle();
            String message = newState ? "§aItem Debugger Enabled (Press H to toggle)" : "§7Item Debugger Disabled";
            mc.player.displayClientMessage(Component.literal(message), true);
            LOGGER.info("Item Debugger toggled: {}", newState);
        }
        
        // Check filter toggle keybind
        if (ModKeybinds.DEBUG_TOGGLE_FILTER.consumeClick()) {
            DebugConfig.initialize();
            mc.player.displayClientMessage(Component.literal("§eFilter toggle: Use config file to manage filters"), true);
        }
        
        // Check copy to clipboard keybind
        if (ModKeybinds.DEBUG_COPY_CLIPBOARD.consumeClick()) {
            ItemStack heldStack = mc.player.getMainHandItem();
            if (!heldStack.isEmpty()) {
                boolean success = DebugDataExporter.copyToClipboard(heldStack);
                String message = success ? "§aCopied item data to clipboard" : "§cFailed to copy to clipboard";
                mc.player.displayClientMessage(Component.literal(message), true);
            } else {
                mc.player.displayClientMessage(Component.literal("§cNo item in hand to copy"), true);
            }
        }
        
        // Check export to file keybind
        if (ModKeybinds.DEBUG_EXPORT_FILE.consumeClick()) {
            ItemStack heldStack = mc.player.getMainHandItem();
            if (!heldStack.isEmpty()) {
                var path = DebugDataExporter.exportToFile(heldStack, "txt");
                if (path != null) {
                    mc.player.displayClientMessage(Component.literal("§aExported to: " + path), true);
                } else {
                    mc.player.displayClientMessage(Component.literal("§cFailed to export"), true);
                }
            } else {
                mc.player.displayClientMessage(Component.literal("§cNo item in hand to export"), true);
            }
        }
        
        // Check player debug info keybind
        if (ModKeybinds.DEBUG_PLAYER_INFO.consumeClick()) {
            PlayerDebuggerHandler.formatPlayerData(mc.player);
        }
        
        // Check filter toggle keybind
        if (ModKeybinds.DEBUG_TOGGLE_FILTER.consumeClick()) {
            DebugConfig.initialize();
            mc.player.displayClientMessage(Component.literal("§eFilter toggle: Use config file to manage filters"), true);
        }
        
        // Check copy to clipboard keybind
        if (ModKeybinds.DEBUG_COPY_CLIPBOARD.consumeClick()) {
            ItemStack heldStack = mc.player.getMainHandItem();
            if (!heldStack.isEmpty()) {
                boolean success = DebugDataExporter.copyToClipboard(heldStack);
                String message = success ? "§aCopied item data to clipboard" : "§cFailed to copy to clipboard";
                mc.player.displayClientMessage(Component.literal(message), true);
            } else {
                mc.player.displayClientMessage(Component.literal("§cNo item in hand to copy"), true);
            }
        }
        
        // Check export to file keybind
        if (ModKeybinds.DEBUG_EXPORT_FILE.consumeClick()) {
            ItemStack heldStack = mc.player.getMainHandItem();
            if (!heldStack.isEmpty()) {
                var path = DebugDataExporter.exportToFile(heldStack, "txt");
                if (path != null) {
                    mc.player.displayClientMessage(Component.literal("§aExported to: " + path), true);
                } else {
                    mc.player.displayClientMessage(Component.literal("§cFailed to export"), true);
                }
            } else {
                mc.player.displayClientMessage(Component.literal("§cNo item in hand to export"), true);
            }
        }
        
        // Check player debug info keybind
        if (ModKeybinds.DEBUG_PLAYER_INFO.consumeClick()) {
            PlayerDebuggerHandler.formatPlayerData(mc.player);
        }
    }
    
    /**
     * Adds debug information to item tooltips.
     */
    @SubscribeEvent
    public static void onItemTooltip(RenderTooltipEvent.GatherComponents event) {
        if (!ItemDebuggerManager.isEnabled()) {
            return;
        }
        
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        
        // Build debug info as components
        List<Component> debugInfo = new java.util.ArrayList<>();
        debugInfo.add(Component.literal("§8§m─────────────────"));
        debugInfo.add(Component.literal("§6[Debug Info]"));
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_ITEM_PROPERTIES)) {
            addItemProperties(debugInfo, stack);
        }
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_DATA_COMPONENTS)) {
            addDataComponents(debugInfo, stack);
        }
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_ENCHANTMENTS)) {
            DebugDataFormatter.formatEnchantments(stack, debugInfo);
        }
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_ATTRIBUTES)) {
            DebugDataFormatter.formatAttributes(stack, debugInfo);
        }
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_BLOCK_STATES)) {
            addBlockData(debugInfo, stack);
        }
        
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_PERFORMANCE)) {
            addPerformanceMetrics(debugInfo, stack);
        }
        
        // Add each component to the tooltip
        // RenderTooltipEvent.GatherComponents uses Either<FormattedText, TooltipComponent>
        // Component implements FormattedText, so we can use it directly
        var tooltipElements = event.getTooltipElements();
        for (Component component : debugInfo) {
            if (component.getString().isEmpty()) {
                continue; // Skip empty lines
            }
            // Component implements FormattedText, so we can use it directly
            net.minecraft.network.chat.FormattedText formattedText = (net.minecraft.network.chat.FormattedText) component;
            com.mojang.datafixers.util.Either<?, ?> either = com.mojang.datafixers.util.Either.left(formattedText);
            var typedEither = (com.mojang.datafixers.util.Either<?, ?>) either;
            // Use reflection to add to bypass type checking
            try {
                java.lang.reflect.Method addMethod = tooltipElements.getClass().getMethod("add", Object.class);
                addMethod.invoke(tooltipElements, typedEither);
            } catch (Exception e) {
                LOGGER.warn("Failed to add debug tooltip line: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Adds basic item properties to the tooltip.
     */
    private static void addItemProperties(List<Component> tooltip, ItemStack stack) {
        tooltip.add(Component.literal("§7--- Item Properties ---"));
        tooltip.add(Component.literal("§7Count: §f" + stack.getCount()));
        tooltip.add(Component.literal("§7Item ID: §f" + BuiltInRegistries.ITEM.getKey(stack.getItem())));
        
        if (stack.isDamaged()) {
            tooltip.add(Component.literal("§7Damage: §f" + stack.getDamageValue() + " / " + stack.getMaxDamage()));
        }
        
        Component hoverName = stack.getHoverName();
        Component defaultName = stack.getItem().getName(stack);
        if (!hoverName.getString().equals(defaultName.getString())) {
            tooltip.add(Component.literal("§7Custom Name: §f" + hoverName.getString()));
        }
        
        var enchantments = stack.getEnchantments();
        if (!enchantments.isEmpty()) {
            tooltip.add(Component.literal("§7Enchantments: §f" + enchantments.size()));
        }
        
        tooltip.add(Component.literal(""));
    }
    
    /**
     * Adds data components to the tooltip.
     */
    private static void addDataComponents(List<Component> tooltip, ItemStack stack) {
        DataComponentMap components = stack.getComponents();
        if (components.isEmpty()) {
            tooltip.add(Component.literal("§7--- Data Components ---"));
            tooltip.add(Component.literal("§8No data components"));
            tooltip.add(Component.literal(""));
            return;
        }
        
        tooltip.add(Component.literal("§7--- Data Components ---"));
        // Iterate through components using formatter
        for (TypedDataComponent<?> component : components) {
            tooltip.add(DebugDataFormatter.formatDataComponent(component));
        }
        tooltip.add(Component.literal(""));
        
        // Add NBT data if enabled
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_NBT_DATA)) {
            try {
                CompoundTag nbt = new CompoundTag();
                nbt.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                nbt.putInt("count", stack.getCount());
                if (nbt != null && !nbt.isEmpty()) {
                    tooltip.add(Component.literal("§7--- NBT Data ---"));
                    int[] lineCount = {0};
                    DebugDataFormatter.formatNBT(nbt, 0, true, tooltip, lineCount);
                    tooltip.add(Component.literal(""));
                }
            } catch (Exception e) {
                // Ignore NBT errors
            }
        }
    }
    
    /**
     * Adds block-specific data (block states, block entity data) to the tooltip.
     */
    private static void addBlockData(List<Component> tooltip, ItemStack stack) {
        if (!(stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem)) {
            return;
        }
        
        Block block = blockItem.getBlock();
        BlockState defaultState = block.defaultBlockState();
        
        tooltip.add(Component.literal("§7--- Block Data ---"));
        tooltip.add(Component.literal("§7Block ID: §f" + BuiltInRegistries.BLOCK.getKey(block)));
        
        // Block States
        if (!defaultState.getProperties().isEmpty()) {
            tooltip.add(Component.literal("§7Block States:"));
            for (Property<?> property : defaultState.getProperties()) {
                Comparable<?> value = defaultState.getValue(property);
                tooltip.add(Component.literal("§7  " + property.getName() + ": §f" + value));
            }
        } else {
            tooltip.add(Component.literal("§7Block States: §8None"));
        }
        
        // Try to get block entity data if we're looking at a block in-world
        if (DebugConfig.shouldShowSection(DebugConfig.SECTION_BLOCK_ENTITY)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.hitResult != null && mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.world.phys.BlockHitResult hitResult = (net.minecraft.world.phys.BlockHitResult) mc.hitResult;
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = mc.level.getBlockState(pos);
                
                if (state.getBlock() == block) {
                    BlockEntity be = mc.level.getBlockEntity(pos);
                    if (be != null) {
                        tooltip.add(Component.literal("§7Block Entity: §f" + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType())));
                        
                        CompoundTag beTag = be.saveWithFullMetadata(mc.level.registryAccess());
                        if (beTag != null && !beTag.isEmpty()) {
                            if (DebugConfig.shouldShowSection(DebugConfig.SECTION_NBT_DATA)) {
                                tooltip.add(Component.literal("§7Block Entity NBT:"));
                                int[] lineCount = {0};
                                DebugDataFormatter.formatNBT(beTag, 0, true, tooltip, lineCount);
                            }
                        }
                    }
                }
            }
        }
        
        tooltip.add(Component.literal(""));
    }
    
    /**
     * Adds performance metrics to the tooltip.
     */
    private static void addPerformanceMetrics(List<Component> tooltip, ItemStack stack) {
        tooltip.add(Component.literal("§7--- Performance Metrics ---"));
        
        // Data component count
        var components = stack.getComponents();
        tooltip.add(Component.literal("§7Data Components: §f" + components.size()));
        
        // NBT size estimate
        try {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            nbt.putInt("count", stack.getCount());
            int nbtSize = nbt.toString().length();
            tooltip.add(Component.literal("§7NBT Size (est): §f" + nbtSize + " bytes"));
        } catch (Exception e) {
            tooltip.add(Component.literal("§7NBT Size: §8Unable to calculate"));
        }
        
        tooltip.add(Component.literal(""));
    }
}
