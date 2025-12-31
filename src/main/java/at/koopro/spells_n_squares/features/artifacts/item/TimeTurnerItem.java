package at.koopro.spells_n_squares.features.artifacts.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Time-Turner artifact that allows players to rewind world time.
 */
public class TimeTurnerItem extends Item {
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TimeTurnerData>> TIME_TURNER_DATA =
        DATA_COMPONENTS.register(
            "time_turner_data",
            () -> DataComponentType.<TimeTurnerData>builder()
                .persistent(TimeTurnerData.CODEC)
                .build()
        );
    
    private static final int COOLDOWN_TICKS = 6000; // 5 minutes
    private static final int TIME_REWIND_TICKS = 12000; // 12 hours (half a day)
    private static final int TIME_ACCELERATION_TICKS = 100; // +100 ticks per tick
    private static final int XP_DRAIN_INTERVAL = 20; // Drain XP every second (20 ticks)
    private static final int SOUND_INTERVAL = 10; // Play sound every 0.5 seconds (10 ticks)
    private static final int DEATH_PREVENTION_COOLDOWN = 6000; // 5 minutes
    private static final int DEATH_PREVENTION_DURABILITY_COST = 500;
    private static final int QUICK_CLICK_THRESHOLD = 10; // If released within 10 ticks, treat as quick click (time rewind)
    
    public record TimeTurnerData(
        int lastUseTick,
        double anchorX,
        double anchorY,
        double anchorZ,
        String anchorDimension,
        long lastDeathPreventionTick
    ) {
        public static final Codec<TimeTurnerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("lastUse").forGetter(TimeTurnerData::lastUseTick),
                Codec.DOUBLE.optionalFieldOf("anchorX", 0.0).forGetter(TimeTurnerData::anchorX),
                Codec.DOUBLE.optionalFieldOf("anchorY", 0.0).forGetter(TimeTurnerData::anchorY),
                Codec.DOUBLE.optionalFieldOf("anchorZ", 0.0).forGetter(TimeTurnerData::anchorZ),
                Codec.STRING.optionalFieldOf("anchorDimension", "").forGetter(TimeTurnerData::anchorDimension),
                Codec.LONG.optionalFieldOf("lastDeathPrevention", 0L).forGetter(TimeTurnerData::lastDeathPreventionTick)
            ).apply(instance, TimeTurnerData::new)
        );
        
        public boolean hasAnchor() {
            return anchorDimension != null && !anchorDimension.isEmpty();
        }
    }
    
    public TimeTurnerItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    /**
     * Gets the TimeTurnerData from an ItemStack, creating default if missing.
     */
    private static TimeTurnerData getData(ItemStack stack) {
        TimeTurnerData data = stack.get(TIME_TURNER_DATA.get());
        if (data == null) {
            return new TimeTurnerData(0, 0.0, 0.0, 0.0, "", 0L);
        }
        return data;
    }
    
    /**
     * Updates the TimeTurnerData on an ItemStack.
     */
    private static void setData(ItemStack stack, TimeTurnerData data) {
        stack.set(TIME_TURNER_DATA.get(), data);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            // On client, if shift-clicking, consume immediately for anchor setting
            // Otherwise, start using for channeling
            if (player.isCrouching()) {
                return InteractionResult.SUCCESS;
            } else {
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            }
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        TimeTurnerData data = getData(stack);
        
        // Shift + Right-Click: Set Temporal Anchor
        if (player.isCrouching()) {
            // Store dimension as string identifier (format: "minecraft:overworld")
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = serverLevel.dimension();
            String dimensionId = dimKey.toString();
            TimeTurnerData newData = new TimeTurnerData(
                data.lastUseTick(),
                player.getX(),
                player.getY(),
                player.getZ(),
                dimensionId,
                data.lastDeathPreventionTick()
            );
            setData(stack, newData);
            
            serverPlayer.displayClientMessage(Component.literal("§6Temporal Anchor Set."), true);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 2.0f);
            
            return InteractionResult.SUCCESS;
        }
        
        // Start using for channeling (hold right-click for time acceleration)
        // Time rewind will be handled in releaseUsing if it was a quick click
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }
    
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide() && entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            // Accelerate Time (Add 100 ticks per game tick)
            long currentDayTime = serverLevel.getDayTime();
            serverLevel.setDayTime(currentDayTime + TIME_ACCELERATION_TICKS);
            
            // Play ticking sound every 0.5 seconds (10 ticks)
            if (remainingUseDuration % SOUND_INTERVAL == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.3f, 1.5f);
            }
            
            // Drain XP every second (20 ticks)
            if (remainingUseDuration % XP_DRAIN_INTERVAL == 0) {
                if (player.totalExperience > 0) {
                    player.giveExperiencePoints(-1);
                } else {
                    // Stop using if no XP left
                    player.stopUsingItem();
                }
            }
            
            // Display action bar message periodically
            if (remainingUseDuration % 40 == 0) {
                player.displayClientMessage(Component.literal("§eThe flow of time accelerates..."), true);
            }
        }
    }
    
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }
    
    public int getUseDuration(ItemStack stack) {
        return 72000; // Allows holding for a long time
    }
    
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!level.isClientSide() && entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            // Calculate how long the item was used
            int useDuration = getUseDuration(stack);
            int actualUseTime = useDuration - timeLeft;
            
            // If it was a quick click (released within threshold), do time rewind
            if (actualUseTime <= QUICK_CLICK_THRESHOLD) {
                TimeTurnerData data = getData(stack);
                long currentTick = serverLevel.getGameTime();
                
                // Check cooldown for time rewind
                long timeSinceLastUse = currentTick - data.lastUseTick();
                if (timeSinceLastUse >= COOLDOWN_TICKS) {
                    // Calculate new world time (rewind by 12 hours)
                    long currentDayTime = serverLevel.getDayTime();
                    long newDayTime = Math.max(0, currentDayTime - TIME_REWIND_TICKS);
                    
                    // Set the new world time
                    serverLevel.setDayTime(newDayTime);
                    
                    // Update last use time
                    TimeTurnerData newData = new TimeTurnerData(
                        (int) currentTick,
                        data.anchorX(),
                        data.anchorY(),
                        data.anchorZ(),
                        data.anchorDimension(),
                        data.lastDeathPreventionTick()
                    );
                    setData(stack, newData);
                    
                    // Visual and audio effects
                    at.koopro.spells_n_squares.features.fx.ShaderEffectHandler.triggerTimeDistortion();
                    at.koopro.spells_n_squares.features.fx.SoundVisualSync.onArtifactActivated(level, player, "time_turner");
                    
                    net.minecraft.world.phys.Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                        pos.x, pos.y, pos.z,
                        30, 0.5, 0.5, 0.5, 0.1
                    );
                    
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.PORTAL,
                        pos.x, pos.y, pos.z,
                        20, 0.3, 0.3, 0.3, 0.05
                    );
                } else if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    // On cooldown - show message
                    long remainingTicks = COOLDOWN_TICKS - timeSinceLastUse;
                    float remainingSeconds = remainingTicks / 20.0f;
                    serverPlayer.displayClientMessage(
                        Component.literal("§cTime-Turner is on cooldown. " + 
                            String.format("%.1f", remainingSeconds) + "s remaining."), 
                        true);
                }
            }
            // If held longer, time acceleration already happened in onUseTick, so nothing to do here
        }
        return true;
    }
    
    /**
     * Gets the temporal anchor data for death prevention.
     */
    public static TimeTurnerData getTimeTurnerData(ItemStack stack) {
        return getData(stack);
    }
    
    /**
     * Checks if the Time-Turner is on cooldown for death prevention.
     */
    public static boolean isOnDeathPreventionCooldown(ItemStack stack, long currentTick) {
        TimeTurnerData data = getData(stack);
        return (currentTick - data.lastDeathPreventionTick()) < DEATH_PREVENTION_COOLDOWN;
    }
    
    /**
     * Adds tooltip information showing cooldown status and anchor information.
     */
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        TimeTurnerData data = getData(stack);
        
        // Show anchor information if set
        if (data.hasAnchor()) {
            tooltip.add(Component.literal("§6Temporal Anchor Set"));
            tooltip.add(Component.literal(String.format("§7Location: §f%.0f, %.0f, %.0f", 
                data.anchorX(), data.anchorY(), data.anchorZ())));
            if (!data.anchorDimension().isEmpty()) {
                // Extract dimension name from string (format: "minecraft:overworld" or similar)
                String dimName = data.anchorDimension();
                if (dimName.contains(":")) {
                    dimName = dimName.substring(dimName.indexOf(':') + 1);
                }
                tooltip.add(Component.literal("§7Dimension: §f" + dimName));
            }
        } else {
            tooltip.add(Component.literal("§7No anchor set. §eShift+Right-Click §7to set one."));
        }
        
        // Show cooldown information
        // Try to get current game time from tooltip context level
        long currentTick = 0;
        if (tooltipContext.level() != null) {
            currentTick = tooltipContext.level().getGameTime();
        } else {
            // Fallback: try to get from Minecraft client instance
            try {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    currentTick = mc.level.getGameTime();
                }
            } catch (Exception e) {
                // Not on client or level not available
            }
        }
        
        if (currentTick > 0) {
            // Time rewind cooldown
            long timeSinceLastUse = currentTick - data.lastUseTick();
            if (timeSinceLastUse < COOLDOWN_TICKS) {
                long remainingTicks = COOLDOWN_TICKS - timeSinceLastUse;
                float remainingSeconds = remainingTicks / 20.0f;
                tooltip.add(Component.literal(String.format("§cTime Rewind Cooldown: §f%.1fs", remainingSeconds)));
            } else {
                tooltip.add(Component.literal("§aTime Rewind: §fReady"));
            }
            
            // Death prevention cooldown
            long timeSinceDeathPrevention = currentTick - data.lastDeathPreventionTick();
            if (timeSinceDeathPrevention < DEATH_PREVENTION_COOLDOWN) {
                long remainingTicks = DEATH_PREVENTION_COOLDOWN - timeSinceDeathPrevention;
                float remainingSeconds = remainingTicks / 20.0f;
                tooltip.add(Component.literal(String.format("§cDeath Prevention Cooldown: §f%.1fs", remainingSeconds)));
            } else {
                tooltip.add(Component.literal("§aDeath Prevention: §fReady"));
            }
        }
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Right-Click: §fRewind time 12 hours"));
        tooltip.add(Component.literal("§7Hold Right-Click: §fAccelerate time"));
        tooltip.add(Component.literal("§7Shift+Right-Click: §fSet anchor"));
    }
    
    /**
     * Updates the death prevention cooldown and applies durability damage.
     */
    public static void applyDeathPrevention(ItemStack stack, Player player, long currentTick) {
        TimeTurnerData data = getData(stack);
        TimeTurnerData newData = new TimeTurnerData(
            data.lastUseTick(),
            data.anchorX(),
            data.anchorY(),
            data.anchorZ(),
            data.anchorDimension(),
            currentTick
        );
        setData(stack, newData);
        
        // Apply durability damage
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.world.entity.EquipmentSlot slot = net.minecraft.world.entity.EquipmentSlot.MAINHAND;
            if (player.getOffhandItem() == stack) {
                slot = net.minecraft.world.entity.EquipmentSlot.OFFHAND;
            }
            stack.hurtAndBreak(DEATH_PREVENTION_DURABILITY_COST, serverPlayer, slot);
        }
    }
}



















