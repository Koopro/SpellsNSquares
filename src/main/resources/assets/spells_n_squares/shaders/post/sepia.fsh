#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SepiaConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to grayscale first
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Apply sepia tone transformation
    // Classic sepia formula: brownish vintage look
    vec3 sepia = vec3(
        gray * 1.2,      // Red channel - slightly brighter
        gray * 1.0,      // Green channel - neutral
        gray * 0.8       // Blue channel - reduced for brownish tint
    );
    
    // Clamp to prevent oversaturation
    sepia = clamp(sepia, 0.0, 1.0);
    
    // Mix between original and sepia based on intensity
    vec3 finalColor = mix(InTexel.rgb, sepia, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














