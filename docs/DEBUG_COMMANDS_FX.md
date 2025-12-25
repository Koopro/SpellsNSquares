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

#### Unified Shader Commands
All shader effects now use a unified command structure:

```
/spells_n_squaresdebug fx shader <shader_name> <action> [intensity]
```

**Actions:**
- `test [intensity]` - Temporary effect (default intensity: 0.8)
- `on [intensity]` - Toggle on (persistent, default intensity: 0.8)
- `off` - Toggle off

**Available Shaders:**
- **Color Effects:**
  - `invert` - Inverted/negative colors
  - `grayscale` - Grayscale/desaturated
  - `black_and_white` - High contrast black and white
  - `sepia` - Sepia tone (brown vintage)
  - `polaroid` - Vintage polaroid photo effect
  - `retro` - Retro effect with quantization
  - `saturated` - Increased color saturation
  - `color_cycle` - Animated color cycling/rainbow

- **Distortion Effects:**
  - `chromatic` - Chromatic aberration
  - `wave_distortion` - Water ripple/wave distortion
  - `fisheye` - Fisheye lens distortion
  - `heat_haze` - Heat distortion effect
  - `zoom_blur` - Radial blur effect
  - `motion_blur` - Motion trails and directional blur
  - `depth_of_field` - Focus blur, bokeh effects

- **Funny/Cool Effects:**
  - `glitch` - Digital corruption with random offsets
  - `kaleidoscope` - Symmetrical mirror patterns
  - `rgb_shift` - RGB channel separation
  - `bloom` - Glowing/bloom effect
  - `edge_detection` - Outline/sketch effect
  - `pixelation` - Dynamic pixelation
  - `mirror` - Screen flip/mirror effects
  - `noise` - Film grain/static noise
  - `mosaic` - Mosaic effect
  - `tunnel` - Tunnel/abstract 3D effect
  - `underwater` - Blue tint, wave distortion, caustics
  - `drunk` - Wobble, blur, double vision
  - `matrix` - Green code rain effect
  - `old_tv` - CRT scanlines, curvature, static
  - `xray` - Medical imaging look with edge detection
  - `thermal` - Heat map colors (red/yellow/blue)
  - `cartoon` - Cel-shading, posterization
  - `oil_painting` - Artistic brush stroke texture
  - `old_film` - Scratches, dust, flicker, vintage
  - `acid_trip` - Extreme color cycling, warping

- **Useful/Practical Effects:**
  - `outline` - Edge detection with customizable color
  - `fog` - Distance fog, atmospheric perspective
  - `sharpen` - Increase image sharpness
  - `lens_flare` - Light flare effects, glare
  - `vignette` - Darkened edges, center focus
  - `contrast_boost` - Increase contrast for visibility

- **Animated Effects:**
  - `pulsing_glow` - Pulsing brightness/glow effect
  - `scrolling_stripes` - Animated scrolling stripe pattern
  - `warping_vortex` - Animated warping/distortion vortex
  - `particle_rain` - Falling particle effect
  - `color_wave` - Animated color shifting wave
  - `ripple_effect` - Animated water ripples expanding from center
  - `breathing_vignette` - Pulsing dark edges
  - `animated_noise` - Moving static/grain pattern
  - `rotating_kaleidoscope` - Rotating kaleidoscope pattern
  - `energy_pulse` - Pulsing energy waves

**Examples:**
- `/spells_n_squaresdebug fx shader glitch test 0.8`
- `/spells_n_squaresdebug fx shader invert on 0.9`
- `/spells_n_squaresdebug fx shader kaleidoscope off`
- `/spells_n_squaresdebug fx shader bloom test`
- `/spells_n_squaresdebug fx shader color_cycle on 1.0`
- `/spells_n_squaresdebug fx shader underwater test 0.8`
- `/spells_n_squaresdebug fx shader drunk on 0.9`
- `/spells_n_squaresdebug fx shader matrix test 1.0`
- `/spells_n_squaresdebug fx shader old_tv on 0.7`
- `/spells_n_squaresdebug fx shader xray test 0.8`
- `/spells_n_squaresdebug fx shader thermal on 0.9`
- `/spells_n_squaresdebug fx shader cartoon test 0.8`
- `/spells_n_squaresdebug fx shader acid_trip on 1.0`
- `/spells_n_squaresdebug fx shader outline test 0.7`
- `/spells_n_squaresdebug fx shader sharpen on 0.6`
- `/spells_n_squaresdebug fx shader lens_flare test 0.8`

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

# Test shader effects (unified structure)
/spells_n_squaresdebug fx shader glitch test 0.8
/spells_n_squaresdebug fx shader invert test 0.8
/spells_n_squaresdebug fx shader kaleidoscope test 0.9
/spells_n_squaresdebug fx shader grayscale test 0.6
/spells_n_squaresdebug fx shader chromatic test 0.7
/spells_n_squaresdebug fx shader sepia test 0.5
/spells_n_squaresdebug fx shader bloom test 0.8
/spells_n_squaresdebug fx shader color_cycle test 1.0

# Test funny/cool shaders
/spells_n_squaresdebug fx shader underwater test 0.8
/spells_n_squaresdebug fx shader drunk test 0.9
/spells_n_squaresdebug fx shader matrix test 1.0
/spells_n_squaresdebug fx shader old_tv test 0.7
/spells_n_squaresdebug fx shader xray test 0.8
/spells_n_squaresdebug fx shader thermal test 0.9
/spells_n_squaresdebug fx shader cartoon test 0.8
/spells_n_squaresdebug fx shader oil_painting test 0.7
/spells_n_squaresdebug fx shader old_film test 0.8
/spells_n_squaresdebug fx shader acid_trip test 1.0

# Test useful shaders
/spells_n_squaresdebug fx shader outline test 0.7
/spells_n_squaresdebug fx shader fog test 0.6
/spells_n_squaresdebug fx shader sharpen test 0.8
/spells_n_squaresdebug fx shader motion_blur test 0.7
/spells_n_squaresdebug fx shader depth_of_field test 0.8
/spells_n_squaresdebug fx shader lens_flare test 0.9
/spells_n_squaresdebug fx shader vignette test 0.6
/spells_n_squaresdebug fx shader contrast_boost test 0.8

# Test animated shaders
/spells_n_squaresdebug fx shader pulsing_glow test 0.8
/spells_n_squaresdebug fx shader scrolling_stripes test 0.7
/spells_n_squaresdebug fx shader warping_vortex test 0.9
/spells_n_squaresdebug fx shader particle_rain test 0.8
/spells_n_squaresdebug fx shader color_wave test 0.8
/spells_n_squaresdebug fx shader ripple_effect test 0.7
/spells_n_squaresdebug fx shader breathing_vignette test 0.6
/spells_n_squaresdebug fx shader animated_noise test 0.5
/spells_n_squaresdebug fx shader rotating_kaleidoscope test 0.8
/spells_n_squaresdebug fx shader energy_pulse test 0.9

# Toggle shaders on/off
/spells_n_squaresdebug fx shader polaroid on 0.8
/spells_n_squaresdebug fx shader polaroid off
/spells_n_squaresdebug fx shader retro on 0.9
/spells_n_squaresdebug fx shader saturated on 0.7
/spells_n_squaresdebug fx shader glitch on 0.8
/spells_n_squaresdebug fx shader underwater on 0.9
/spells_n_squaresdebug fx shader matrix on 1.0
```

## Notes

- All effects respect the `screenEffectIntensity` config setting
- Effects are client-side only (triggered via network packets from server commands)
- Cut effects will also trigger automatically when entities take damage
- Screen shake is now smooth (no jitter) using time-based sine/cosine waves
