package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    
    public record TimeTurnerData(
        int lastUseTick
    ) {
        public static final Codec<TimeTurnerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("lastUse").forGetter(TimeTurnerData::lastUseTick)
            ).apply(instance, TimeTurnerData::new)
        );
    }
    
    public TimeTurnerItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        ItemStack stack = player.getItemInHand(hand);
        int currentTick = (int) level.getGameTime();
        
        TimeTurnerData data = stack.get(TIME_TURNER_DATA.get());
        if (data == null) {
            data = new TimeTurnerData(0);
        }
        
        // Check cooldown
        if (currentTick - data.lastUseTick() < COOLDOWN_TICKS) {
            return InteractionResult.FAIL;
        }
        
        // Calculate new world time (rewind by 12 hours)
        long currentDayTime = serverLevel.getDayTime();
        long newDayTime = Math.max(0, currentDayTime - TIME_REWIND_TICKS);
        
        // Set the new world time
        serverLevel.setDayTime(newDayTime);
        
        // Update last use time
        stack.set(TIME_TURNER_DATA.get(), new TimeTurnerData(currentTick));
        
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
        
        return InteractionResult.SUCCESS;
    }
}
















