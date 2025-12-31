package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
import at.koopro.spells_n_squares.features.building.BuildingRegistry;
import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import at.koopro.spells_n_squares.features.environment.block.TreeBlockSet;
import at.koopro.spells_n_squares.features.navigation.NavigationRegistry;
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import at.koopro.spells_n_squares.core.registry.ModTreeBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
            .icon(() -> new ItemStack(WandRegistry.DEMO_WAND.get()))
            .displayItems((parameters, output) -> {
                output.accept(WandRegistry.DEMO_WAND.get());
                // Add other wand/spell related items here
            })
            .build()
    );
    
    // Economy tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ECONOMY_TAB = CREATIVE_TABS.register(
        "economy",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.economy"))
            .icon(() -> new ItemStack(EconomyRegistry.TRADING_POST.get()))
            .displayItems((parameters, output) -> {
                Item tradingPostItem = EconomyRegistry.TRADING_POST.get().asItem();
                if (tradingPostItem != null && tradingPostItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(tradingPostItem);
                }
                Item automatedShopItem = EconomyRegistry.AUTOMATED_SHOP.get().asItem();
                if (automatedShopItem != null && automatedShopItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(automatedShopItem);
                }
                Item vaultItem = EconomyRegistry.VAULT.get().asItem();
                if (vaultItem != null && vaultItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(vaultItem);
                }
            })
            .build()
    );
    
    // Quality of life tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUALITY_OF_LIFE_TAB = CREATIVE_TABS.register(
        "quality_of_life",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.quality_of_life"))
            .icon(() -> new ItemStack(at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry.FLASHLIGHT.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.RUBBER_DUCK.get());
                output.accept(at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry.FLASHLIGHT.get());
            })
            .build()
    );
    
    // Transportation tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRANSPORTATION_TAB = CREATIVE_TABS.register(
        "transportation",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.transportation"))
            .icon(() -> new ItemStack(TransportationRegistry.BROOMSTICK_BASIC.get()))
            .displayItems((parameters, output) -> {
                output.accept(TransportationRegistry.PORTKEY.get());
                output.accept(TransportationRegistry.FLOO_POWDER.get());
                output.accept(TransportationRegistry.BROOMSTICK_BASIC.get());
                output.accept(TransportationRegistry.BROOMSTICK_RACING.get());
                output.accept(TransportationRegistry.BROOMSTICK_FIREBOLT.get());
            })
            .build()
    );
    
    // Storage tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STORAGE_TAB = CREATIVE_TABS.register(
        "storage",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.storage"))
            .icon(() -> new ItemStack(StorageRegistry.MAGICAL_TRUNK.get()))
            .displayItems((parameters, output) -> {
                Item magicalTrunkItem = StorageRegistry.MAGICAL_TRUNK.get().asItem();
                if (magicalTrunkItem != null && magicalTrunkItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalTrunkItem);
                }
                Item autoSortChestItem = StorageRegistry.AUTO_SORT_CHEST.get().asItem();
                if (autoSortChestItem != null && autoSortChestItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(autoSortChestItem);
                }
                output.accept(StorageRegistry.ENCHANTED_BAG_SMALL.get());
                output.accept(StorageRegistry.ENCHANTED_BAG_MEDIUM.get());
                output.accept(StorageRegistry.ENCHANTED_BAG_LARGE.get());
                output.accept(StorageRegistry.ENCHANTED_BAG_BOTTOMLESS.get());
                output.accept(StorageRegistry.POCKET_DIMENSION.get());
                output.accept(StorageRegistry.NEWTS_CASE_ITEM.get());
            })
            .build()
    );
    
    // Building tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_TAB = CREATIVE_TABS.register(
        "building",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.building"))
            .icon(() -> new ItemStack(BuildingRegistry.MAGICAL_LIGHT_WHITE.get()))
            .displayItems((parameters, output) -> {
                Item magicalLightWhiteItem = BuildingRegistry.MAGICAL_LIGHT_WHITE.get().asItem();
                if (magicalLightWhiteItem != null && magicalLightWhiteItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightWhiteItem);
                }
                Item magicalLightBlueItem = BuildingRegistry.MAGICAL_LIGHT_BLUE.get().asItem();
                if (magicalLightBlueItem != null && magicalLightBlueItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightBlueItem);
                }
                Item magicalLightGreenItem = BuildingRegistry.MAGICAL_LIGHT_GREEN.get().asItem();
                if (magicalLightGreenItem != null && magicalLightGreenItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightGreenItem);
                }
                Item magicalLightRedItem = BuildingRegistry.MAGICAL_LIGHT_RED.get().asItem();
                if (magicalLightRedItem != null && magicalLightRedItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightRedItem);
                }
                Item magicalLightPurpleItem = BuildingRegistry.MAGICAL_LIGHT_PURPLE.get().asItem();
                if (magicalLightPurpleItem != null && magicalLightPurpleItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightPurpleItem);
                }
                Item magicalLightGoldItem = BuildingRegistry.MAGICAL_LIGHT_GOLD.get().asItem();
                if (magicalLightGoldItem != null && magicalLightGoldItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalLightGoldItem);
                }
                Item wizardTowerItem = BuildingRegistry.WIZARD_TOWER.get().asItem();
                if (wizardTowerItem != null && wizardTowerItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(wizardTowerItem);
                }
                
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
            .icon(() -> new ItemStack(AutomationRegistry.SELF_STIRRING_CAULDRON.get()))
            .displayItems((parameters, output) -> {
                Item selfStirringCauldronItem = AutomationRegistry.SELF_STIRRING_CAULDRON.get().asItem();
                if (selfStirringCauldronItem != null && selfStirringCauldronItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(selfStirringCauldronItem);
                }
                Item magicalFurnaceItem = AutomationRegistry.MAGICAL_FURNACE.get().asItem();
                if (magicalFurnaceItem != null && magicalFurnaceItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalFurnaceItem);
                }
                Item magicalFarmItem = AutomationRegistry.MAGICAL_FARM.get().asItem();
                if (magicalFarmItem != null && magicalFarmItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalFarmItem);
                }
                Item itemCollectorItem = AutomationRegistry.ITEM_COLLECTOR.get().asItem();
                if (itemCollectorItem != null && itemCollectorItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(itemCollectorItem);
                }
                Item magicalComposterItem = AutomationRegistry.MAGICAL_COMPOSTER.get().asItem();
                if (magicalComposterItem != null && magicalComposterItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(magicalComposterItem);
                }
                Item resourceGeneratorItem = AutomationRegistry.RESOURCE_GENERATOR.get().asItem();
                if (resourceGeneratorItem != null && resourceGeneratorItem != net.minecraft.world.item.Items.AIR) {
                    output.accept(resourceGeneratorItem);
                }
                output.accept(AutomationRegistry.ENCHANTED_WORKBENCH.get());
                output.accept(AutomationRegistry.AUTO_HARVEST_HOE.get());
            })
            .build()
    );
    
    // Navigation tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NAVIGATION_TAB = CREATIVE_TABS.register(
        "navigation",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.navigation"))
            .icon(() -> new ItemStack(NavigationRegistry.MAGICAL_MAP.get()))
            .displayItems((parameters, output) -> {
                output.accept(NavigationRegistry.MAGICAL_MAP.get());
                output.accept(NavigationRegistry.LOCATION_COMPASS.get());
                output.accept(NavigationRegistry.MAGICAL_JOURNAL.get());
            })
            .build()
    );
    
    // Robes tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ROBES_TAB = CREATIVE_TABS.register(
        "robes",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.robes"))
            .icon(() -> new ItemStack(RobesRegistry.GRYFFINDOR_ROBE_CHEST.get()))
            .displayItems((parameters, output) -> {
                // Gryffindor robes
                output.accept(RobesRegistry.GRYFFINDOR_ROBE_CHEST.get());
                output.accept(RobesRegistry.GRYFFINDOR_ROBE_LEGS.get());
                output.accept(RobesRegistry.GRYFFINDOR_ROBE_BOOTS.get());
                
                // Slytherin robes
                output.accept(RobesRegistry.SLYTHERIN_ROBE_CHEST.get());
                output.accept(RobesRegistry.SLYTHERIN_ROBE_LEGS.get());
                output.accept(RobesRegistry.SLYTHERIN_ROBE_BOOTS.get());
                
                // Hufflepuff robes
                output.accept(RobesRegistry.HUFFLEPUFF_ROBE_CHEST.get());
                output.accept(RobesRegistry.HUFFLEPUFF_ROBE_LEGS.get());
                output.accept(RobesRegistry.HUFFLEPUFF_ROBE_BOOTS.get());
                
                // Ravenclaw robes
                output.accept(RobesRegistry.RAVENCLAW_ROBE_CHEST.get());
                output.accept(RobesRegistry.RAVENCLAW_ROBE_LEGS.get());
                output.accept(RobesRegistry.RAVENCLAW_ROBE_BOOTS.get());
            })
            .build()
    );
}
