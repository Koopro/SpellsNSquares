#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform AnimatedNoiseConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function for noise
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Noise function with multiple octaves
float noise(vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);
    
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    
    vec2 u = f * f * (3.0 - 2.0 * f);
    
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate animation time - use Time for moving noise
    float animTime = Time * 0.2;
    
    // Create animated noise pattern
    vec2 noiseCoord = texCoord * 8.0 + vec2(animTime * 2.0, animTime * 1.5);
    float noiseValue = noise(noiseCoord);
    
    // Create grain/static effect
    float grain = (noiseValue - 0.5) * Intensity * 0.1;
    
    // Add noise to color
    vec3 noisyColor = InTexel.rgb + vec3(grain);
    
    // Add occasional bright spots (like TV static)
    float staticSpot = step(0.95, noiseValue) * Intensity * 0.3;
    noisyColor += vec3(staticSpot);
    
    // Mix between original and noisy
    vec3 finalColor = mix(InTexel.rgb, noisyColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

