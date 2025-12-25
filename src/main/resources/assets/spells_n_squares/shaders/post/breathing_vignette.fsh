#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform BreathingVignetteConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate animation time - use Time for breathing pulse
    float animTime = Time * 0.08;
    
    // Create pulsing effect
    float pulse = sin(animTime * 3.14159 * 2.0) * 0.5 + 0.5;
    
    // Center of screen
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    float dist = length(offset);
    
    // Vignette effect (dark edges)
    float vignetteRadius = 0.7 + pulse * 0.2; // Breathing radius
    float vignetteFalloff = 0.3;
    float vignette = smoothstep(vignetteRadius, vignetteRadius - vignetteFalloff, dist);
    
    // Apply vignette with breathing
    float breathingVignette = mix(vignette, 1.0, pulse * Intensity * 0.3);
    vec3 vignettedColor = InTexel.rgb * breathingVignette;
    
    // Mix between original and vignetted
    vec3 finalColor = mix(InTexel.rgb, vignettedColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

