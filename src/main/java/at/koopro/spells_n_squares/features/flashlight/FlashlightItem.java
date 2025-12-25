package at.koopro.spells_n_squares.features.flashlight;

import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.features.flashlight.client.FlashlightItemRenderer;
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

import java.util.function.Consumer;

/**
 * Flashlight item with Geckolib 3D model rendering.
 * Can be toggled on/off by right-clicking.
 */
public class FlashlightItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = 
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_ON = 
        DATA_COMPONENTS.register("flashlight_on", () -> 
            DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .build());

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // No animations needed for flashlight, but method must be implemented
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    /**
     * Checks if the flashlight is on.
     * @param stack The item stack
     * @return true if the flashlight is on
     */
    public static boolean isOn(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getOrDefault(IS_ON.get(), false);
    }
    
    /**
     * Sets the flashlight on/off state.
     * @param stack The item stack
     * @param on Whether the flashlight should be on
     */
    public static void setOn(ItemStack stack, boolean on) {
        if (stack.isEmpty()) {
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
        boolean currentlyOn = isOn(stack);
        boolean newState = !currentlyOn;
        
        setOn(stack, newState);
        
        // Play sound
        var soundEvent = newState ? ModSounds.FLASHLIGHT_ON.value() : ModSounds.FLASHLIGHT_OFF.value();
        level.playSound(player, player.getX(), player.getY(), player.getZ(), 
                       soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        return InteractionResult.SUCCESS;
    }
}









