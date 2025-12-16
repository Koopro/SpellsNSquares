package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModEntities;
// TODO: Re-enable when renderer classes are implemented
// import at.koopro.spells_n_squares.features.spell.client.DummyPlayerRenderer;
// import at.koopro.spells_n_squares.features.spell.client.LightOrbRenderer;
// import at.koopro.spells_n_squares.features.spell.client.LightningBeamRenderer;
// import at.koopro.spells_n_squares.features.spell.client.ShieldOrbRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // TODO: Re-enable when renderer classes and entities are implemented
        // Register entity renderers using helper method to reduce repetition
        /*
        registerRenderer(event, ModEntities.SHIELD_ORB, ShieldOrbRenderer::new);
        registerRenderer(event, ModEntities.LIGHT_ORB, LightOrbRenderer::new);
        registerRenderer(event, ModEntities.LIGHTNING_BEAM, LightningBeamRenderer::new);
        registerRenderer(event, ModEntities.DUMMY_PLAYER, DummyPlayerRenderer::new);
        */
        // Owl entity would need a renderer - using default for now
    }
    
    /**
     * Helper method to register an entity renderer.
     * Reduces code duplication in renderer registration.
     * 
     * @param event The renderer registration event
     * @param entityType The deferred holder for the entity type
     * @param rendererFactory The factory function to create the renderer
     * @param <T> The entity type
     */
    private static <T extends Entity> void registerRenderer(
            EntityRenderersEvent.RegisterRenderers event,
            DeferredHolder<EntityType<?>, EntityType<T>> entityType,
            EntityRendererProvider<T> rendererFactory
    ) {
        event.registerEntityRenderer(entityType.get(), rendererFactory);
    }
    
}
