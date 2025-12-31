package at.koopro.spells_n_squares.features.flashlight.handler;

import at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry;
import at.koopro.spells_n_squares.features.flashlight.item.FlashlightItem;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Client-side handler for flashlight visual light overlay effect.
 * Adds a screen-space light overlay that makes it appear as if light is coming from the flashlight.
 * This works in combination with the light block system for actual world lighting.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class FlashlightShaderHandler {
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        SafeEventHandler.execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) {
                return;
            }
            
            Player player = mc.player;
            
            // Find held flashlight using utility
            var flashlightStackOpt = PlayerItemUtils.findHeldItem(player, FlashlightRegistry.FLASHLIGHT.get());
            boolean hasFlashlight = flashlightStackOpt.isPresent();
            boolean isOn = flashlightStackOpt.map(FlashlightItem::isOn).orElse(false);
            
            if (!hasFlashlight || !isOn) {
                return;
            }
            
            // Disabled overlay - using light blocks for actual lighting instead
            // Uncomment below to enable visual overlay effect
            // renderFlashlightOverlay(event, player);
        }, "rendering flashlight GUI");
    }
    
}
