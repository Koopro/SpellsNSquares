package at.koopro.spells_n_squares.features.flashlight;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.item.client.FlashlightItemRenderer;

/**
 * Flashlight item that can be toggled on/off and emits light when held.
 */
public class FlashlightItem extends Item implements GeoItem {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = 
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_ON = 
            DATA_COMPONENTS.register(
                    "flashlight_on",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .build()
            );
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public FlashlightItem(Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }
    
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private FlashlightItemRenderer renderer;

            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new FlashlightItemRenderer();

                return this.renderer;
            }
        });
    }
    
    /**
     * Checks if the flashlight is currently turned on.
     */
    public static boolean isOn(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof FlashlightItem)) {
            return false;
        }
        return stack.getOrDefault(IS_ON.get(), false);
    }
    
    /**
     * Sets the flashlight on/off state.
     */
    public static void setOn(ItemStack stack, boolean on) {
        if (stack.isEmpty() || !(stack.getItem() instanceof FlashlightItem)) {
            return;
        }
        if (on) {
            stack.set(IS_ON.get(), true);
        } else {
            stack.remove(IS_ON.get());
        }
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            boolean currentlyOn = isOn(stack);
            boolean newState = !currentlyOn;
            setOn(stack, newState);
            
            // Play toggle sound
            var soundEvent = newState ? ModSounds.FLASHLIGHT_ON.value() : ModSounds.FLASHLIGHT_OFF.value();
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    soundEvent, SoundSource.PLAYERS, 0.5f, 1.0f);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Make the flashlight glow when it's on
        return isOn(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // No animations needed for flashlight, but method must be implemented
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
