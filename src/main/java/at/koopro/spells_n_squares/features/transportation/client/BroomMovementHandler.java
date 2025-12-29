package at.koopro.spells_n_squares.features.transportation.client;

import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import at.koopro.spells_n_squares.features.transportation.network.BroomMovementInputPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Client-side handler for sending broom movement input to the server.
 */
@EventBusSubscriber(modid = "spells_n_squares", value = Dist.CLIENT)
public class BroomMovementHandler {
    private static KeyMapping forwardKey = null;
    private static KeyMapping backKey = null;
    private static boolean keysInitialized = false;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options == null) {
            return;
        }
        
        // Check if player is riding a BroomEntity
        if (mc.player.getVehicle() instanceof BroomEntity broom) {
            Options options = mc.options;
            
            // Initialize key mappings on first use
            if (!keysInitialized) {
                forwardKey = findKeyMapping(options, "forward");
                backKey = findKeyMapping(options, "back");
                // If not found, try alternative names
                if (forwardKey == null) {
                    forwardKey = findKeyMapping(options, "up");
                }
                if (backKey == null) {
                    backKey = findKeyMapping(options, "down");
                }
                keysInitialized = true;
            }
            
            // Get key states directly (works even when riding)
            // Calculate forward/backward: W pressed = 1, S pressed = -1, neither = 0
            float forward = 0.0f;
            if (forwardKey != null && forwardKey.isDown()) {
                forward += 1.0f;
            }
            if (backKey != null && backKey.isDown()) {
                forward -= 1.0f;
            }
            
            // Calculate left/right: A pressed = -1, D pressed = 1, neither = 0
            float strafe = 0.0f;
            if (options.keyLeft.isDown()) strafe -= 1.0f;
            if (options.keyRight.isDown()) strafe += 1.0f;
            
            // Get jump input
            boolean jump = options.keyJump.isDown();
            
            // Send movement input to server every tick
            ClientPacketDistributor.sendToServer(new BroomMovementInputPayload(
                broom.getId(),
                forward,
                strafe,
                jump
            ));
        }
    }
    
    /**
     * Helper method to find a KeyMapping from Options by searching KeyMapping names.
     */
    private static KeyMapping findKeyMapping(Options options, String keyName) {
        try {
            // First try direct field access with common names
            String[] possibleFieldNames = {
                "key" + keyName.substring(0, 1).toUpperCase() + keyName.substring(1), // keyForward, keyBack
                "key" + keyName.toUpperCase().charAt(0) + keyName.substring(1), // keyForward, keyBack
                "key" + keyName, // keyforward, keyback
            };
            
            for (String fieldName : possibleFieldNames) {
                try {
                    java.lang.reflect.Field field = Options.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    KeyMapping keyMapping = (KeyMapping) field.get(options);
                    if (keyMapping != null) {
                        return keyMapping;
                    }
                } catch (NoSuchFieldException e) {
                    // Try next name
                }
            }
            
            // If direct access failed, search all fields
            java.lang.reflect.Field[] fields = Options.class.getDeclaredFields();
            String searchName = keyName.toLowerCase();
            String searchKey = "key." + searchName;
            
            for (java.lang.reflect.Field field : fields) {
                if (field.getType() == KeyMapping.class) {
                    field.setAccessible(true);
                    KeyMapping keyMapping = (KeyMapping) field.get(options);
                    if (keyMapping != null) {
                        // Check field name
                        String fieldName = field.getName().toLowerCase();
                        // Check key mapping name
                        String mappingName = keyMapping.getName().toLowerCase();
                        
                        // Match by field name (e.g., "keyForward", "keyBack")
                        if (fieldName.contains(searchName)) {
                            return keyMapping;
                        }
                        // Match by key mapping name (e.g., "key.forward", "key.back")
                        if (mappingName.equals(searchKey) || mappingName.contains(searchName)) {
                            return keyMapping;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Reflection failed, return null
        }
        return null;
    }
}

