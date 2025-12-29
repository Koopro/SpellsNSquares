package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

public class DemoBroomEntityRenderer extends GeoEntityRenderer<BroomEntity, DemoBroomEntityRenderer.BroomRenderState> {
    public DemoBroomEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DemoBroomEntityModel());
    }
    
    public static final class BroomRenderState extends EntityRenderState implements GeoRenderState {
        private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();
        
        public BroomRenderState() {
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

