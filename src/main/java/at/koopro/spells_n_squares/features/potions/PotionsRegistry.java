package at.koopro.spells_n_squares.features.potions;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.potions.item.BabblingBeverageItem;
import at.koopro.spells_n_squares.features.potions.item.DraughtOfLivingDeathItem;
import at.koopro.spells_n_squares.features.potions.item.DraughtOfPeaceItem;
import at.koopro.spells_n_squares.features.potions.item.FelixFelicisItem;
import at.koopro.spells_n_squares.features.potions.item.FireProtectionPotionItem;
import at.koopro.spells_n_squares.features.potions.item.ForgetfulnessPotionItem;
import at.koopro.spells_n_squares.features.potions.item.HealingPotionItem;
import at.koopro.spells_n_squares.features.potions.item.InvisibilityPotionItem;
import at.koopro.spells_n_squares.features.potions.item.LovePotionItem;
import at.koopro.spells_n_squares.features.potions.item.MurtlapEssenceItem;
import at.koopro.spells_n_squares.features.potions.item.PepperupPotionItem;
import at.koopro.spells_n_squares.features.potions.item.PolyjuicePotionItem;
import at.koopro.spells_n_squares.features.potions.item.ShrinkingSolutionItem;
import at.koopro.spells_n_squares.features.potions.item.SkeleGroPotionItem;
import at.koopro.spells_n_squares.features.potions.item.StrengthPotionItem;
import at.koopro.spells_n_squares.features.potions.item.SwellingSolutionItem;
import at.koopro.spells_n_squares.features.potions.item.VeritaserumItem;
import at.koopro.spells_n_squares.features.potions.item.WideyePotionItem;
import at.koopro.spells_n_squares.features.potions.item.WitSharpeningPotionItem;
import at.koopro.spells_n_squares.features.potions.item.WolfsbanePotionItem;
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
    
    // Additional potion items
    public static final DeferredItem<BabblingBeverageItem> BABBLING_BEVERAGE_POTION = ITEMS.register(
            "babbling_beverage_potion", id -> new BabblingBeverageItem(createProperties(id)));
    public static final DeferredItem<HealingPotionItem> HEALING_POTION = ITEMS.register(
            "healing_potion", id -> new HealingPotionItem(createProperties(id)));
    public static final DeferredItem<FireProtectionPotionItem> FIRE_PROTECTION_POTION = ITEMS.register(
            "fire_protection_potion", id -> new FireProtectionPotionItem(createProperties(id)));
    public static final DeferredItem<ForgetfulnessPotionItem> FORGETFULNESS_POTION = ITEMS.register(
            "forgetfulness_potion", id -> new ForgetfulnessPotionItem(createProperties(id)));
    public static final DeferredItem<InvisibilityPotionItem> INVISIBILITY_POTION = ITEMS.register(
            "invisibility_potion", id -> new InvisibilityPotionItem(createProperties(id)));
    public static final DeferredItem<LovePotionItem> LOVE_POTION = ITEMS.register(
            "love_potion", id -> new LovePotionItem(createProperties(id)));
    public static final DeferredItem<PepperupPotionItem> PEPPERUP_POTION = ITEMS.register(
            "pepperup_potion", id -> new PepperupPotionItem(createProperties(id)));
    public static final DeferredItem<PolyjuicePotionItem> POLYJUICE_POTION = ITEMS.register(
            "polyjuice_potion", id -> new PolyjuicePotionItem(createProperties(id)));
    public static final DeferredItem<SkeleGroPotionItem> SKELE_GRO_POTION = ITEMS.register(
            "skele_gro_potion", id -> new SkeleGroPotionItem(createProperties(id)));
    public static final DeferredItem<StrengthPotionItem> STRENGTH_POTION = ITEMS.register(
            "strength_potion", id -> new StrengthPotionItem(createProperties(id)));
    public static final DeferredItem<WideyePotionItem> WIDEYE_POTION = ITEMS.register(
            "wideye_potion", id -> new WideyePotionItem(createProperties(id)));
    public static final DeferredItem<WitSharpeningPotionItem> WIT_SHARPENING_POTION = ITEMS.register(
            "wit_sharpening_potion", id -> new WitSharpeningPotionItem(createProperties(id)));
    public static final DeferredItem<WolfsbanePotionItem> WOLFSBANE_POTION = ITEMS.register(
            "wolfsbane_potion", id -> new WolfsbanePotionItem(createProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}












