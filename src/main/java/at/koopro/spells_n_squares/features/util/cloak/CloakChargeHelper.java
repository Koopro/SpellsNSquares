package at.koopro.spells_n_squares.features.util.cloak;

import at.koopro.spells_n_squares.features.cloak.CloakChargeData;
import net.minecraft.world.item.ItemStack;

/**
 * Helper class for managing cloak charges.
 */
public final class CloakChargeHelper {
    private CloakChargeHelper() {
    }
    
    // Default max charges
    public static final int DEFAULT_MAX_CHARGES = 100;
    
    // Charge drain rate (1 charge per 20 ticks = 1 per second)
    public static final int CHARGE_DRAIN_INTERVAL = 20;
    
    // Charge recharge rate (1 charge per 60 ticks = 3 seconds per charge)
    public static final int CHARGE_RECHARGE_INTERVAL = 60;
    
    /**
     * Gets the cloak charge component from an item stack, creating default if missing.
     */
    public static CloakChargeData.CloakChargeComponent getChargeData(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        
        CloakChargeData.CloakChargeComponent data = stack.get(CloakChargeData.CLOAK_CHARGES.get());
        if (data == null) {
            // Create default charge data
            data = CloakChargeData.CloakChargeComponent.createDefault(DEFAULT_MAX_CHARGES);
            setChargeData(stack, data);
        }
        return data;
    }
    
    /**
     * Sets the cloak charge data on an item stack.
     */
    public static void setChargeData(ItemStack stack, CloakChargeData.CloakChargeComponent data) {
        if (stack.isEmpty()) {
            return;
        }
        stack.set(CloakChargeData.CLOAK_CHARGES.get(), data);
    }
    
    /**
     * Gets the current charges.
     */
    public static int getCurrentCharges(ItemStack stack) {
        CloakChargeData.CloakChargeComponent data = getChargeData(stack);
        return data != null ? data.currentCharges() : 0;
    }
    
    /**
     * Gets the max charges.
     */
    public static int getMaxCharges(ItemStack stack) {
        CloakChargeData.CloakChargeComponent data = getChargeData(stack);
        return data != null ? data.maxCharges() : DEFAULT_MAX_CHARGES;
    }
    
    /**
     * Sets the current charges.
     */
    public static void setCurrentCharges(ItemStack stack, int charges) {
        CloakChargeData.CloakChargeComponent data = getChargeData(stack);
        if (data != null) {
            int clamped = Math.max(0, Math.min(charges, data.maxCharges()));
            setChargeData(stack, new CloakChargeData.CloakChargeComponent(
                clamped,
                data.maxCharges(),
                data.lastRechargeTick()
            ));
        }
    }
    
    /**
     * Drains one charge from the cloak.
     */
    public static boolean drainCharge(ItemStack stack) {
        int current = getCurrentCharges(stack);
        if (current <= 0) {
            return false;
        }
        setCurrentCharges(stack, current - 1);
        return true;
    }
    
    /**
     * Checks if the cloak has any charges.
     */
    public static boolean hasCharges(ItemStack stack) {
        return getCurrentCharges(stack) > 0;
    }
    
    /**
     * Recharges one charge if enough time has passed.
     */
    public static void tryRecharge(ItemStack stack, int currentTick) {
        CloakChargeData.CloakChargeComponent data = getChargeData(stack);
        if (data == null) {
            return;
        }
        
        int current = data.currentCharges();
        int max = data.maxCharges();
        
        if (current >= max) {
            // Already at max, just update last recharge tick
            setChargeData(stack, new CloakChargeData.CloakChargeComponent(
                current, max, currentTick
            ));
            return;
        }
        
        int ticksSinceRecharge = currentTick - data.lastRechargeTick();
        if (ticksSinceRecharge >= CHARGE_RECHARGE_INTERVAL) {
            // Recharge one charge
            setChargeData(stack, new CloakChargeData.CloakChargeComponent(
                current + 1, max, currentTick
            ));
        }
    }
}

