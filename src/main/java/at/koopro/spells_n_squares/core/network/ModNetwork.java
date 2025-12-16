package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.registry.AddonRegistry;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.features.playerclass.network.PlayerClassSyncPayload;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.spell.network.LumosStateSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.LumosTogglePayload;
import at.koopro.spells_n_squares.features.convenience.WaypointSystem;
import at.koopro.spells_n_squares.features.convenience.network.WaypointListPayload;
import at.koopro.spells_n_squares.features.convenience.network.WaypointSelectionPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles network packet registration and processing for the mod.
 */
public class ModNetwork {
    
    /**
     * Helper method to safely execute server-side code.
     * @param context The payload context
     * @param action The action to execute if player is a ServerPlayer
     */
    private static void executeOnServer(IPayloadContext context, java.util.function.Consumer<ServerPlayer> action) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                action.accept(serverPlayer);
            }
        });
    }
    
    /**
     * Helper method to safely execute client-side code.
     * @param context The payload context
     * @param action The action to execute if on client side
     */
    private static void executeOnClient(IPayloadContext context, Runnable action) {
        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level().isClientSide()) {
                action.run();
            }
        });
    }
    
    /**
     * Helper method to register a payload handler for client-to-server communication.
     * Wraps the common pattern of executeOnServer for cleaner registration code.
     * 
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload and ServerPlayer
     * @param <T> The payload type
     */
    private static <T extends CustomPacketPayload> void registerToServer(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler
    ) {
        registrar.playToServer(type, codec, (payload, context) -> 
            executeOnServer(context, serverPlayer -> handler.accept(payload, serverPlayer))
        );
    }
    
    /**
     * Helper method to register a payload handler for server-to-client communication.
     * Wraps the common pattern of executeOnClient for cleaner registration code.
     * 
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload
     * @param <T> The payload type
     */
    private static <T extends CustomPacketPayload> void registerToClient(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            Consumer<T> handler
    ) {
        registrar.playToClient(type, codec, (payload, context) -> 
            executeOnClient(context, () -> handler.accept(payload))
        );
    }
    
    /**
     * Registers all custom network payloads.
     * This should be called from the mod's event bus during initialization.
     */
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        
        // Register addon network payloads first
        AddonRegistry.registerAllAddonNetworkPayloads(registrar);
        
        // Register spell cast payload (client -> server)
        registerToServer(registrar, SpellCastPayload.TYPE, SpellCastPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Validate slot
            if (!SpellManager.isValidSlot(payload.slot())) {
                return; // Invalid slot, ignore
            }
            
            // Cast the spell on the server
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                SpellManager.castSpellInSlot(serverPlayer, serverLevel, payload.slot());
            }
        });
        
        // Register spell slots sync payload (server -> client)
        registerToClient(registrar, SpellSlotsSyncPayload.TYPE, SpellSlotsSyncPayload.STREAM_CODEC, payload -> {
            // Use loop with SpellManager.SLOTS array for consistency
            for (int slot : SpellManager.SLOTS) {
                ClientSpellData.setSpellSlot(slot, payload.getSlot(slot).orElse(null));
            }
        });
        
        // Register spell cooldown sync payload (server -> client)
        registerToClient(registrar, SpellCooldownSyncPayload.TYPE, SpellCooldownSyncPayload.STREAM_CODEC, payload -> {
            ClientSpellData.updateCooldowns(payload.toMap());
        });
        
        // Register spell slot assignment payload (client -> server)
        registerToServer(registrar, SpellSlotAssignPayload.TYPE, SpellSlotAssignPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Validate slot
            if (!SpellManager.isValidSlot(payload.slot())) {
                return; // Invalid slot, ignore
            }
            
            // Assign spell to slot
            SpellManager.setSpellInSlot(serverPlayer, payload.slot(), payload.spellId().orElse(null));
        });
        
        // Register player class sync payload (server -> client)
        registerToClient(registrar, PlayerClassSyncPayload.TYPE, PlayerClassSyncPayload.STREAM_CODEC, payload -> {
            ClientSpellData.setPlayerClass(payload.playerClass());
        });
        
        // Register lumos state sync payload (server -> client)
        registrar.playToClient(
            LumosStateSyncPayload.TYPE,
            LumosStateSyncPayload.STREAM_CODEC,
            (payload, context) -> executeOnClient(context, () -> {
                if (context.player() != null) {
                    LumosManager.setLumosActive(context.player(), payload.active());
                }
            })
        );

        // Register lumos toggle payload (client -> server)
        registerToServer(registrar, LumosTogglePayload.TYPE, LumosTogglePayload.STREAM_CODEC, (payload, serverPlayer) -> {
            var stack = serverPlayer.getItemInHand(payload.hand());
            if (stack.is(ModTags.WANDS)) {
                LumosManager.toggleLumos(serverPlayer, stack);
            }
        });
        
        // Register waypoint list payload (server -> client)
        registerToClient(registrar, WaypointListPayload.TYPE, WaypointListPayload.STREAM_CODEC, payload -> {
            // Open waypoint selection screen on client
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                var waypoints = payload.waypoints().stream()
                    .map(wp -> new WaypointSystem.Waypoint(
                        wp.name(),
                        wp.dimension(),
                        wp.position(),
                        wp.createdTick()
                    ))
                    .toList();
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.convenience.client.WaypointSelectionScreen(waypoints)
                );
            });
        });
        
        // Register waypoint selection payload (client -> server)
        registerToServer(registrar, WaypointSelectionPayload.TYPE, WaypointSelectionPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Delegate teleportation to WaypointSystem
            WaypointSystem.apparateToWaypoint(serverPlayer, payload.waypointName());
        });
    }
}

