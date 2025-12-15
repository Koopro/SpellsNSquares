package at.koopro.spells_n_squares.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.cloak.DeathlyHallowCloakItem;
import at.koopro.spells_n_squares.features.cloak.DemiguiseCloakItem;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import at.koopro.spells_n_squares.features.wand.DemoWandItem;
import at.koopro.spells_n_squares.item.FleshlightItem;
import at.koopro.spells_n_squares.item.RubberDuckItem;

/**
 * Registry for all mod items.
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);

    public static final DeferredItem<RubberDuckItem> RUBBER_DUCK = ITEMS.register(
            "rubber_duck", id -> new RubberDuckItem(createProperties(id)));
    
    public static final DeferredItem<FlashlightItem> FLASHLIGHT = ITEMS.register(
            "flashlight", id -> new FlashlightItem(createProperties(id).stacksTo(1)));

    public static final DeferredItem<FleshlightItem> FLESHLIGHT = ITEMS.register(
            "fleshlight", id -> new FleshlightItem(createProperties(id).stacksTo(1)));
    
    public static final DeferredItem<DemoWandItem> DEMO_WAND = ITEMS.register(
            "demo_wand", id -> new DemoWandItem(createProperties(id)));
    
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
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}
