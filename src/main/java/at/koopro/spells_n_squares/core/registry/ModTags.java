package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Central tag definitions for the mod.
 */
public final class ModTags {
    private ModTags() {
    }

    public static final TagKey<Item> WANDS = TagKey.create(
        Registries.ITEM,
        ModIdentifierHelper.modId("wands")
    );
}

