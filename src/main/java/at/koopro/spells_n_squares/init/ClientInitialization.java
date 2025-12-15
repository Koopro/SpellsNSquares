package at.koopro.spells_n_squares.init;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import at.koopro.spells_n_squares.core.registry.ModEntities;
import at.koopro.spells_n_squares.features.spell.client.LightOrbRenderer;
import at.koopro.spells_n_squares.features.spell.client.ShieldOrbRenderer;
import at.koopro.spells_n_squares.features.spell.client.LightningBeamRenderer;
import at.koopro.spells_n_squares.features.spell.client.DummyPlayerRenderer;

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
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
            ModEntities.SHIELD_ORB.get(),
            ShieldOrbRenderer::new
        );
        event.registerEntityRenderer(
            ModEntities.LIGHT_ORB.get(),
            LightOrbRenderer::new
        );
        event.registerEntityRenderer(
            ModEntities.LIGHTNING_BEAM.get(),
            LightningBeamRenderer::new
        );
        event.registerEntityRenderer(
            ModEntities.DUMMY_PLAYER.get(),
            DummyPlayerRenderer::new
        );
    }
    
}
