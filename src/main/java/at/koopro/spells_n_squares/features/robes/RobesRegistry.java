package at.koopro.spells_n_squares.features.robes;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModArmorMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for robes feature items.
 */
public class RobesRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
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
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}



