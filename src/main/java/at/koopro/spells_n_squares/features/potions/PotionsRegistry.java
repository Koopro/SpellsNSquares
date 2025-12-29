package at.koopro.spells_n_squares.features.potions;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for potions feature items.
 */
public class PotionsRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredItem<DraughtOfLivingDeathItem> DRAUGHT_OF_LIVING_DEATH_POTION = ITEMS.register(
            "draught_of_living_death_potion", id -> new DraughtOfLivingDeathItem(createProperties(id)));
    public static final DeferredItem<DraughtOfPeaceItem> DRAUGHT_OF_PEACE_POTION = ITEMS.register(
            "draught_of_peace_potion", id -> new DraughtOfPeaceItem(createProperties(id)));
    public static final DeferredItem<FelixFelicisItem> FELIX_FELICIS = ITEMS.register(
            "felix_felicis", id -> new FelixFelicisItem(createProperties(id)));
    public static final DeferredItem<MurtlapEssenceItem> MURTLAP_ESSENCE_POTION = ITEMS.register(
            "murtlap_essence_potion", id -> new MurtlapEssenceItem(createProperties(id)));
    public static final DeferredItem<ShrinkingSolutionItem> SHRINKING_SOLUTION_POTION = ITEMS.register(
            "shrinking_solution_potion", id -> new ShrinkingSolutionItem(createProperties(id)));
    public static final DeferredItem<SwellingSolutionItem> SWELLING_SOLUTION_POTION = ITEMS.register(
            "swelling_solution_potion", id -> new SwellingSolutionItem(createProperties(id)));
    public static final DeferredItem<VeritaserumItem> VERITASERUM = ITEMS.register(
            "veritaserum", id -> new VeritaserumItem(createProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}












