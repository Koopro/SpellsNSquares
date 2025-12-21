package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.logging.LogUtils;
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
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // TODO: Re-enable when renderer classes and entities are implemented
        // Owl entity would need a renderer - using default for now
    }
}
