package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
import at.koopro.spells_n_squares.features.creatures.client.OwlRenderer;
import at.koopro.spells_n_squares.features.spell.SpellEntityRegistry;
import at.koopro.spells_n_squares.features.spell.client.DummyPlayerRenderer;
import at.koopro.spells_n_squares.features.spell.client.LightOrbRenderer;
import at.koopro.spells_n_squares.features.spell.client.LightningBeamRenderer;
import at.koopro.spells_n_squares.features.spell.client.ShieldOrbRenderer;
import at.koopro.spells_n_squares.features.fx.block.FxBlockEntities;
import at.koopro.spells_n_squares.features.fx.block.client.EnergyBallBlockRenderer;
import at.koopro.spells_n_squares.features.storage.block.StorageBlockEntities;
import at.koopro.spells_n_squares.features.storage.block.client.NewtsCaseBlockRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

/**
 * Client-side initialization module.
 * Handles client-specific setup like renderers.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ClientInitialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Client setup complete - Spells_n_Squares mod ready");
        // Initialize all features on the client side
        at.koopro.spells_n_squares.core.registry.FeatureRegistry.initializeAllClient();
        // Initialize all addons on the client side
        at.koopro.spells_n_squares.core.registry.AddonRegistry.initializeAllAddonsClient();
        // Initialize FX systems
        at.koopro.spells_n_squares.features.fx.ShaderEffectHandler.initialize();
    }
    
    @SubscribeEvent
    public static void onRegisterMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        // Register menu screens
        event.register(
            at.koopro.spells_n_squares.core.registry.ModMenus.VAULT_MENU.get(),
            at.koopro.spells_n_squares.features.economy.client.VaultScreen::new
        );
        event.register(
            at.koopro.spells_n_squares.core.registry.ModMenus.MAILBOX_MENU.get(),
            at.koopro.spells_n_squares.features.mail.client.MailboxScreen::new
        );
        event.register(
            at.koopro.spells_n_squares.core.registry.ModMenus.ENCHANTMENT_MENU.get(),
            at.koopro.spells_n_squares.features.enchantments.client.EnchantmentScreen::new
        );
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register owl entity renderer
        event.registerEntityRenderer(
            CommunicationRegistry.OWL.get(),
            (EntityRendererProvider.Context context) -> new OwlRenderer(context)
        );
        
        // Register spell entity renderers
        event.registerEntityRenderer(
            SpellEntityRegistry.SHIELD_ORB.get(),
            (EntityRendererProvider.Context context) -> new ShieldOrbRenderer(context)
        );
        event.registerEntityRenderer(
            SpellEntityRegistry.LIGHT_ORB.get(),
            (EntityRendererProvider.Context context) -> new LightOrbRenderer(context)
        );
        event.registerEntityRenderer(
            SpellEntityRegistry.LIGHTNING_BEAM.get(),
            (EntityRendererProvider.Context context) -> new LightningBeamRenderer(context)
        );
        event.registerEntityRenderer(
            SpellEntityRegistry.DUMMY_PLAYER.get(),
            (EntityRendererProvider.Context context) -> new DummyPlayerRenderer(context)
        );

        // Register block entity renderers
        event.registerBlockEntityRenderer(
            FxBlockEntities.ENERGY_BALL_BLOCK_ENTITY.get(),
            EnergyBallBlockRenderer::new
        );
        
        if (StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY != null) {
            event.registerBlockEntityRenderer(
                StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY.get(),
                NewtsCaseBlockRenderer::new
            );
        }
        
        // Note: GeckoLib item renderers are registered automatically via the item's createGeoRenderer method
        // No need to register them here in EntityRenderersEvent
    }
}
