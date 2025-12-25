package at.koopro.spells_n_squares.features.cloak;

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
 * Registry for cloak feature items.
 */
public class CloakRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
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
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CloakChargeData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}








