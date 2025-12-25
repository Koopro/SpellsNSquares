#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SaturatedConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Increase saturation by desaturating and mixing back
    vec3 color = InTexel.rgb;
    
    // Calculate grayscale (luminance)
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    
    // Boost saturation by mixing away from gray toward original color
    // Higher intensity = more saturation boost
    float saturationBoost = 1.0 + Intensity * 1.5;
    vec3 saturated = mix(vec3(gray), color, saturationBoost);
    
    // Clamp to prevent oversaturation artifacts
    saturated = clamp(saturated, 0.0, 1.0);
    
    // Mix between original and saturated based on intensity
    vec3 finalColor = mix(InTexel.rgb, saturated, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}

