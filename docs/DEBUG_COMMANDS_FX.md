# Debug Commands for FX Testing

## Overview
Debug commands have been added to test all the new rendering and FX features.

## Commands

### Screen Effects (`/spells_n_squaresdebug fx`)

#### Screen Shake
```
/spells_n_squaresdebug fx shake [intensity] [duration]
```
- **Default**: `/spells_n_squaresdebug fx shake` (intensity=0.1, duration=20)
- **With intensity**: `/spells_n_squaresdebug fx shake 0.5`
- **Full**: `/spells_n_squaresdebug fx shake 0.5 30`
- **Effect**: Triggers smooth screen shake using sine/cosine waves

#### Screen Flash
```
/spells_n_squaresdebug fx overlay flash [opacity]
```
- **Default**: `/spells_n_squaresdebug fx overlay flash` (opacity=0.2)
- **With opacity**: `/spells_n_squaresdebug fx overlay flash 0.5`
- **Effect**: Full-screen white flash

#### Damage Vignette
```
/spells_n_squaresdebug fx overlay vignette
```
- **Effect**: Red vignette overlay at screen edges

#### Cut Effects
```
/spells_n_squaresdebug fx cut [intensity] [duration]
```
- **Default**: `/spells_n_squaresdebug fx cut` (intensity=1.0, duration=30)
- **With intensity**: `/spells_n_squaresdebug fx cut 0.5`
- **Full**: `/spells_n_squaresdebug fx cut 0.5 40`
- **Effect**: Triggers a cut effect at screen center (red line that fades)

#### Shader Effects
```
/spells_n_squaresdebug fx shader <effect_type> [intensity]
```
- **Basic Effects**:
  - `spell_cast` - White flash + shake
  - `time_distortion` - Yellow/gold glow + shake
  - `invisibility` - Purple/blue shimmer
  - `magical_aura` - Cyan/blue magical aura
  
- **Color Effects**:
  - `inverted` / `invert` / `negative` - Inverted/negative colors effect
  - `hue_max` / `saturated` / `vibrant` - Max saturation, vibrant rainbow colors
  - `grayscale` / `black_and_white` / `bw` - Grayscale/desaturated effect
  - `sepia` - Sepia tone (brown vintage look)
  
- **Special Effects**:
  - `chromatic` / `chromatic_aberration` - Color separation/chromatic aberration
  - `night_vision` / `nightvision` - Green night vision overlay
  - `acid` / `trippy` - Trippy multi-color effect with shake
  
- **Examples**: 
  - `/spells_n_squaresdebug fx shader inverted`
  - `/spells_n_squaresdebug fx shader hue_max 0.8`
  - `/spells_n_squaresdebug fx shader acid 1.0`
- **Effect**: Triggers the specified shader effect (falls back to screen effects if shaders unavailable)

## Testing Tips

1. **Screen Shake**: Try different intensities (0.1-1.0) to see smooth motion
2. **Cut Effects**: Will appear as red lines on screen, fade over time
3. **Shader Effects**: Only work if shaders are enabled in config
4. **Multiple Effects**: You can trigger multiple effects simultaneously

## Example Usage

```bash
# Test screen shake
/spells_n_squaresdebug fx shake 0.3 25

# Test cut effect
/spells_n_squaresdebug fx cut 0.8 40

# Test screen flash
/spells_n_squaresdebug fx overlay flash 0.4

# Test shader effects
/spells_n_squaresdebug fx shader spell_cast
/spells_n_squaresdebug fx shader inverted 0.8
/spells_n_squaresdebug fx shader hue_max 1.0
/spells_n_squaresdebug fx shader grayscale 0.6
/spells_n_squaresdebug fx shader chromatic 0.7
/spells_n_squaresdebug fx shader sepia 0.5
/spells_n_squaresdebug fx shader night_vision 0.8
/spells_n_squaresdebug fx shader acid 1.0
```

## Notes

- All effects respect the `screenEffectIntensity` config setting
- Effects are client-side only (triggered via network packets from server commands)
- Cut effects will also trigger automatically when entities take damage
- Screen shake is now smooth (no jitter) using time-based sine/cosine waves
