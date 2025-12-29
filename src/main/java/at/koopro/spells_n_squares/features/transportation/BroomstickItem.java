package at.koopro.spells_n_squares.features.transportation;

import at.koopro.spells_n_squares.features.transportation.client.DemoBroomItemRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Broomstick item for flying travel with GeckoLib rendering.
 */
public class BroomstickItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public enum BroomstickTier {
        // Demo broom
        DEMO("demo", 100.0f, 0.5f, "Demo Broomstick"),
        // Basic brooms
        BLUEBOTTLE("bluebottle", 80.0f, 0.4f, "Bluebottle"),
        SHOOTING_STAR("shooting_star", 90.0f, 0.45f, "Shooting Star"),
        BASIC("basic", 100.0f, 0.5f, "Basic Broomstick"),
        
        // Cleansweep series
        CLEANSWEEP_5("cleansweep_5", 110.0f, 0.5f, "Cleansweep Five"),
        CLEANSWEEP_7("cleansweep_7", 130.0f, 0.6f, "Cleansweep Seven"),
        
        // Comet series
        COMET_140("comet_140", 120.0f, 0.55f, "Comet 140"),
        COMET_260("comet_260", 140.0f, 0.65f, "Comet 260"),
        
        // Nimbus series
        NIMBUS_2000("nimbus_2000", 180.0f, 0.8f, "Nimbus 2000"),
        NIMBUS_2001("nimbus_2001", 190.0f, 0.85f, "Nimbus 2001"),
        
        // Premium brooms
        SILVER_ARROW("silver_arrow", 200.0f, 0.9f, "Silver Arrow"),
        RACING("racing", 150.0f, 0.7f, "Racing Broomstick"),
        FIREBOLT("firebolt", 220.0f, 1.2f, "Firebolt"),
        FIREBOLT_SUPREME("firebolt_supreme", 250.0f, 1.3f, "Firebolt Supreme");
        
        private final String id;
        private final float maxStamina;
        private final float speed;
        private final String name;
        
        BroomstickTier(String id, float maxStamina, float speed, String name) {
            this.id = id;
            this.maxStamina = maxStamina;
            this.speed = speed;
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public float getMaxStamina() {
            return maxStamina;
        }
        
        public float getSpeed() {
            return speed;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final BroomstickTier tier;
    
    public BroomstickItem(Properties properties, BroomstickTier tier) {
        super(properties.stacksTo(1));
        this.tier = tier;
        GeoItem.registerSyncedAnimatable(this);
    }
    
    public BroomstickTier getTier() {
        return tier;
    }
    
    public static BroomstickData.BroomstickDataComponent getBroomstickData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BroomstickItem broomstick)) {
            return null;
        }
        
        BroomstickData.BroomstickDataComponent data = stack.get(BroomstickData.BROOMSTICK_DATA.get());
        if (data == null) {
            data = BroomstickData.BroomstickDataComponent.createDefault(
                broomstick.tier.getId(),
                broomstick.tier.getMaxStamina(),
                broomstick.tier.getSpeed()
            );
            stack.set(BroomstickData.BROOMSTICK_DATA.get(), data);
        }
        return data;
    }
    
    public static void handleFlight(Player player, ItemStack broomstick) {
        if (player.level().isClientSide()) {
            return;
        }
        
        BroomstickData.BroomstickDataComponent data = getBroomstickData(broomstick);
        if (data == null || data.currentStamina() <= 0) {
            return;
        }
        
        // Flight logic would be handled by a tick handler
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        // Check if player is already riding a broom
        if (player.getVehicle() instanceof BroomEntity) {
            // Already on a broom - dismount
            player.stopRiding();
            return InteractionResult.SUCCESS;
        }
        
        // Check if player is already riding something else
        if (player.isPassenger()) {
            return InteractionResult.FAIL;
        }
        
        // Get broom data
        BroomstickData.BroomstickDataComponent data = getBroomstickData(stack);
        if (data == null) {
            return InteractionResult.FAIL;
        }
        
        // Check stamina
        if (data.currentStamina() <= 0) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cBroomstick has no stamina!"), 
                true);
            return InteractionResult.FAIL;
        }
        
        // Spawn broom entity (don't auto-mount, player will right-click to mount)
        // Position it 1 block above the ground
        Vec3 playerPos = player.position();
        double groundY = serverLevel.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, 
            (int) playerPos.x, (int) playerPos.z);
        double hoverY = groundY + 1.0; // 1 block above ground
        
        BroomEntity broom = new BroomEntity(
            TransportationRegistry.BROOM_ENTITY.get(),
            serverLevel,
            stack
        );
        broom.setPos(playerPos.x, hoverY, playerPos.z);
        broom.setBroomItem(stack);
        
        if (serverLevel.addFreshEntity(broom)) {
            // Play sound
            serverLevel.playSound(null, playerPos.x, hoverY, playerPos.z,
                SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5f, 1.5f);
            
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§aBroom summoned! Right-click to ride."), 
                true);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.FAIL;
    }
    
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private DemoBroomItemRenderer renderer;

            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DemoBroomItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add idle animation when held in hand
        // Animation controllers can be added here if needed
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}


















