package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.core.base.item.BaseGeoItem;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.client.WandItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Wand item with Geckolib 3D model rendering.
 */
public class WandItem extends BaseGeoItem {

    public WandItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        DevLogger.logItemInteraction(this, "use", player, player.getItemInHand(hand));
        DevLogger.logMethodEntry(this, "use", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", hand=" + hand +
            ", clientSide=" + level.isClientSide());
        
        // Server-side spell casting is handled via network packet from client
        // The client sends SpellCastPayload with the selected slot
        // This method only handles client-side packet sending
        
        // On client side, send packet to server
        if (level.isClientSide()) {
            // Get the currently selected slot from client-side data
            int selectedSlot = at.koopro.spells_n_squares.features.spell.client.ClientSpellData.getSelectedSlot();
            DevLogger.logParameter(this, "use", "selectedSlot", selectedSlot);
            
            // Send spell cast request to server via network packet
            at.koopro.spells_n_squares.features.spell.network.SpellCastPayload payload = 
                new at.koopro.spells_n_squares.features.spell.network.SpellCastPayload(selectedSlot);
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
            
            DevLogger.logNetworkPacket(this, "use", "SpellCastPayload", "SEND", 
                "slot=" + selectedSlot);
            DevLogger.logMethodExit(this, "use", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }
        
        DevLogger.logMethodExit(this, "use", InteractionResult.PASS);
        return InteractionResult.PASS; // Let other handlers process if spell casting didn't work
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new WandItemRenderer();
    }
}








