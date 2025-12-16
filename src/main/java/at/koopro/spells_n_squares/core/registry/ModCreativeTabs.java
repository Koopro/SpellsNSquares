package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod creative tabs.
 */
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpellsNSquares.MODID);
    
    public static final net.neoforged.neoforge.registries.DeferredHolder<CreativeModeTab, CreativeModeTab> spells_n_squares_TAB = 
            CREATIVE_TABS.register("spells_n_squares_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spells_n_squares"))
                    .icon(() -> new ItemStack(ModItems.RUBBER_DUCK.get()))
                    .displayItems((parameters, output) -> {
                        // Add all items to the creative tab
                        output.accept(ModItems.RUBBER_DUCK.get());
                        output.accept(ModItems.FLASHLIGHT.get());
                        output.accept(ModItems.DEMO_WAND.get());
                        output.accept(ModItems.DEMIGUISE_CLOAK.get());
                        output.accept(ModItems.DEATHLY_HALLOW_CLOAK.get());
                    })
                    .build());
}
