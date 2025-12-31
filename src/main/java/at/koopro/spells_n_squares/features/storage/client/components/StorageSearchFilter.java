package at.koopro.spells_n_squares.features.storage.client.components;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * Component for adding search and filtering functionality to storage GUIs.
 * Provides search box and filter buttons for common item categories.
 */
public final class StorageSearchFilter {
    private StorageSearchFilter() {
    }
    
    /**
     * Filter types for item categorization.
     */
    public enum FilterType {
        ALL("All"),
        TOOLS("Tools"),
        WEAPONS("Weapons"),
        ARMOR("Armor"),
        FOOD("Food"),
        POTIONS("Potions"),
        BLOCKS("Blocks"),
        INGREDIENTS("Ingredients"),
        OTHER("Other");
        
        private final String displayName;
        
        FilterType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Filter state for storage search.
     */
    public static class FilterState {
        private String searchText = "";
        private FilterType filterType = FilterType.ALL;
        
        public String getSearchText() {
            return searchText;
        }
        
        public void setSearchText(String searchText) {
            this.searchText = searchText != null ? searchText : "";
        }
        
        public FilterType getFilterType() {
            return filterType;
        }
        
        public void setFilterType(FilterType filterType) {
            this.filterType = filterType != null ? filterType : FilterType.ALL;
        }
        
        /**
         * Checks if an item stack matches the current filter criteria.
         */
        public boolean matches(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            
            // Category filter
            if (filterType != FilterType.ALL && !matchesCategory(stack, filterType)) {
                return false;
            }
            
            // Search text filter
            if (!searchText.isEmpty()) {
                String itemName = stack.getDisplayName().getString().toLowerCase();
                String searchLower = searchText.toLowerCase();
                if (!itemName.contains(searchLower)) {
                    // Also check item ID
                    String itemId = stack.getItem().toString().toLowerCase();
                    if (!itemId.contains(searchLower)) {
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        /**
         * Checks if an item matches a category.
         * Uses a simplified approach that checks item properties rather than specific class types.
         */
        private boolean matchesCategory(ItemStack stack, FilterType category) {
            net.minecraft.world.item.Item item = stack.getItem();
            
            // Helper to check if item is food
            // Use a simplified check based on common food items and item name patterns
            String itemId = item.toString().toLowerCase();
            boolean isFood = item == Items.CAKE || item == Items.BREAD || item == Items.APPLE ||
                            item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP ||
                            item == Items.COOKED_CHICKEN || item == Items.CARROT ||
                            item == Items.POTATO || item == Items.BAKED_POTATO ||
                            itemId.contains("food") || itemId.contains("potion") ||
                            itemId.contains("beer") || itemId.contains("juice") ||
                            itemId.contains("chocolate") || itemId.contains("candy");
            
            // Helper to check if item is a block
            boolean isBlock = item instanceof net.minecraft.world.item.BlockItem;
            
            // Helper to check if item is a potion
            boolean isPotion = item instanceof net.minecraft.world.item.PotionItem ||
                               item instanceof net.minecraft.world.item.SplashPotionItem ||
                               item instanceof net.minecraft.world.item.LingeringPotionItem ||
                               item instanceof net.minecraft.world.item.TippedArrowItem;
            
            return switch (category) {
                case ALL -> true;
                case TOOLS -> {
                    // Check for common tool items by name/ID pattern or use tags if available
                    yield itemId.contains("pickaxe") || itemId.contains("shovel") || 
                           itemId.contains("hoe") || itemId.contains("axe") ||
                           item instanceof net.minecraft.world.item.FishingRodItem ||
                           item instanceof net.minecraft.world.item.ShearsItem ||
                           item instanceof net.minecraft.world.item.FlintAndSteelItem;
                }
                case WEAPONS -> {
                    yield itemId.contains("sword") || itemId.contains("bow") ||
                           itemId.contains("crossbow") || itemId.contains("trident") ||
                           item instanceof net.minecraft.world.item.BowItem ||
                           item instanceof net.minecraft.world.item.CrossbowItem ||
                           item instanceof net.minecraft.world.item.TridentItem;
                }
                case ARMOR -> {
                    yield itemId.contains("helmet") || itemId.contains("chestplate") ||
                           itemId.contains("leggings") || itemId.contains("boots") ||
                           itemId.contains("elytra");
                }
                case FOOD -> isFood;
                case POTIONS -> isPotion;
                case BLOCKS -> isBlock;
                case INGREDIENTS -> !isFood && !isPotion && !isBlock && 
                                   !matchesCategory(stack, FilterType.TOOLS) &&
                                   !matchesCategory(stack, FilterType.WEAPONS) &&
                                   !matchesCategory(stack, FilterType.ARMOR);
                case OTHER -> !isFood && !isPotion && !isBlock &&
                             !matchesCategory(stack, FilterType.TOOLS) &&
                             !matchesCategory(stack, FilterType.WEAPONS) &&
                             !matchesCategory(stack, FilterType.ARMOR) &&
                             !matchesCategory(stack, FilterType.INGREDIENTS);
            };
        }
    }
    
    /**
     * Creates a search box for storage filtering.
     * 
     * @param font The font to use for the search box
     * @param x X position
     * @param y Y position
     * @param width Width of the search box
     * @param height Height of the search box
     * @param onSearchChanged Callback when search text changes
     * @return The search box widget
     */
    public static EditBox createSearchBox(net.minecraft.client.gui.Font font, int x, int y, int width, int height, Consumer<String> onSearchChanged) {
        EditBox searchBox = new EditBox(
            font,
            x, y, width, height,
            Component.translatable("gui.spells_n_squares.storage.search")
        );
        searchBox.setMaxLength(50);
        searchBox.setResponder(onSearchChanged);
        return searchBox;
    }
    
    /**
     * Creates filter buttons for storage filtering.
     * 
     * @param startX Starting X position
     * @param y Y position
     * @param buttonWidth Width of each button
     * @param buttonHeight Height of each button
     * @param spacing Spacing between buttons
     * @param selectedFilter Currently selected filter
     * @param onFilterChanged Callback when filter changes
     * @return Array of filter buttons
     */
    public static Button[] createFilterButtons(
            int startX, int y, int buttonWidth, int buttonHeight, int spacing,
            FilterType selectedFilter, Consumer<FilterType> onFilterChanged) {
        
        FilterType[] filters = FilterType.values();
        Button[] buttons = new Button[filters.length];
        
        for (int i = 0; i < filters.length; i++) {
            FilterType filter = filters[i];
            int x = startX + i * (buttonWidth + spacing);
            
            Component buttonText = Component.literal(filter.getDisplayName());
            if (filter == selectedFilter) {
                buttonText = buttonText.copy().withStyle(
                    style -> style.withColor(0x00FF00) // Green for selected
                );
            }
            
            buttons[i] = Button.builder(buttonText, button -> onFilterChanged.accept(filter))
                .bounds(x, y, buttonWidth, buttonHeight)
                .build();
        }
        
        return buttons;
    }
}

