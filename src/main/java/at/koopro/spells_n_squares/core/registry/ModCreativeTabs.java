package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.block.tree.TreeBlockSet;
import at.koopro.spells_n_squares.core.registry.ModTreeBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod creative mode tabs.
 */
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpellsNSquares.MODID);
    
    // Main tab - general items
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPELLS_N_SQUARES_TAB = CREATIVE_TABS.register(
        "spells_n_squares_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares"))
            .icon(() -> new ItemStack(ModItems.RUBBER_DUCK.get()))
            .displayItems((parameters, output) -> {
                // Add all mod items
                ModItems.ITEMS.getEntries().forEach(holder -> {
                    Item item = holder.get();
                    if (item != null && item != net.minecraft.world.item.Items.AIR) {
                        output.accept(item);
                    }
                });
            })
            .build()
    );
    
    // Wands and spells tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WANDS_SPELLS_TAB = CREATIVE_TABS.register(
        "wands_spells",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.wands_spells"))
            .icon(() -> new ItemStack(ModItems.DEMO_WAND.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.DEMO_WAND.get());
                // Add other wand/spell related items here
            })
            .build()
    );
    
    // Economy tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ECONOMY_TAB = CREATIVE_TABS.register(
        "economy",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.economy"))
            .icon(() -> new ItemStack(ModBlocks.TRADING_POST.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.TRADING_POST.get());
                output.accept(ModBlocks.AUTOMATED_SHOP.get());
                output.accept(ModBlocks.VAULT.get());
            })
            .build()
    );
    
    // Quality of life tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUALITY_OF_LIFE_TAB = CREATIVE_TABS.register(
        "quality_of_life",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.quality_of_life"))
            .icon(() -> new ItemStack(ModItems.FLASHLIGHT.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.RUBBER_DUCK.get());
                output.accept(ModItems.FLASHLIGHT.get());
            })
            .build()
    );
    
    // Transportation tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRANSPORTATION_TAB = CREATIVE_TABS.register(
        "transportation",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.transportation"))
            .icon(() -> new ItemStack(ModItems.BROOMSTICK_BASIC.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.PORTKEY.get());
                output.accept(ModItems.FLOO_POWDER.get());
                output.accept(ModItems.BROOMSTICK_BASIC.get());
                output.accept(ModItems.BROOMSTICK_RACING.get());
                output.accept(ModItems.BROOMSTICK_FIREBOLT.get());
            })
            .build()
    );
    
    // Storage tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STORAGE_TAB = CREATIVE_TABS.register(
        "storage",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.storage"))
            .icon(() -> new ItemStack(ModBlocks.MAGICAL_TRUNK.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.MAGICAL_TRUNK.get());
                output.accept(ModBlocks.AUTO_SORT_CHEST.get());
                output.accept(ModItems.ENCHANTED_BAG_SMALL.get());
                output.accept(ModItems.ENCHANTED_BAG_MEDIUM.get());
                output.accept(ModItems.ENCHANTED_BAG_LARGE.get());
                output.accept(ModItems.ENCHANTED_BAG_BOTTOMLESS.get());
                output.accept(ModItems.POCKET_DIMENSION.get());
            })
            .build()
    );
    
    // Building tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_TAB = CREATIVE_TABS.register(
        "building",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.building"))
            .icon(() -> new ItemStack(ModBlocks.MAGICAL_LIGHT_WHITE.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.MAGICAL_LIGHT_WHITE.get());
                output.accept(ModBlocks.MAGICAL_LIGHT_BLUE.get());
                output.accept(ModBlocks.MAGICAL_LIGHT_GREEN.get());
                output.accept(ModBlocks.MAGICAL_LIGHT_RED.get());
                output.accept(ModBlocks.MAGICAL_LIGHT_PURPLE.get());
                output.accept(ModBlocks.MAGICAL_LIGHT_GOLD.get());
                output.accept(ModItems.WIZARD_TOWER.get());
                
                // Add all tree blocks
                for (TreeBlockSet set : ModTreeBlocks.getAllTreeSets()) {
                    for (DeferredHolder<Block, ? extends Block> holder : set.getAllBlocks()) {
                        Block block = holder.get();
                        Item blockItem = block.asItem();
                        if (blockItem != null && blockItem != net.minecraft.world.item.Items.AIR) {
                            output.accept(blockItem);
                        }
                    }
                }
            })
            .build()
    );
    
    // Automation tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AUTOMATION_TAB = CREATIVE_TABS.register(
        "automation",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.automation"))
            .icon(() -> new ItemStack(ModBlocks.SELF_STIRRING_CAULDRON.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.SELF_STIRRING_CAULDRON.get());
                output.accept(ModBlocks.MAGICAL_FURNACE.get());
                output.accept(ModBlocks.MAGICAL_FARM.get());
                output.accept(ModBlocks.ITEM_COLLECTOR.get());
                output.accept(ModBlocks.MAGICAL_COMPOSTER.get());
                output.accept(ModBlocks.RESOURCE_GENERATOR.get());
                output.accept(ModItems.ENCHANTED_WORKBENCH.get());
                output.accept(ModItems.AUTO_HARVEST_HOE.get());
            })
            .build()
    );
    
    // Communication tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COMMUNICATION_TAB = CREATIVE_TABS.register(
        "communication",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.communication"))
            .icon(() -> new ItemStack(ModBlocks.NOTICE_BOARD.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.NOTICE_BOARD.get());
                output.accept(ModItems.TWO_WAY_MIRROR.get());
            })
            .build()
    );
    
    // Navigation tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NAVIGATION_TAB = CREATIVE_TABS.register(
        "navigation",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.navigation"))
            .icon(() -> new ItemStack(ModItems.MAGICAL_MAP.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MAGICAL_MAP.get());
                output.accept(ModItems.LOCATION_COMPASS.get());
                output.accept(ModItems.MAGICAL_JOURNAL.get());
            })
            .build()
    );
    
    // Education and combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EDUCATION_COMBAT_TAB = CREATIVE_TABS.register(
        "education_combat",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.education_combat"))
            .icon(() -> {
                Item hourglassItem = ModBlocks.HOUSE_POINTS_HOURGLASS.get().asItem();
                return hourglassItem != null && hourglassItem != net.minecraft.world.item.Items.AIR 
                    ? new ItemStack(hourglassItem) 
                    : new ItemStack(ModBlocks.DUEL_ARENA.get().asItem());
            })
            .displayItems((parameters, output) -> {
                Item hourglassItem = ModBlocks.HOUSE_POINTS_HOURGLASS.get().asItem();
                if (hourglassItem != null && hourglassItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(hourglassItem);
                }
                Item duelArenaItem = ModBlocks.DUEL_ARENA.get().asItem();
                if (duelArenaItem != null && duelArenaItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(duelArenaItem);
                }
                Item noticeBoardItem = ModBlocks.NOTICE_BOARD.get().asItem();
                if (noticeBoardItem != null && noticeBoardItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(noticeBoardItem);
                }
                Item enchantmentTableItem = ModBlocks.ENCHANTMENT_TABLE.get().asItem();
                if (enchantmentTableItem != null && enchantmentTableItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(enchantmentTableItem);
                }
            })
            .build()
    );
    
    // Robes tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ROBES_TAB = CREATIVE_TABS.register(
        "robes",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.robes"))
            .icon(() -> new ItemStack(ModItems.GRYFFINDOR_ROBE_CHEST.get()))
            .displayItems((parameters, output) -> {
                // Gryffindor robes
                output.accept(ModItems.GRYFFINDOR_ROBE_CHEST.get());
                output.accept(ModItems.GRYFFINDOR_ROBE_LEGS.get());
                output.accept(ModItems.GRYFFINDOR_ROBE_BOOTS.get());
                
                // Slytherin robes
                output.accept(ModItems.SLYTHERIN_ROBE_CHEST.get());
                output.accept(ModItems.SLYTHERIN_ROBE_LEGS.get());
                output.accept(ModItems.SLYTHERIN_ROBE_BOOTS.get());
                
                // Hufflepuff robes
                output.accept(ModItems.HUFFLEPUFF_ROBE_CHEST.get());
                output.accept(ModItems.HUFFLEPUFF_ROBE_LEGS.get());
                output.accept(ModItems.HUFFLEPUFF_ROBE_BOOTS.get());
                
                // Ravenclaw robes
                output.accept(ModItems.RAVENCLAW_ROBE_CHEST.get());
                output.accept(ModItems.RAVENCLAW_ROBE_LEGS.get());
                output.accept(ModItems.RAVENCLAW_ROBE_BOOTS.get());
            })
            .build()
    );
}
