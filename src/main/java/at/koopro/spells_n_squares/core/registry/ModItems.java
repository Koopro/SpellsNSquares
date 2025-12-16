package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.artifacts.SneakoscopeItem;
import at.koopro.spells_n_squares.features.artifacts.TimeTurnerItem;
import at.koopro.spells_n_squares.features.cloak.DeathlyHallowCloakItem;
import at.koopro.spells_n_squares.features.cloak.DemiguiseCloakItem;
import at.koopro.spells_n_squares.features.cloak.RevealerDustItem;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import at.koopro.spells_n_squares.features.misc.RubberDuckItem;
import at.koopro.spells_n_squares.features.robes.HouseRobeItem;
import at.koopro.spells_n_squares.features.storage.EnchantedBagItem;
import at.koopro.spells_n_squares.features.storage.PocketDimensionItem;
import at.koopro.spells_n_squares.features.building.WizardTowerItem;
import at.koopro.spells_n_squares.features.automation.AutoHarvestTool;
import at.koopro.spells_n_squares.features.navigation.LocationCompassItem;
import at.koopro.spells_n_squares.features.navigation.MagicalJournalItem;
import at.koopro.spells_n_squares.features.navigation.MagicalMapItem;
import at.koopro.spells_n_squares.features.automation.EnchantedWorkbenchItem;
import at.koopro.spells_n_squares.features.communication.TwoWayMirrorItem;
import at.koopro.spells_n_squares.features.transportation.BroomstickItem;
import at.koopro.spells_n_squares.features.transportation.FlooPowderItem;
import at.koopro.spells_n_squares.features.transportation.PortkeyItem;
import at.koopro.spells_n_squares.features.wand.WandItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod items.
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);

    public static final DeferredItem<RubberDuckItem> RUBBER_DUCK = ITEMS.register(
            "rubber_duck", id -> new RubberDuckItem(createProperties(id)));
    
    public static final DeferredItem<FlashlightItem> FLASHLIGHT = ITEMS.register(
            "flashlight", id -> new FlashlightItem(createProperties(id).stacksTo(1)));
    
    public static final DeferredItem<WandItem> DEMO_WAND = ITEMS.register(
            "demo_wand", id -> new WandItem(createProperties(id)));
    
    public static final DeferredItem<DemiguiseCloakItem> DEMIGUISE_CLOAK = ITEMS.register(
            "demiguise_cloak", id -> new DemiguiseCloakItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.DEMIGUISE_CLOAK_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)
            ));
    
    public static final DeferredItem<DeathlyHallowCloakItem> DEATHLY_HALLOW_CLOAK = ITEMS.register(
            "deathly_hallow_cloak", id -> new DeathlyHallowCloakItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.DEATHLY_HALLOW_CLOAK_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)
            ));
    
    public static final DeferredItem<RevealerDustItem> REVEALER_DUST = ITEMS.register(
            "revealer_dust", id -> new RevealerDustItem(createProperties(id)));
    
    public static final DeferredItem<TimeTurnerItem> TIME_TURNER = ITEMS.register(
            "time_turner", id -> new TimeTurnerItem(createProperties(id)));
    
    public static final DeferredItem<SneakoscopeItem> SNEAKOSCOPE = ITEMS.register(
            "sneakoscope", id -> new SneakoscopeItem(createProperties(id)));
    
    // Enchanted bags
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_SMALL = ITEMS.register(
            "enchanted_bag_small", id -> new EnchantedBagItem(createProperties(id), EnchantedBagItem.BagTier.SMALL));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_MEDIUM = ITEMS.register(
            "enchanted_bag_medium", id -> new EnchantedBagItem(createProperties(id), EnchantedBagItem.BagTier.MEDIUM));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_LARGE = ITEMS.register(
            "enchanted_bag_large", id -> new EnchantedBagItem(createProperties(id), EnchantedBagItem.BagTier.LARGE));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_BOTTOMLESS = ITEMS.register(
            "enchanted_bag_bottomless", id -> new EnchantedBagItem(createProperties(id), EnchantedBagItem.BagTier.BOTTOMLESS));
    
    // Storage items
    public static final DeferredItem<PocketDimensionItem> POCKET_DIMENSION = ITEMS.register(
            "pocket_dimension", id -> new PocketDimensionItem(createProperties(id)));
    
    // Transportation items
    public static final DeferredItem<PortkeyItem> PORTKEY = ITEMS.register(
            "portkey", id -> new PortkeyItem(createProperties(id)));
    public static final DeferredItem<FlooPowderItem> FLOO_POWDER = ITEMS.register(
            "floo_powder", id -> new FlooPowderItem(createProperties(id)));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_BASIC = ITEMS.register(
            "broomstick_basic", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.BASIC));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_RACING = ITEMS.register(
            "broomstick_racing", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.RACING));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_FIREBOLT = ITEMS.register(
            "broomstick_firebolt", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.FIREBOLT));
    
    // Communication items
    public static final DeferredItem<TwoWayMirrorItem> TWO_WAY_MIRROR = ITEMS.register(
            "two_way_mirror", id -> new TwoWayMirrorItem(createProperties(id)));
    
    // Automation items
    public static final DeferredItem<EnchantedWorkbenchItem> ENCHANTED_WORKBENCH = ITEMS.register(
            "enchanted_workbench", id -> new EnchantedWorkbenchItem(createProperties(id)));
    public static final DeferredItem<AutoHarvestTool> AUTO_HARVEST_HOE = ITEMS.register(
            "auto_harvest_hoe", id -> new AutoHarvestTool(createProperties(id)));
    
    // Building items
    public static final DeferredItem<WizardTowerItem> WIZARD_TOWER = ITEMS.register(
            "wizard_tower", id -> new WizardTowerItem(createProperties(id)));
    
    // Navigation items
    public static final DeferredItem<MagicalMapItem> MAGICAL_MAP = ITEMS.register(
            "magical_map", id -> new MagicalMapItem(createProperties(id)));
    public static final DeferredItem<LocationCompassItem> LOCATION_COMPASS = ITEMS.register(
            "location_compass", id -> new LocationCompassItem(createProperties(id)));
    public static final DeferredItem<MagicalJournalItem> MAGICAL_JOURNAL = ITEMS.register(
            "magical_journal", id -> new MagicalJournalItem(createProperties(id)));
    
    // Gryffindor robes
    public static final DeferredItem<HouseRobeItem> GRYFFINDOR_ROBE_CHEST = ITEMS.register(
            "gryffindor_robe_chest", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> GRYFFINDOR_ROBE_LEGS = ITEMS.register(
            "gryffindor_robe_legs", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.LEGGINGS)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> GRYFFINDOR_ROBE_BOOTS = ITEMS.register(
            "gryffindor_robe_boots", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.BOOTS)
                    .stacksTo(1)));
    
    // Slytherin robes
    public static final DeferredItem<HouseRobeItem> SLYTHERIN_ROBE_CHEST = ITEMS.register(
            "slytherin_robe_chest", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> SLYTHERIN_ROBE_LEGS = ITEMS.register(
            "slytherin_robe_legs", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.LEGGINGS)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> SLYTHERIN_ROBE_BOOTS = ITEMS.register(
            "slytherin_robe_boots", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.BOOTS)
                    .stacksTo(1)));
    
    // Hufflepuff robes
    public static final DeferredItem<HouseRobeItem> HUFFLEPUFF_ROBE_CHEST = ITEMS.register(
            "hufflepuff_robe_chest", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> HUFFLEPUFF_ROBE_LEGS = ITEMS.register(
            "hufflepuff_robe_legs", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.LEGGINGS)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> HUFFLEPUFF_ROBE_BOOTS = ITEMS.register(
            "hufflepuff_robe_boots", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.BOOTS)
                    .stacksTo(1)));
    
    // Ravenclaw robes
    public static final DeferredItem<HouseRobeItem> RAVENCLAW_ROBE_CHEST = ITEMS.register(
            "ravenclaw_robe_chest", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.CHESTPLATE)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> RAVENCLAW_ROBE_LEGS = ITEMS.register(
            "ravenclaw_robe_legs", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.LEGGINGS)
                    .stacksTo(1)));
    public static final DeferredItem<HouseRobeItem> RAVENCLAW_ROBE_BOOTS = ITEMS.register(
            "ravenclaw_robe_boots", id -> new HouseRobeItem(
                createProperties(id)
                    .humanoidArmor(ModArmorMaterials.HOUSE_ROBE_MATERIAL_HOLDER.value(), ArmorType.BOOTS)
                    .stacksTo(1)));
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}
