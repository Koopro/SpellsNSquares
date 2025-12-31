package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.economy.VaultMenu;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentMenu;
import at.koopro.spells_n_squares.features.storage.BagMenu;
import at.koopro.spells_n_squares.features.wand.WandLatheMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod menu types.
 */
public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SpellsNSquares.MODID);
    
    public static final DeferredHolder<MenuType<?>, MenuType<BagMenu>> BAG_MENU = MENUS.register(
        "bag_menu",
        () -> IMenuTypeExtension.create(BagMenu::new)
    );
    
    public static final DeferredHolder<MenuType<?>, MenuType<VaultMenu>> VAULT_MENU = MENUS.register(
        "vault_menu",
        () -> IMenuTypeExtension.create(VaultMenu::new)
    );
    
    public static final DeferredHolder<MenuType<?>, MenuType<EnchantmentMenu>> ENCHANTMENT_MENU = MENUS.register(
        "enchantment_menu",
        () -> IMenuTypeExtension.create(EnchantmentMenu::new)
    );
    
    public static final DeferredHolder<MenuType<?>, MenuType<WandLatheMenu>> WAND_LATHE_MENU = MENUS.register(
        "wand_lathe_menu",
        () -> IMenuTypeExtension.create(WandLatheMenu::new)
    );
}











