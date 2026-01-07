#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform HeatHazeConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Simple noise function
float noise(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

// Smooth noise
float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    
    float a = noise(i);
    float b = noise(i + vec2(1.0, 0.0));
    float c = noise(i + vec2(0.0, 1.0));
    float d = noise(i + vec2(1.0, 1.0));
    
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Create heat haze distortion using noise
    float distortionAmount = Intensity * 0.02;
    
    // Animated noise for heat waves
    vec2 noiseCoord = texCoord * 5.0 + vec2(Time * 0.5, Time * 0.3);
    float noiseValue = smoothNoise(noiseCoord);
    
    // Create wavy distortion
    vec2 distortion = vec2(
        sin(noiseValue * 6.28318) * distortionAmount,
        cos(noiseValue * 6.28318) * distortionAmount
    );
    
    // Apply distortion to UV coordinates
    vec2 distortedCoord = texCoord + distortion;
    
    // Sample from distorted position
    vec3 distorted = texture(InSampler, clamp(distortedCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Mix between original and distorted based on intensity
    vec3 finalColor = mix(InTexel.rgb, distorted, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














