# Spell Icon Textures

This directory contains icon textures for spells. Each spell should have a 16x16 pixel icon texture.

## File Naming Convention

Spell icons should be named after the spell ID path:
- `heal.png` - for the heal spell
- `teleport.png` - for the teleport spell
- `fireball.png` - for the fireball spell
- `lightning.png` - for the lightning spell
- `protego.png` - for the protego spell

## Texture Specifications

- **Size**: 16x16 pixels (recommended)
- **Format**: PNG with transparency
- **Location**: `assets/spells_n_squares/textures/spell/{spell_name}.png`

## For Addons

Addons can create their own spell icons by:
1. Creating a `textures/spell/` directory in their mod's assets folder
2. Naming icons after their spell ID path
3. The system will automatically find icons using the pattern: `textures/spell/{spell_id_path}.png`

Example: If an addon registers a spell with ID `addonmod:coolspell`, the icon should be at:
`assets/addonmod/textures/spell/coolspell.png`

## Default Behavior

If no icon texture is found, the HUD will fall back to displaying the keybind text only.

