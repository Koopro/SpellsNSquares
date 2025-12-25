#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform MosaicConfig {
    float Intensity;
};

out vec4 fragColor;

// Tile border feature
#define USE_TILE_BORDER
//#define USE_ROUNDED_CORNERS

void main() {
    vec4 original = texture(InSampler, texCoord);
    
    if (Intensity < 0.01) {
        fragColor = original;
        return;
    }
    
    const float minTileSize = 2.0;
    const float maxTileSize = 64.0;
    const float textureSamplesCount = 3.0;
    const float textureEdgeOffset = 0.005;
    const float borderSize = 1.0;

    vec2 texSize = textureSize(InSampler, 0);
    vec2 fragCoord = texCoord * texSize;
    
    // Map intensity to tile size range
    // Intensity 0.0 = no pixelation, Intensity 1.0 = maximum pixelation
    // Use larger tile sizes for more visible effect
    float tileSize = mix(minTileSize, maxTileSize, Intensity * Intensity); // Square for more dramatic effect
    tileSize = max(1.0, floor(tileSize)); // Ensure at least 1 pixel
    
    // Calculate which tile this fragment belongs to
    vec2 tileNumber = floor(fragCoord / tileSize);
    
    // Calculate the center of the tile in texture coordinates
    vec2 tileCenter = (tileNumber + vec2(0.5)) * tileSize / texSize;
    
    // Sample from the center of the tile (simple pixelation)
    // No need to flip Y - Minecraft's screenquad already handles this correctly
    tileCenter = clamp(tileCenter, vec2(0.0), vec2(1.0));
    vec4 tileColor = texture(InSampler, tileCenter);

#if defined(USE_TILE_BORDER) || defined(USE_ROUNDED_CORNERS)
    // Calculate position within tile
    vec2 pixelNumber = floor(fragCoord - (tileNumber * tileSize));
    pixelNumber = mod(pixelNumber + borderSize, tileSize);
    
#if defined(USE_TILE_BORDER)
    float pixelBorder = step(min(pixelNumber.x, pixelNumber.y), borderSize) * step(borderSize * 2.0 + 1.0, tileSize);
#else
    float pixelBorder = step(pixelNumber.x, borderSize) * step(pixelNumber.y, borderSize) * step(borderSize * 2.0 + 1.0, tileSize);
#endif
    // Darken borders
    tileColor *= (1.0 - pixelBorder * 0.3);
#endif

    // Mix with original based on intensity for smooth transitions
    fragColor = mix(original, tileColor, Intensity);
}
