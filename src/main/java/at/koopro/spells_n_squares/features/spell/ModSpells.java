package at.koopro.spells_n_squares.features.spell;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Registry and initialization of all mod spells.
 * 
 * To create a new spell:
 * 1. Create a class that implements the Spell interface
 * 2. Register it in the register() method below
 * 3. Create an icon texture at: assets/{modid}/textures/spell/{spell_name}.png (16x16 pixels recommended)
 * 
 * For addons: Simply register spells with your mod's namespace, and place icons in:
 * assets/{your_modid}/textures/spell/{spell_id_path}.png
 */
public class ModSpells {
    
    /**
     * Registers all spells in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Core spells
        SpellRegistry.register(new HealSpell());
        SpellRegistry.register(new TeleportSpell());
        SpellRegistry.register(new FireballSpell());
        SpellRegistry.register(new LightningSpell());
        SpellRegistry.register(new ProtegoSpell());
        SpellRegistry.register(new ApparitionSpell());
        SpellRegistry.register(new LumosSpell());
        
        // Utility spells
        SpellRegistry.register(new EntityDetectionSpell());
        SpellRegistry.register(new ExtensionCharmSpell());
        SpellRegistry.register(new PortableCraftingSpell());
        SpellRegistry.register(new ItemRecallSpell());
    }
    
    /**
     * Helper class for creating simple spells without implementing all methods.
     * Provides sensible defaults for optional Spell interface methods.
     */
    public static abstract class SimpleSpell implements Spell {
        private final Identifier id;
        private final String name;
        private final String description;
        private final int cooldown;
        private final Identifier icon;
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown) {
            this(id, name, description, cooldown, null);
        }
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown, Identifier icon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cooldown = cooldown;
            this.icon = icon;
        }
        
        @Override
        public Identifier getId() {
            return id;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public Component getTranslatableName() {
            String translationKey = "spell." + id.getNamespace() + "." + id.getPath() + ".name";
            return Component.translatable(translationKey);
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public int getCooldown() {
            return cooldown;
        }
        
        @Override
        public Identifier getIcon() {
            return icon != null ? icon : Spell.super.getIcon();
        }
        
        @Override
        public void spawnCastEffects(Player player, Level level, boolean success) {
            if (success) {
                // Default: screen flash
                if (level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
                }
                
                // Screen shake for powerful spells
                if (getVisualEffectIntensity() > 0.7f && level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerShake(
                        0.1f * getVisualEffectIntensity(), 10);
                }
            }
        }
    }
}
