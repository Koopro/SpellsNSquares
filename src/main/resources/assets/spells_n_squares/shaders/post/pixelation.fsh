#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform PixelationConfig {
    float Intensity;
    float BlockSize;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate pixelation block size
    float pixelSize = mix(1.0, max(1.0, BlockSize * 20.0), Intensity);
    
    // Quantize UV coordinates
    vec2 pixelatedCoord = floor(texCoord * pixelSize) / pixelSize;
    
    // Sample from pixelated position
    vec3 pixelated = texture(InSampler, pixelatedCoord).rgb;
    
    // Mix between original and pixelated based on intensity
    vec3 finalColor = mix(InTexel.rgb, pixelated, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}







