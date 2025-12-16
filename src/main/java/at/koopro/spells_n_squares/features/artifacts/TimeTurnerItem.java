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

import java.util.ArrayList;
import java.util.List;

/**
 * Time-Turner artifact that allows players to rewind time.
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
    
    private static final int COOLDOWN_TICKS = 6000;
    private static final int SNAPSHOT_DURATION = 600;
    private static final int REWIND_MIN = 100;
    private static final int REWIND_MAX = 200;
    
    public record TimeTurnerData(
        int lastUseTick,
        List<PlayerSnapshot> snapshots
    ) {
        public static final Codec<TimeTurnerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("lastUse").forGetter(TimeTurnerData::lastUseTick),
                Codec.list(PlayerSnapshot.CODEC).fieldOf("snapshots").forGetter(TimeTurnerData::snapshots)
            ).apply(instance, TimeTurnerData::new)
        );
    }
    
    public record PlayerSnapshot(
        int tick,
        double x, double y, double z,
        float health,
        float foodLevel,
        float saturation
    ) {
        public static final Codec<PlayerSnapshot> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("tick").forGetter(PlayerSnapshot::tick),
                Codec.DOUBLE.fieldOf("x").forGetter(PlayerSnapshot::x),
                Codec.DOUBLE.fieldOf("y").forGetter(PlayerSnapshot::y),
                Codec.DOUBLE.fieldOf("z").forGetter(PlayerSnapshot::z),
                Codec.FLOAT.fieldOf("health").forGetter(PlayerSnapshot::health),
                Codec.FLOAT.fieldOf("food").forGetter(PlayerSnapshot::foodLevel),
                Codec.FLOAT.fieldOf("saturation").forGetter(PlayerSnapshot::saturation)
            ).apply(instance, PlayerSnapshot::new)
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
        
        ItemStack stack = player.getItemInHand(hand);
        int currentTick = (int) level.getGameTime();
        
        TimeTurnerData data = stack.get(TIME_TURNER_DATA.get());
        if (data == null) {
            data = new TimeTurnerData(0, new ArrayList<>());
        }
        
        if (currentTick - data.lastUseTick() < COOLDOWN_TICKS) {
            return InteractionResult.FAIL;
        }
        
        PlayerSnapshot snapshot = findSnapshot(data.snapshots(), currentTick);
        if (snapshot == null) {
            return InteractionResult.FAIL;
        }
        
        player.setPos(snapshot.x(), snapshot.y(), snapshot.z());
        player.setHealth(snapshot.health());
        player.getFoodData().setFoodLevel((int) snapshot.foodLevel());
        player.getFoodData().setSaturation(snapshot.saturation());
        
        stack.set(TIME_TURNER_DATA.get(), new TimeTurnerData(currentTick, data.snapshots()));
        
        if (level instanceof ServerLevel serverLevel) {
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
        }
        
        return InteractionResult.SUCCESS;
    }
    
    private PlayerSnapshot findSnapshot(List<PlayerSnapshot> snapshots, int currentTick) {
        int rewindTicks = REWIND_MIN + (int) (Math.random() * (REWIND_MAX - REWIND_MIN));
        int targetTick = currentTick - rewindTicks;
        
        PlayerSnapshot best = null;
        int bestDiff = Integer.MAX_VALUE;
        
        for (PlayerSnapshot snapshot : snapshots) {
            int diff = Math.abs(snapshot.tick() - targetTick);
            if (diff < bestDiff && snapshot.tick() <= currentTick - REWIND_MIN) {
                best = snapshot;
                bestDiff = diff;
            }
        }
        
        return best;
    }
    
    public static void storeSnapshot(ItemStack stack, Player player, int currentTick) {
        TimeTurnerData data = stack.get(TIME_TURNER_DATA.get());
        if (data == null) {
            data = new TimeTurnerData(0, new ArrayList<>());
        }
        
        List<PlayerSnapshot> snapshots = new ArrayList<>(data.snapshots());
        
        snapshots.add(new PlayerSnapshot(
            currentTick,
            player.getX(), player.getY(), player.getZ(),
            player.getHealth(),
            player.getFoodData().getFoodLevel(),
            player.getFoodData().getSaturationLevel()
        ));
        
        snapshots.removeIf(s -> currentTick - s.tick() > SNAPSHOT_DURATION);
        
        stack.set(TIME_TURNER_DATA.get(), new TimeTurnerData(data.lastUseTick(), snapshots));
    }
}
