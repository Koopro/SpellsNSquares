#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform EnergyPulseConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate animation time - use Time for pulsing energy waves
    float animTime = Time * 0.12;
    
    // Center of screen
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    float dist = length(offset);
    
    // Create pulsing energy waves
    float waveSpeed = 3.0;
    float waveFrequency = 12.0;
    float wave = sin((dist * waveFrequency) - (animTime * waveSpeed)) * 0.5 + 0.5;
    
    // Create multiple wave rings
    float wave1 = sin((dist * waveFrequency * 1.0) - (animTime * waveSpeed * 1.0));
    float wave2 = sin((dist * waveFrequency * 1.3) - (animTime * waveSpeed * 1.1));
    float wave3 = sin((dist * waveFrequency * 0.8) - (animTime * waveSpeed * 0.9));
    
    // Combine waves
    float combinedWave = (wave1 + wave2 + wave3) / 3.0;
    combinedWave = combinedWave * 0.5 + 0.5;
    
    // Create energy glow effect
    float energyGlow = pow(combinedWave, 2.0) * Intensity;
    
    // Add energy color (cyan/blue)
    vec3 energyColor = vec3(0.0, energyGlow * 0.8, energyGlow);
    
    // Apply energy to image
    vec3 pulsedColor = InTexel.rgb + energyColor;
    
    // Add brightness boost in wave peaks
    float brightnessBoost = smoothstep(0.3, 0.7, combinedWave) * Intensity * 0.2;
    pulsedColor *= (1.0 + brightnessBoost);
    
    // Mix between original and energy pulse
    vec3 finalColor = mix(InTexel.rgb, pulsedColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

