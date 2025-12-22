package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.wand.client.WandItemRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Wand item with Geckolib 3D model rendering.
 */
public class WandItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WandItem(Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Server-side spell casting is handled via network packet from client
        // The client sends SpellCastPayload with the selected slot
        // This method only handles client-side packet sending
        
        // On client side, send packet to server
        if (level.isClientSide()) {
            // Get the currently selected slot from client-side data
            int selectedSlot = at.koopro.spells_n_squares.features.spell.client.ClientSpellData.getSelectedSlot();
            
            // Send spell cast request to server via network packet
            at.koopro.spells_n_squares.features.spell.network.SpellCastPayload payload = 
                new at.koopro.spells_n_squares.features.spell.network.SpellCastPayload(selectedSlot);
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS; // Let other handlers process if spell casting didn't work
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private WandItemRenderer renderer;

            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new WandItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // No animations needed for wand, but method must be implemented
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}








