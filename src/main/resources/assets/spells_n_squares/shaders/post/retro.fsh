#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform RetroConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Retro effect: reduced color palette, slight scanlines, and color shift
    vec3 color = InTexel.rgb;
    
    // Reduce color depth for retro look (quantization)
    color = floor(color * 8.0) / 8.0;
    
    // Apply slight color shift (warm/cool split)
    vec3 retro = vec3(
        color.r * 1.1,      // Boost red slightly
        color.g * 0.95,     // Reduce green slightly
        color.b * 1.05      // Boost blue slightly
    );
    
    // Add subtle scanline effect (horizontal lines)
    float scanline = sin(texCoord.y * 800.0) * 0.02 + 1.0;
    retro *= scanline;
    
    // Add slight chromatic aberration (color separation)
    float offset = 0.002 * Intensity;
    vec2 redCoord = texCoord + vec2(offset, 0.0);
    vec2 blueCoord = texCoord - vec2(offset, 0.0);
    
    float red = texture(InSampler, redCoord).r;
    float green = color.g;
    float blue = texture(InSampler, blueCoord).b;
    
    retro = mix(retro, vec3(red, green, blue), 0.3 * Intensity);
    
    // Clamp to prevent oversaturation
    retro = clamp(retro, 0.0, 1.0);
    
    // Mix between original and retro based on intensity
    vec3 finalColor = mix(InTexel.rgb, retro, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














