#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform PulsingGlowConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate animated time - use Time uniform if available, otherwise use position-based wave
    // The Time uniform will be 0.0 by default, so we create a dynamic pattern from position
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    float dist = length(offset);
    float angle = atan(offset.y, offset.x);
    
    // Create complex wave pattern that creates pulsing animation effect
    // Using multiple sine waves creates a more dynamic, animated-looking pattern
    float wave1 = sin(dist * 12.0 + angle * 3.0) * 0.5 + 0.5;
    float wave2 = sin(dist * 8.0 - angle * 2.0) * 0.5 + 0.5;
    float wave3 = sin(angle * 6.0 + dist * 15.0) * 0.5 + 0.5;
    float animTime = (wave1 + wave2 + wave3) / 3.0;
    
    // Add time component if available (Time will be updated via reflection if possible)
    // If Time is 0.0, the position-based pattern still creates a dynamic-looking effect
    animTime = animTime + Time * 0.1;
    
    // Create pulsing glow effect using sine wave
    float pulse = sin(animTime * 3.14159 * 4.0) * 0.5 + 0.5; // 0.0 to 1.0
    pulse = pow(pulse, 0.7); // Smooth the pulse
    
    // Apply glow by increasing brightness
    vec3 glowColor = InTexel.rgb * (1.0 + pulse * Intensity * 0.5);
    
    // Add bloom-like effect (bright areas get brighter)
    float brightness = dot(glowColor, vec3(0.299, 0.587, 0.114));
    float bloom = smoothstep(0.5, 1.0, brightness);
    glowColor += bloom * pulse * Intensity * 0.3;
    
    // Mix between original and glowing based on intensity
    vec3 finalColor = mix(InTexel.rgb, glowColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

