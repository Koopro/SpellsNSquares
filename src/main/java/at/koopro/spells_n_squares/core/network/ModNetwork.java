package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.registry.AddonRegistry;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.spell.manager.LumosManager;
import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.spell.network.LumosStateSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.LumosTogglePayload;
import at.koopro.spells_n_squares.features.spell.network.SpellCastPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellShootPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotsSyncPayload;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.data.PlayerIdentityData;
import at.koopro.spells_n_squares.core.data.PlayerIdentityHelper;
import at.koopro.spells_n_squares.features.artifact.network.ImmortalitySyncPayload;
import at.koopro.spells_n_squares.features.artifact.client.ClientImmortalityCache;
import at.koopro.spells_n_squares.features.enchantments.network.EnchantmentRequestPayload;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentHelper;
import at.koopro.spells_n_squares.features.enchantments.block.EnchantmentTableBlockEntity;
import at.koopro.spells_n_squares.features.contracts.network.ContractCreationPayload;
import at.koopro.spells_n_squares.features.contracts.ContractData;
import at.koopro.spells_n_squares.features.contracts.ContractStorage;
import at.koopro.spells_n_squares.features.mail.network.MailSendPayload;
import at.koopro.spells_n_squares.features.mail.MailData;
import at.koopro.spells_n_squares.features.mail.MailStorage;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.spell.movement.WingardiumLeviosaSpell;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.fx.handler.CutEffectHandler;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingPipelineManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.logging.LogUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles network packet registration and processing for the mod.
 */
public class ModNetwork {
    
    /**
     * Helper method to safely execute server-side code.
     * Includes error handling for network payload processing.
     * @param context The payload context
     * @param action The action to execute if player is a ServerPlayer
     */
    private static void executeOnServer(IPayloadContext context, Consumer<ServerPlayer> action) {
        context.enqueueWork(() -> {
            try {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    action.accept(serverPlayer);
                } else {
                    LogUtils.getLogger().warn("[ModNetwork] executeOnServer: Context player is not a ServerPlayer");
                }
            } catch (Exception e) {
                LogUtils.getLogger().error("[ModNetwork] executeOnServer: Error executing server-side action", e);
            }
        });
    }
    
    /**
     * Helper method to safely execute client-side code.
     * Includes error handling for network payload processing.
     * @param context The payload context
     * @param action The action to execute if on client side
     */
    private static void executeOnClient(IPayloadContext context, Runnable action) {
        context.enqueueWork(() -> {
            try {
                if (context.player() != null && context.player().level().isClientSide()) {
                    action.run();
                }
            } catch (Exception e) {
                LogUtils.getLogger().error("[ModNetwork] executeOnClient: Error executing client-side action", e);
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
        DevLogger.logMethodEntry(ModNetwork.class, "registerPayloadHandlers");
        final var registrar = event.registrar("1");
        
        // Register addon network payloads first
        AddonRegistry.registerAllAddonNetworkPayloads(registrar);
        DevLogger.logDebug(ModNetwork.class, "registerPayloadHandlers", "Registered addon network payloads");
        
        // Register spell shoot payload (client -> server)
        registerToServer(registrar, SpellShootPayload.TYPE, SpellShootPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "SpellShootPayload", "SpellShootPayload", "RECEIVE", 
                    "player=" + (serverPlayer != null ? serverPlayer.getName().getString() : "null"));
                // Handle shooting entities/blocks away while holding a spell
                if (serverPlayer != null && serverPlayer.level() instanceof ServerLevel serverLevel) {
                    Identifier activeHoldSpell = SpellManager.getActiveHoldSpell(serverPlayer);
                    if (activeHoldSpell != null) {
                        Spell spell = SpellRegistry.get(activeHoldSpell);
                        if (spell != null && spell instanceof WingardiumLeviosaSpell) {
                            // Shoot entities away for Wingardium Leviosa
                            WingardiumLeviosaSpell.shootEntities(serverPlayer, serverLevel);
                        }
                    }
                }
            }, "handling SpellShootPayload", serverPlayer);
        });
        
        // Register character creation payload (client -> server)
        registerToServer(registrar, CharacterCreationPayload.TYPE, CharacterCreationPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "CharacterCreationPayload", "CharacterCreationPayload", "RECEIVE", 
                    "bloodStatus=" + payload.bloodStatus() + ", magicalType=" + payload.magicalType() + 
                    ", player=" + (serverPlayer != null ? serverPlayer.getName().getString() : "null"));
                
                // Validate the combination
                if (!PlayerIdentityHelper.isValidCombination(payload.bloodStatus(), payload.magicalType())) {
                    DevLogger.logWarn(ModNetwork.class, "CharacterCreationPayload", 
                        "Invalid combination: " + payload.bloodStatus() + " + " + payload.magicalType());
                    return;
                }
                
                // Create identity data
                PlayerIdentityData.IdentityData identity = new PlayerIdentityData.IdentityData(
                    payload.bloodStatus(),
                    payload.magicalType()
                );
                
                // Save to player data
                PlayerDataHelper.setIdentityData(serverPlayer, identity);
                
                // Apply race-based size scaling
                PlayerIdentityHelper.applyRaceScaling(serverPlayer);
                
                DevLogger.logStateChange(ModNetwork.class, "CharacterCreationPayload", 
                    "Set player identity: " + payload.magicalType() + " (" + payload.bloodStatus() + ")");
            }, "handling CharacterCreationPayload", serverPlayer);
        });
        
        // Register spell cast payload (client -> server)
        registerToServer(registrar, SpellCastPayload.TYPE, SpellCastPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "SpellCastPayload", "SpellCastPayload", "RECEIVE", 
                    "slot=" + payload.slot() + ", player=" + (serverPlayer != null ? serverPlayer.getName().getString() : "null"));
                
                int slot = payload.slot();
                
                // Handle stopping hold spells (slot = -1)
                if (slot == -1) {
                    DevLogger.logDebug(ModNetwork.class, "SpellCastPayload", "Stopping hold spell");
                    SpellManager.stopHoldSpell(serverPlayer);
                    return;
                }
                
                // Validate slot
                if (!SpellManager.isValidSlot(slot)) {
                    DevLogger.logWarn(ModNetwork.class, "SpellCastPayload", "Invalid slot: " + slot);
                    return; // Invalid slot, ignore
                }
                
                // Cast the spell on the server
                if (serverPlayer != null && serverPlayer.level() instanceof ServerLevel serverLevel) {
                    Identifier spellId = SpellManager.getSpellInSlot(serverPlayer, slot);
                    if (spellId != null) {
                        Spell spell = SpellRegistry.get(spellId);
                        if (spell != null && spell.isHoldToCast()) {
                            // Check if player is already holding this spell to prevent duplicate casts
                            Identifier activeHoldSpell = SpellManager.getActiveHoldSpell(serverPlayer);
                            if (activeHoldSpell == null || !activeHoldSpell.equals(spellId)) {
                                // Start hold-to-cast spell (set active BEFORE casting to suppress effects)
                                DevLogger.logDebug(ModNetwork.class, "SpellCastPayload", "Starting hold spell: " + spellId);
                                SpellManager.startHoldSpell(serverPlayer, spellId);
                                // Initial cast to start the effect (effects will be suppressed because spell is now active)
                                SpellManager.castSpellInSlot(serverPlayer, serverLevel, slot);
                            }
                            // If already holding this spell, do nothing - tickHoldSpells will handle it
                        } else {
                            // Normal one-time cast
                            SpellManager.castSpellInSlot(serverPlayer, serverLevel, slot);
                        }
                    } else {
                        DevLogger.logWarn(ModNetwork.class, "SpellCastPayload", "No spell in slot " + slot);
                    }
                }
            }, "handling SpellCastPayload", serverPlayer);
        });
        
        // Register spell slots sync payload (server -> client)
        registerToClient(registrar, SpellSlotsSyncPayload.TYPE, SpellSlotsSyncPayload.STREAM_CODEC, payload -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "SpellSlotsSyncPayload", "SpellSlotsSyncPayload", "RECEIVE", "");
                // Use loop with SpellManager.SLOTS array for consistency
                for (int slot : SpellManager.SLOTS) {
                    ClientSpellData.setSpellInSlot(slot, payload.getSlot(slot).orElse(null));
                }
            }, "handling SpellSlotsSyncPayload");
        });
        
        // Register spell cooldown sync payload (server -> client)
        registerToClient(registrar, SpellCooldownSyncPayload.TYPE, SpellCooldownSyncPayload.STREAM_CODEC, payload -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "SpellCooldownSyncPayload", "SpellCooldownSyncPayload", "RECEIVE", "");
                ClientSpellData.updateCooldowns(payload.toMap());
            }, "handling SpellCooldownSyncPayload");
        });
        
        // Register player model sync payload (server -> client)
        registrar.playToClient(PlayerModelSyncPayload.TYPE, PlayerModelSyncPayload.STREAM_CODEC, (payload, context) -> {
            SafeEventHandler.execute(() -> {
                // Store model data on client for rendering
                context.enqueueWork(() -> {
                    if (context.player() != null) {
                        var player = context.player();
                        var modelData = payload.toModelData();
                        
                        // Only log if data is non-default (to reduce log spam)
                        if (modelData.scale() != 1.0f || modelData.hitboxScale() != 1.0f || 
                            modelData.bodyScale() != 1.0f || modelData.headScale() != 1.0f) {
                            LogUtils.getLogger().debug("[ModNetwork] PlayerModelSyncPayload: Received model data for player {}: scale={}, hitboxScale={}, bodyScale={}, headScale={}", 
                                player.getName().getString(), modelData.scale(), modelData.hitboxScale(), modelData.bodyScale(), modelData.headScale());
                        }
                        
                        at.koopro.spells_n_squares.core.util.player.PlayerModelUtils.updateClientModelData(
                            player.getUUID(), modelData);
                    } else {
                        DevLogger.logError(ModNetwork.class, "PlayerModelSyncPayload", 
                            "Context player is null in enqueueWork", null);
                    }
                });
            }, "handling PlayerModelSyncPayload");
        });
        
        // Register spell slot assignment payload (client -> server)
        registerToServer(registrar, SpellSlotAssignPayload.TYPE, SpellSlotAssignPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                // Validate slot
                if (!SpellManager.isValidSlot(payload.slot())) {
                    return; // Invalid slot, ignore
                }
                
                // Assign spell to slot
                SpellManager.setSpellInSlot(serverPlayer, payload.slot(), payload.spellId().orElse(null));
            }, "handling SpellSlotAssignPayload", serverPlayer);
        });
        
        // Register lumos state sync payload (server -> client)
        registrar.playToClient(
            LumosStateSyncPayload.TYPE,
            LumosStateSyncPayload.STREAM_CODEC,
            (payload, context) -> executeOnClient(context, () -> {
                SafeEventHandler.execute(() -> {
                    if (context.player() != null) {
                        LumosManager.setLumosActive(context.player(), payload.active());
                    }
                }, "handling LumosStateSyncPayload");
            })
        );

        // Register immortality sync payload (server -> client)
        registerToClient(registrar, ImmortalitySyncPayload.TYPE, ImmortalitySyncPayload.STREAM_CODEC, payload -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "ImmortalitySyncPayload", "ImmortalitySyncPayload", "RECEIVE", 
                    "ticksRemaining=" + payload.ticksRemaining() + ", hasEverDrunk=" + payload.hasEverDrunk());
                if (Minecraft.getInstance().player != null) {
                    ClientImmortalityCache.update(Minecraft.getInstance().player, payload.toComponent());
                }
            }, "handling ImmortalitySyncPayload");
        });
        
        // Register lumos toggle payload (client -> server)
        registerToServer(registrar, LumosTogglePayload.TYPE, LumosTogglePayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                var stack = serverPlayer.getItemInHand(payload.hand());
                if (stack.is(ModTags.WANDS)) {
                    LumosManager.toggleLumos(serverPlayer, stack);
                }
            }, "handling LumosTogglePayload", serverPlayer);
        });
        
        // Register enchantment request payload (client -> server)
        registerToServer(registrar, EnchantmentRequestPayload.TYPE, EnchantmentRequestPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "EnchantmentRequestPayload", "EnchantmentRequestPayload", "RECEIVE",
                    "tablePos=" + payload.tablePos() + ", enchantmentId=" + payload.enchantmentId() + ", level=" + payload.enchantmentLevel());
                
                // Get the BlockEntity
                if (serverPlayer.level().getBlockEntity(payload.tablePos()) instanceof EnchantmentTableBlockEntity blockEntity) {
                    var itemStack = blockEntity.getItem();
                    if (itemStack.isEmpty()) {
                        DevLogger.logWarn(ModNetwork.class, "EnchantmentRequestPayload", "No item in enchantment table");
                        return;
                    }
                    
                    // Apply the enchantment
                    boolean success = EnchantmentHelper.applyEnchantment(itemStack, payload.enchantmentId(), payload.enchantmentLevel());
                    if (success) {
                        blockEntity.setItem(itemStack);
                        DevLogger.logStateChange(ModNetwork.class, "EnchantmentRequestPayload",
                            "Applied enchantment " + payload.enchantmentId() + " level " + payload.enchantmentLevel() + " to item");
                    } else {
                        DevLogger.logWarn(ModNetwork.class, "EnchantmentRequestPayload",
                            "Failed to apply enchantment " + payload.enchantmentId() + " to item");
                    }
                } else {
                    DevLogger.logWarn(ModNetwork.class, "EnchantmentRequestPayload",
                        "No EnchantmentTableBlockEntity found at " + payload.tablePos());
                }
            }, "handling EnchantmentRequestPayload", serverPlayer);
        });
        
        // Register contract creation payload (client -> server)
        registerToServer(registrar, ContractCreationPayload.TYPE, ContractCreationPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "ContractCreationPayload", "ContractCreationPayload", "RECEIVE",
                    "targetPlayer=" + payload.targetPlayerName() + ", textLength=" + payload.contractText().length());
                
                // Validate input
                if (payload.targetPlayerName() == null || payload.targetPlayerName().trim().isEmpty()) {
                    DevLogger.logWarn(ModNetwork.class, "ContractCreationPayload", "Invalid target player name");
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Please enter a valid target player name.", ColorUtils.SPELL_RED));
                    return;
                }
                
                if (payload.contractText() == null || payload.contractText().trim().isEmpty()) {
                    DevLogger.logWarn(ModNetwork.class, "ContractCreationPayload", "Invalid contract text");
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Contract text cannot be empty.", ColorUtils.SPELL_RED));
                    return;
                }
                
                // Find target player
                if (!(serverPlayer.level() instanceof ServerLevel serverLevel)) {
                    DevLogger.logWarn(ModNetwork.class, "ContractCreationPayload", "Server level is null");
                    return;
                }
                var server = serverLevel.getServer();
                var playerList = server.getPlayerList();
                ServerPlayer targetPlayer = playerList.getPlayerByName(payload.targetPlayerName());
                if (targetPlayer == null) {
                    DevLogger.logWarn(ModNetwork.class, "ContractCreationPayload", 
                        "Target player not found: " + payload.targetPlayerName());
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Player '" + payload.targetPlayerName() + "' is not online or does not exist.", ColorUtils.SPELL_RED));
                    return;
                }
                
                // Create contract
                ContractData contract = ContractData.create(
                    serverPlayer.getUUID(),
                    serverPlayer.getName().getString(),
                    targetPlayer.getUUID(),
                    targetPlayer.getName().getString(),
                    payload.contractText().trim()
                );
                
                // Store contract
                ContractStorage.addContract(contract);
                
                DevLogger.logStateChange(ModNetwork.class, "ContractCreationPayload",
                    "Created contract " + contract.contractId() + " between " + serverPlayer.getName().getString() + 
                    " and " + targetPlayer.getName().getString());
                
                // Send notification to target player
                targetPlayer.sendSystemMessage(
                    ColorUtils.coloredText("You have received a new contract from " + serverPlayer.getName().getString() + ".", 
                        ColorUtils.SPELL_GREEN));
            }, "handling ContractCreationPayload", serverPlayer);
        });
        
        // Register mail send payload (client -> server)
        registerToServer(registrar, MailSendPayload.TYPE, MailSendPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            SafeEventHandler.execute(() -> {
                DevLogger.logNetworkPacket(ModNetwork.class, "MailSendPayload", "MailSendPayload", "RECEIVE",
                    "recipient=" + payload.recipientName() + ", subjectLength=" + payload.subject().length() + ", messageLength=" + payload.message().length());
                
                // Validate input
                if (payload.recipientName() == null || payload.recipientName().trim().isEmpty()) {
                    DevLogger.logWarn(ModNetwork.class, "MailSendPayload", "Invalid recipient name");
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Please enter a valid recipient name.", ColorUtils.SPELL_RED));
                    return;
                }
                
                if (payload.message() == null || payload.message().trim().isEmpty()) {
                    DevLogger.logWarn(ModNetwork.class, "MailSendPayload", "Invalid message");
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Mail message cannot be empty.", ColorUtils.SPELL_RED));
                    return;
                }
                
                // Find recipient player
                if (!(serverPlayer.level() instanceof ServerLevel serverLevel)) {
                    DevLogger.logWarn(ModNetwork.class, "MailSendPayload", "Server level is null");
                    return;
                }
                var server = serverLevel.getServer();
                var playerList = server.getPlayerList();
                ServerPlayer recipient = playerList.getPlayerByName(payload.recipientName());
                
                if (recipient == null) {
                    DevLogger.logWarn(ModNetwork.class, "MailSendPayload", 
                        "Recipient player not found: " + payload.recipientName());
                    serverPlayer.sendSystemMessage(
                        ColorUtils.coloredText("Error: Player '" + payload.recipientName() + "' is not online or does not exist.", ColorUtils.SPELL_RED));
                    return;
                }
                
                // Create mail
                MailData mail = MailData.create(
                    serverPlayer.getUUID(),
                    serverPlayer.getName().getString(),
                    recipient.getUUID(),
                    recipient.getName().getString(),
                    payload.subject() != null ? payload.subject().trim() : "No Subject",
                    payload.message().trim()
                );
                
                // Add to mail storage
                MailStorage.addMail(mail);
                
                // Try to deliver immediately if recipient is online
                MailStorage.deliverPendingMail(recipient, serverLevel);
                
                DevLogger.logStateChange(ModNetwork.class, "MailSendPayload",
                    "Created mail " + mail.mailId() + " from " + serverPlayer.getName().getString() + 
                    " to " + recipient.getName().getString());
            }, "handling MailSendPayload", serverPlayer);
        });
        
        // Register FX test payload (server -> client) for testing screen effects
        registerToClient(registrar, FXTestPayload.TYPE, FXTestPayload.STREAM_CODEC, payload -> {
            Minecraft.getInstance().execute(() -> {
                SafeEventHandler.execute(() -> {
                    switch (payload.effectType()) {
                    case "shake":
                        ScreenEffectManager.triggerShake(
                            payload.param1(), (int)payload.param2());
                        break;
                    case "flash":
                        ScreenEffectManager.triggerSpellFlash();
                        break;
                    case "vignette":
                        ScreenEffectManager.triggerDamageVignette();
                        break;
                    case "cut":
                        // Use screen center coordinates (normalized 0-1 for screen space)
                        Minecraft mc = Minecraft.getInstance();
                        int width = mc.getWindow().getGuiScaledWidth();
                        int height = mc.getWindow().getGuiScaledHeight();
                        float centerX = width * 0.5f;
                        float centerY = height * 0.5f;
                        Vec3 start = new Vec3(centerX - 50, centerY, 0);
                        Vec3 end = new Vec3(centerX + 50, centerY, 0);
                        CutEffectHandler.triggerCut(
                            start, end, payload.param1(), payload.param3(), true);
                        break;
                    case "pipeline_status":
                        // Check and log shader pipeline status
                        LogUtils.getLogger().info("=== Shader Pipeline Status ===");
                        RenderPipeline lumosPipeline =
                            PostProcessingPipelineManager.getLumosOrbRenderPipeline();
                        RenderPipeline cutPipeline =
                            PostProcessingPipelineManager.getCutEffectRenderPipeline();
                        
                        boolean lumosValid = lumosPipeline != null &&
                                PostProcessingPipelineManager.isPipelineValid(lumosPipeline);
                        boolean cutValid = cutPipeline != null &&
                                PostProcessingPipelineManager.isPipelineValid(cutPipeline);
                        
                        LogUtils.getLogger().info("Lumos Orb Pipeline: {}", 
                            lumosValid ? "VALID" : "INVALID or NOT LOADED");
                        LogUtils.getLogger().info("Cut Effect Pipeline: {}", 
                            cutValid ? "VALID" : "INVALID or NOT LOADED");
                        LogUtils.getLogger().info("Check client logs for detailed shader compilation messages");
                        break;
                    case "shader":
                        // Use the shader type string directly from payload
                        String shaderType = payload.shaderType();
                        if (shaderType == null || shaderType.isEmpty()) {
                            shaderType = "spell_cast"; // Default
                        }
                        ShaderEffectHandler.triggerShaderEffect(
                            shaderType, payload.param1());
                        break;
                    }
                }, "handling FXTestPayload");
            });
        });
        
        DevLogger.logMethodExit(ModNetwork.class, "registerPayloadHandlers");
    }
}