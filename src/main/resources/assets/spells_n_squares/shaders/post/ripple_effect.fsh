#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform RippleEffectConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Center of screen (or multiple ripple centers)
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    float dist = length(offset);
    
    // Calculate animation time - use Time for expanding ripples
    float animTime = Time * 0.15;
    
    // Create expanding ripples
    float rippleSpeed = 2.0;
    float rippleFrequency = 8.0;
    float ripple = sin((dist * rippleFrequency) - (animTime * rippleSpeed)) * 0.5 + 0.5;
    
    // Create multiple ripple rings
    float ripple1 = sin((dist * rippleFrequency * 1.0) - (animTime * rippleSpeed * 1.0));
    float ripple2 = sin((dist * rippleFrequency * 1.5) - (animTime * rippleSpeed * 1.2));
    float ripple3 = sin((dist * rippleFrequency * 2.0 - animTime * rippleSpeed * 0.8));
    
    // Combine ripples
    float combinedRipple = (ripple1 + ripple2 + ripple3) / 3.0;
    combinedRipple = combinedRipple * 0.5 + 0.5;
    
    // Create distortion based on ripple
    float distortion = combinedRipple * Intensity * 0.05;
    vec2 distortedCoord = texCoord + normalize(offset) * distortion;
    
    // Sample with distortion
    vec4 distortedTexel = texture(InSampler, clamp(distortedCoord, vec2(0.0), vec2(1.0)));
    
    // Add ripple highlight
    float rippleHighlight = smoothstep(0.4, 0.6, combinedRipple) * Intensity * 0.3;
    vec3 rippleColor = distortedTexel.rgb + vec3(rippleHighlight);
    
    // Mix between original and rippled
    vec3 finalColor = mix(InTexel.rgb, rippleColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

