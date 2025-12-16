# Refactoring Opportunities

This document identifies potential refactoring opportunities for large or complex files in the codebase.

## SpellSelectionScreen.java (940 lines)

**Location**: `features/spell/client/SpellSelectionScreen.java`

**Current Status**: Functional and well-organized, but could benefit from extraction of some components.

### Potential Improvements

1. **Extract UI Constants Class**
   - Move all UI layout constants to a separate `SpellSelectionScreenConstants` class
   - Constants like `SLOT_SIZE`, `SLOT_SPACING`, `AVAILABLE_SPELLS_START_Y`, etc.
   - **Benefit**: Reduces file size and makes constants reusable

2. **Extract Animation Handler**
   - Create `SpellSelectionAnimationHandler` class for animation state management
   - Handle `slotHoverTimes`, `spellHoverTimes`, `selectionAnimationTime`
   - **Benefit**: Separates animation logic from UI logic

3. **Extract Rendering Logic**
   - Create `SpellSelectionRenderer` helper class for rendering methods
   - Methods like `renderSlot()`, `renderAvailableSpells()`, `renderTooltip()`
   - **Benefit**: Separates rendering concerns from screen management

4. **Extract Slot Management**
   - Create `SpellSlotManager` class to handle slot assignment and state
   - Methods like `assignSpellToSlot()`, slot position calculations
   - **Benefit**: Better separation of concerns

### Recommendation

The file is currently functional and well-structured. Refactoring should only be done if:
- The file grows beyond 1000 lines
- New features require significant additions
- Performance issues arise from the current structure

The current organization with clear method separation is acceptable for a GUI screen class.

## Other Large Files

No other files currently exceed 300 lines threshold mentioned in user rules. Files are generally well-organized and follow good patterns.

## Notes

- All refactoring should preserve existing functionality
- Test thoroughly after any refactoring
- Consider incremental refactoring rather than large-scale changes



