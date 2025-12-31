package at.koopro.spells_n_squares.features.storage.block.client;

import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

/**
 * Renderer for Newt's Case block entity with GeckoLib animations.
 */
public class NewtsCaseBlockRenderer extends GeoBlockRenderer<NewtsCaseBlockEntity, NewtsCaseBlockRenderer.NewtsCaseRenderState> {
    public NewtsCaseBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new NewtsCaseBlockModel());
    }

    @Override
    public NewtsCaseRenderState createRenderState() {
        return new NewtsCaseRenderState();
    }

    public static final class NewtsCaseRenderState extends BlockEntityRenderState implements GeoRenderState {
        private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();

        public NewtsCaseRenderState() {
            // Provide defaults for the values GeckoLib expects during render state extraction.
            addGeckolibData(DataTickets.TICK, 0d);
            addGeckolibData(DataTickets.PARTIAL_TICK, 0f);
            addGeckolibData(DataTickets.PACKED_LIGHT, 0x00F000F0); // full-bright fallback
            addGeckolibData(DataTickets.PACKED_OVERLAY, 0);
        }

        @Override
        public double getAnimatableAge() {
            Double age = getGeckolibData(DataTickets.TICK);
            return age != null ? age : 0d;
        }

        @Override
        public float getPartialTick() {
            Float partialTick = getGeckolibData(DataTickets.PARTIAL_TICK);
            if (partialTick != null) {
                return partialTick;
            }
            // Fallback to the live delta tracker if GeckoLib hasn't provided a partial tick yet.
            return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        }

        @Override
        public int getPackedLight() {
            Integer packedLight = getGeckolibData(DataTickets.PACKED_LIGHT);
            return packedLight != null ? packedLight : 0x00F000F0;
        }

        @Override
        public <D> D getOrDefaultGeckolibData(DataTicket<D> dataTicket, D defaultValue) {
            D value = getGeckolibData(dataTicket);
            return value != null ? value : defaultValue;
        }

        @Override
        public <D> void addGeckolibData(DataTicket<D> dataTicket, D data) {
            if (data == null) {
                dataMap.remove(dataTicket);
                return;
            }
            dataMap.put(dataTicket, data);
        }

        @Override
        public boolean hasGeckolibData(DataTicket<?> dataTicket) {
            Object value = dataMap.get(dataTicket);
            return value != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D getGeckolibData(DataTicket<D> dataTicket) {
            return (D) dataMap.get(dataTicket);
        }

        @Override
        public Map<DataTicket<?>, Object> getDataMap() {
            return dataMap;
        }
    }
}







