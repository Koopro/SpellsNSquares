package at.koopro.spells_n_squares.features.economy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for currency items (Galleons, Sickles, Knuts).
 */
public class CurrencyItem extends Item {
    private final CurrencyType currencyType;
    
    public CurrencyItem(Properties properties, CurrencyType currencyType) {
        super(properties);
        this.currencyType = currencyType;
    }
    
    public CurrencyType getCurrencyType() {
        return currencyType;
    }
    
    // Simple helper for adding currency tooltip text; not overriding mapped tooltip API
    public void appendCurrencyTooltip(ItemStack stack, java.util.List<Component> tooltip) {
        tooltip.add(Component.translatable("item.spells_n_squares.currency." + currencyType.name().toLowerCase() + ".desc"));
    }
    
    public enum CurrencyType {
        GALLEON,
        SICKLE,
        KNUT
    }
}

