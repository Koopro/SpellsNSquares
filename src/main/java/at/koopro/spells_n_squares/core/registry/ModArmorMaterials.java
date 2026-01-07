package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

/**
 * Registry for armor materials used by mod armor items.
 */
public class ModArmorMaterials {
    
    // Create an empty tag for items that cannot be repaired
    private static final TagKey<Item> NO_REPAIR = TagKey.create(
        BuiltInRegistries.ITEM.key(),
        ModIdentifierHelper.modId("no_repair")
    );
    
    public static final ArmorMaterial DEMIGUISE_CLOAK_MATERIAL = new ArmorMaterial(
        500, // durability
        Util.make(new EnumMap<>(ArmorType.class), map -> {
            map.put(ArmorType.BOOTS, 0);
            map.put(ArmorType.LEGGINGS, 0);
            map.put(ArmorType.CHESTPLATE, 0);
            map.put(ArmorType.HELMET, 0);
            map.put(ArmorType.BODY, 0);
        }),
        15, // enchantmentValue (must be positive, using leather's value)
        SoundEvents.ARMOR_EQUIP_LEATHER, // equipSound
        0.0f, // toughness
        0.0f, // knockbackResistance
        NO_REPAIR, // repairIngredient - empty tag means cannot be repaired
        ResourceKey.create(EquipmentAssets.ROOT_ID, ModIdentifierHelper.modId("demiguise_cloak"))
    );
    
    public static final ArmorMaterial DEATHLY_HALLOW_CLOAK_MATERIAL = new ArmorMaterial(
        Integer.MAX_VALUE, // durability (unbreakable)
        Util.make(new EnumMap<>(ArmorType.class), map -> {
            map.put(ArmorType.BOOTS, 0);
            map.put(ArmorType.LEGGINGS, 0);
            map.put(ArmorType.CHESTPLATE, 0);
            map.put(ArmorType.HELMET, 0);
            map.put(ArmorType.BODY, 0);
        }),
        25, // enchantmentValue (must be positive, using diamond's value for the legendary cloak)
        SoundEvents.ARMOR_EQUIP_ELYTRA, // equipSound
        0.0f, // toughness
        0.0f, // knockbackResistance
        NO_REPAIR, // repairIngredient - empty tag means cannot be repaired
        ResourceKey.create(EquipmentAssets.ROOT_ID, ModIdentifierHelper.modId("deathly_hallow_cloak"))
    );
    
    // Create Holders for use with Item.Properties.humanoidArmor()
    public static final Holder<ArmorMaterial> DEMIGUISE_CLOAK_MATERIAL_HOLDER = 
        Holder.direct(DEMIGUISE_CLOAK_MATERIAL);
    
    public static final Holder<ArmorMaterial> DEATHLY_HALLOW_CLOAK_MATERIAL_HOLDER = 
        Holder.direct(DEATHLY_HALLOW_CLOAK_MATERIAL);
    
    // House robe material (shared by all houses)
    public static final ArmorMaterial HOUSE_ROBE_MATERIAL = new ArmorMaterial(
        300, // durability
        Util.make(new EnumMap<>(ArmorType.class), map -> {
            map.put(ArmorType.BOOTS, 0);
            map.put(ArmorType.LEGGINGS, 0);
            map.put(ArmorType.CHESTPLATE, 0);
            map.put(ArmorType.HELMET, 0);
            map.put(ArmorType.BODY, 0);
        }),
        15, // enchantmentValue
        SoundEvents.ARMOR_EQUIP_LEATHER, // equipSound
        0.0f, // toughness
        0.0f, // knockbackResistance
        NO_REPAIR, // repairIngredient
        ResourceKey.create(EquipmentAssets.ROOT_ID, ModIdentifierHelper.modId("house_robe"))
    );
    
    public static final Holder<ArmorMaterial> HOUSE_ROBE_MATERIAL_HOLDER = 
        Holder.direct(HOUSE_ROBE_MATERIAL);
}
