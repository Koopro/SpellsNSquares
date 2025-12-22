package at.koopro.spells_n_squares.features.transportation;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Broomstick item for flying travel.
 */
public class BroomstickItem extends Item {
    
    public enum BroomstickTier {
        BASIC("basic", 100.0f, 0.5f, "Basic Broomstick"),
        RACING("racing", 150.0f, 0.8f, "Racing Broomstick"),
        FIREBOLT("firebolt", 200.0f, 1.2f, "Firebolt");
        
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
}











