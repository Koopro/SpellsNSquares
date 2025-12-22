package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.features.artifacts.DeluminatorData;
import at.koopro.spells_n_squares.features.artifacts.ElderWandData;
import at.koopro.spells_n_squares.features.artifacts.GobletOfFireData;
import at.koopro.spells_n_squares.features.artifacts.MaraudersMapData;
import at.koopro.spells_n_squares.features.artifacts.MirrorOfErisedData;
import at.koopro.spells_n_squares.features.artifacts.PensieveData;
import at.koopro.spells_n_squares.features.artifacts.PhilosophersStoneData;
import at.koopro.spells_n_squares.features.artifacts.RemembrallData;
import at.koopro.spells_n_squares.features.artifacts.ResurrectionStoneData;
import at.koopro.spells_n_squares.features.artifacts.SortingHatData;
import at.koopro.spells_n_squares.features.artifacts.TimeTurnerItem;
import at.koopro.spells_n_squares.features.cloak.CloakChargeData;
import at.koopro.spells_n_squares.features.combat.CombatStatsData;
import at.koopro.spells_n_squares.features.communication.MirrorData;
import at.koopro.spells_n_squares.features.creatures.CreatureData;
import at.koopro.spells_n_squares.features.contracts.ContractData;
import at.koopro.spells_n_squares.features.economy.CurrencyData;
import at.koopro.spells_n_squares.features.education.BestiaryData;
import at.koopro.spells_n_squares.features.education.HomeworkSystem;
import at.koopro.spells_n_squares.features.education.HousePointsSystem;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentSystem;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import at.koopro.spells_n_squares.features.gear.SocketData;
import at.koopro.spells_n_squares.features.ghosts.GhostData;
import at.koopro.spells_n_squares.features.mail.MailData;
import at.koopro.spells_n_squares.features.mail.MailboxData;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassData;
import at.koopro.spells_n_squares.features.portraits.PortraitData;
import at.koopro.spells_n_squares.features.potions.PotionData;
import at.koopro.spells_n_squares.features.social.SocialData;
import at.koopro.spells_n_squares.features.storage.BagInventoryData;
import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import at.koopro.spells_n_squares.features.storage.TrunkInventoryData;
import at.koopro.spells_n_squares.features.transportation.BroomstickData;
import at.koopro.spells_n_squares.features.transportation.PortkeyData;
import at.koopro.spells_n_squares.features.wand.WandData;
import net.neoforged.bus.api.IEventBus;

/**
 * Centralized registry for all data component registrations.
 * Groups registrations by feature category for better organization.
 */
public final class DataComponentRegistry {
    private DataComponentRegistry() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Registers all data component deferred registers with the mod event bus.
     * Groups registrations by feature category for clarity.
     * 
     * @param modEventBus The mod event bus to register with
     */
    public static void registerAll(IEventBus modEventBus) {
        // Core data components
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        
        // Artifacts
        DeluminatorData.DATA_COMPONENTS.register(modEventBus);
        ElderWandData.DATA_COMPONENTS.register(modEventBus);
        GobletOfFireData.DATA_COMPONENTS.register(modEventBus);
        MaraudersMapData.DATA_COMPONENTS.register(modEventBus);
        MirrorOfErisedData.DATA_COMPONENTS.register(modEventBus);
        PensieveData.DATA_COMPONENTS.register(modEventBus);
        PhilosophersStoneData.DATA_COMPONENTS.register(modEventBus);
        RemembrallData.DATA_COMPONENTS.register(modEventBus);
        ResurrectionStoneData.DATA_COMPONENTS.register(modEventBus);
        SortingHatData.DATA_COMPONENTS.register(modEventBus);
        TimeTurnerItem.DATA_COMPONENTS.register(modEventBus);
        
        // Equipment & Gear
        FlashlightItem.DATA_COMPONENTS.register(modEventBus);
        WandData.DATA_COMPONENTS.register(modEventBus);
        CloakChargeData.DATA_COMPONENTS.register(modEventBus);
        SocketData.DATA_COMPONENTS.register(modEventBus);
        
        // Storage
        BagInventoryData.DATA_COMPONENTS.register(modEventBus);
        TrunkInventoryData.DATA_COMPONENTS.register(modEventBus);
        PocketDimensionData.DATA_COMPONENTS.register(modEventBus);
        
        // Transportation
        PortkeyData.DATA_COMPONENTS.register(modEventBus);
        BroomstickData.DATA_COMPONENTS.register(modEventBus);
        
        // Communication
        MirrorData.DATA_COMPONENTS.register(modEventBus);
        
        // Mail System
        MailData.DATA_COMPONENTS.register(modEventBus);
        MailboxData.DATA_COMPONENTS.register(modEventBus);
        
        // Contracts
        ContractData.DATA_COMPONENTS.register(modEventBus);
        
        // Social Systems
        SocialData.DATA_COMPONENTS.register(modEventBus);
        
        // Portraits
        PortraitData.DATA_COMPONENTS.register(modEventBus);
        
        // Ghosts
        GhostData.DATA_COMPONENTS.register(modEventBus);
        
        // Creatures & Systems
        EnchantmentSystem.DATA_COMPONENTS.register(modEventBus);
        CreatureData.DATA_COMPONENTS.register(modEventBus);
        
        // Education
        BestiaryData.DATA_COMPONENTS.register(modEventBus);
        HomeworkSystem.DATA_COMPONENTS.register(modEventBus);
        HousePointsSystem.DATA_COMPONENTS.register(modEventBus);
        
        // Combat & Economy
        CombatStatsData.DATA_COMPONENTS.register(modEventBus);
        CurrencyData.DATA_COMPONENTS.register(modEventBus);
        
        // Potions
        PotionData.DATA_COMPONENTS.register(modEventBus);
        
        // Player Classes
        PlayerClassData.DATA_COMPONENTS.register(modEventBus);
    }
}






