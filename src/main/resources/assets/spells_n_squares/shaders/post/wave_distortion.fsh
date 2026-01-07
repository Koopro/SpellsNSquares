#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform WaveDistortionConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Create wave distortion effect
    float waveAmount = Intensity * 0.05;
    
    // Multiple sine waves for complex distortion
    float waveX = sin(texCoord.y * 10.0 + Time * 2.0) * waveAmount;
    float waveY = cos(texCoord.x * 8.0 + Time * 1.5) * waveAmount;
    
    // Apply wave distortion to UV coordinates
    vec2 distortedCoord = texCoord + vec2(waveX, waveY);
    
    // Sample from distorted position
    vec3 distorted = texture(InSampler, clamp(distortedCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Mix between original and distorted based on intensity
    vec3 finalColor = mix(InTexel.rgb, distorted, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














