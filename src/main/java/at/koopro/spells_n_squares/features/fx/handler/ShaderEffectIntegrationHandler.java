package at.koopro.spells_n_squares.features.fx.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import at.koopro.spells_n_squares.features.creatures.hostile.DementorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Integrates post-processing shader effects into gameplay mechanics.
 * Applies shader effects based on player state, nearby entities, and game events.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ShaderEffectIntegrationHandler {
    
    private static final double DEMENTOR_EFFECT_RANGE = 12.0; // blocks
    private static final float DEMENTOR_GRAYSCALE_INTENSITY = 0.4f;
    private static final double BLOCK_LOOK_RANGE = 32.0; // blocks - max distance to detect block player is looking at
    
    // Track which effects were added by this handler (so we don't remove manually added ones)
    private static boolean dementorGrayscaleActive = false;
    private static boolean nauseaFisheyeActive = false;
    private static boolean darknessInvertedActive = false;
    private static boolean blindnessMosaicActive = false;
    
    // Track which block-based effects are currently active
    private static final Map<Identifier, Boolean> activeBlockEffects = new HashMap<>();
    
    // Block-based shader effects: maps block identifier to shader effect configuration
    private static final Map<Identifier, BlockShaderConfig> blockShaderEffects = new HashMap<>();
    
    /**
     * Configuration for a block-based shader effect.
     */
    public static class BlockShaderConfig {
        public final Identifier shaderId;
        public final float intensity;
        public final boolean lookAtOnly; // If true, only applies when looking at block. If false, applies when nearby.
        public final double range; // Range for proximity-based effects
        
        public BlockShaderConfig(Identifier shaderId, float intensity, boolean lookAtOnly, double range) {
            this.shaderId = shaderId;
            this.intensity = intensity;
            this.lookAtOnly = lookAtOnly;
            this.range = range;
        }
    }
    
    /**
     * Registers a shader effect for a specific block.
     * 
     * @param blockId The block identifier (e.g., "minecraft:beacon" or "spells_n_squares:magical_furnace")
     * @param shaderId The shader effect identifier
     * @param intensity The intensity of the effect (0.0 to 1.0)
     * @param lookAtOnly If true, effect only applies when looking at the block. If false, applies when nearby.
     * @param range The range for proximity-based effects (only used if lookAtOnly is false)
     */
    public static void registerBlockShaderEffect(Identifier blockId, Identifier shaderId, 
                                                  float intensity, boolean lookAtOnly, double range) {
        blockShaderEffects.put(blockId, new BlockShaderConfig(shaderId, intensity, lookAtOnly, range));
    }
    
    /**
     * Removes a block shader effect registration.
     */
    public static void unregisterBlockShaderEffect(Identifier blockId) {
        blockShaderEffects.remove(blockId);
    }
    
    /**
     * Applies shader effects based on player's current state (effects, nearby entities, etc.)
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.player.LocalPlayer player = mc != null ? mc.player : null;
        
        SafeEventHandler.execute(() -> {
            if (mc == null || mc.level == null || mc.player == null || !Config.areShaderEffectsEnabled()) {
                return;
            }
            
            // Check for dementor proximity effect
            checkDementorProximity(mc.player);
            
            // Check for status effect-based shader effects
            checkStatusEffectShaders(mc.player);
            
            // Check for block-based shader effects
            checkBlockShaderEffects(mc.player);
        }, "ticking shader effect integration", player);
    }
    
    /**
     * Applies shader effects when player takes damage (especially magical damage).
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        SafeEventHandler.execute(() -> {
            if (!event.getEntity().level().isClientSide()) {
                return;
            }
            
            LivingEntity entity = event.getEntity();
            Minecraft mc = Minecraft.getInstance();
            
            // Only apply to local player
            if (entity != mc.player) {
                return;
            }
            
            // Apply chromatic aberration for magical damage (disorientation)
            // Check if damage source is magic by checking the damage type identifier
            var damageSource = event.getSource();
            var damageType = damageSource.type();
            String damageTypeMsgId = damageType.msgId();
            if (damageTypeMsgId.contains("magic") || damageTypeMsgId.contains("indirect_magic")) {
                ShaderEffectHandler.triggerChromaticAberration(0.3f);
            }
        }, "handling living incoming damage for shader effects", event.getEntity() instanceof Player player ? player : null);
    }
    
    /**
     * Checks if player is near dementors and applies grayscale/inverted colors effect.
     */
    private static void checkDementorProximity(Player player) {
        if (!PostProcessingManager.isPostProcessingShaderAvailable(
                PostProcessingManager.GRAYSCALE_POST_SHADER)) {
            return;
        }
        
        // Check for nearby dementors
        var nearbyDementors = player.level().getEntitiesOfClass(
            DementorEntity.class,
            player.getBoundingBox().inflate(DEMENTOR_EFFECT_RANGE)
        );
        
        boolean shouldBeActive = !nearbyDementors.isEmpty();
        
        if (shouldBeActive && !dementorGrayscaleActive) {
            // Apply persistent grayscale effect while near dementors
            PostProcessingManager.addPersistentEffect(
                PostProcessingManager.GRAYSCALE_POST_SHADER,
                DEMENTOR_GRAYSCALE_INTENSITY
            );
            dementorGrayscaleActive = true;
        } else if (!shouldBeActive && dementorGrayscaleActive) {
            // Only remove if we're the ones who added it
            PostProcessingManager.removeEffect(PostProcessingManager.GRAYSCALE_POST_SHADER);
            dementorGrayscaleActive = false;
        }
    }
    
    /**
     * Applies shader effects based on player's active status effects.
     */
    private static void checkStatusEffectShaders(Player player) {
        // Check for NAUSEA effect - apply fisheye/chromatic aberration (disorientation)
        MobEffectInstance nausea = player.getEffect(MobEffects.NAUSEA);
        boolean shouldHaveNauseaEffect = nausea != null && PostProcessingManager.isPostProcessingShaderAvailable(
                PostProcessingManager.FISHEYE_POST_SHADER);
        
        if (shouldHaveNauseaEffect && !nauseaFisheyeActive) {
            float intensity = Math.min(0.5f, (nausea.getAmplifier() + 1) * 0.15f);
            PostProcessingManager.addPersistentEffect(
                PostProcessingManager.FISHEYE_POST_SHADER,
                intensity
            );
            nauseaFisheyeActive = true;
        } else if (!shouldHaveNauseaEffect && nauseaFisheyeActive) {
            // Only remove if we're the ones who added it
            PostProcessingManager.removeEffect(PostProcessingManager.FISHEYE_POST_SHADER);
            nauseaFisheyeActive = false;
        }
        
        // Check for DARKNESS effect - apply inverted colors or grayscale
        MobEffectInstance darkness = player.getEffect(MobEffects.DARKNESS);
        boolean shouldHaveDarknessEffect = darkness != null && PostProcessingManager.isPostProcessingShaderAvailable(
                PostProcessingManager.INVERTED_COLORS_POST_SHADER);
        
        if (shouldHaveDarknessEffect && !darknessInvertedActive) {
            float intensity = Math.min(0.6f, (darkness.getAmplifier() + 1) * 0.2f);
            PostProcessingManager.addPersistentEffect(
                PostProcessingManager.INVERTED_COLORS_POST_SHADER,
                intensity
            );
            darknessInvertedActive = true;
        } else if (!shouldHaveDarknessEffect && darknessInvertedActive) {
            // Only remove if we're the ones who added it
            PostProcessingManager.removeEffect(PostProcessingManager.INVERTED_COLORS_POST_SHADER);
            darknessInvertedActive = false;
        }
        
        // Check for BLINDNESS effect - apply mosaic (vision impairment)
        MobEffectInstance blindness = player.getEffect(MobEffects.BLINDNESS);
        boolean shouldHaveBlindnessEffect = blindness != null && PostProcessingManager.isPostProcessingShaderAvailable(
                PostProcessingManager.MOSAIC_POST_SHADER);
        
        if (shouldHaveBlindnessEffect && !blindnessMosaicActive) {
            float intensity = Math.min(0.7f, (blindness.getAmplifier() + 1) * 0.25f);
            PostProcessingManager.addPersistentEffect(
                PostProcessingManager.MOSAIC_POST_SHADER,
                intensity
            );
            blindnessMosaicActive = true;
        } else if (!shouldHaveBlindnessEffect && blindnessMosaicActive) {
            // Only remove if we're the ones who added it
            PostProcessingManager.removeEffect(PostProcessingManager.MOSAIC_POST_SHADER);
            blindnessMosaicActive = false;
        }
    }
    
    /**
     * Checks for block-based shader effects (looking at or near specific blocks).
     */
    private static void checkBlockShaderEffects(Player player) {
        if (blockShaderEffects.isEmpty() || !Config.areShaderEffectsEnabled()) {
            return;
        }
        
        // Check what block player is looking at
        HitResult hitResult = player.pick(BLOCK_LOOK_RANGE, 1.0f, false);
        BlockState lookedAtState = null;
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            lookedAtState = player.level().getBlockState(blockHit.getBlockPos());
        }
        
        // Check for nearby blocks (for proximity-based effects)
        BlockPos playerPos = player.blockPosition();
        
        for (Map.Entry<Identifier, BlockShaderConfig> entry : blockShaderEffects.entrySet()) {
            Identifier blockId = entry.getKey();
            BlockShaderConfig config = entry.getValue();
            
            boolean shouldBeActive = false;
            
            if (config.lookAtOnly) {
                // Only check if player is looking at the block
                if (lookedAtState != null) {
                    Block block = lookedAtState.getBlock();
                    Identifier blockKey = BuiltInRegistries.BLOCK.getKey(block);
                    if (blockKey != null && blockKey.equals(blockId)) {
                        shouldBeActive = true;
                    }
                }
            } else {
                // Check for nearby blocks within range
                int searchRadius = (int) Math.ceil(config.range);
                for (int x = -searchRadius; x <= searchRadius; x++) {
                    for (int y = -searchRadius; y <= searchRadius; y++) {
                        for (int z = -searchRadius; z <= searchRadius; z++) {
                            BlockPos checkPos = playerPos.offset(x, y, z);
                            double distance = Math.sqrt(
                                (x * x) + (y * y) + (z * z)
                            );
                            
                            if (distance <= config.range) {
                                BlockState state = player.level().getBlockState(checkPos);
                                Block block = state.getBlock();
                                Identifier blockKey = BuiltInRegistries.BLOCK.getKey(block);
                                
                                if (blockKey != null && blockKey.equals(blockId)) {
                                    shouldBeActive = true;
                                    break;
                                }
                            }
                        }
                        if (shouldBeActive) break;
                    }
                    if (shouldBeActive) break;
                }
            }
            
            // Get previous state
            boolean wasActive = activeBlockEffects.getOrDefault(blockId, false);
            
            // Apply or remove effect based on condition
            if (shouldBeActive && !wasActive) {
                // Condition just became true - add effect
                if (PostProcessingManager.isPostProcessingShaderAvailable(config.shaderId)) {
                    PostProcessingManager.addPersistentEffect(config.shaderId, config.intensity);
                    activeBlockEffects.put(blockId, true);
                }
            } else if (!shouldBeActive && wasActive) {
                // Condition just became false - remove effect
                // Only remove if we're tracking it (block-based effect)
                PostProcessingManager.removeEffect(config.shaderId);
                activeBlockEffects.put(blockId, false);
            }
        }
    }
}

