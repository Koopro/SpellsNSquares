#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ContrastBoostConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // S-curve contrast enhancement
    // Map from [0,1] to [0,1] with S-curve
    vec3 color = InTexel.rgb;
    
    // Apply contrast boost
    float contrast = 1.0 + Intensity * 1.5;
    color = (color - 0.5) * contrast + 0.5;
    
    // Clamp to prevent oversaturation
    color = clamp(color, 0.0, 1.0);
    
    // Mix between original and contrast boosted
    vec3 finalColor = mix(InTexel.rgb, color, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}






