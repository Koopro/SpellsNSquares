#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform UnderwaterConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Blue underwater tint
    vec3 underwater = InTexel.rgb;
    underwater.b *= 1.3;
    underwater.r *= 0.7;
    underwater.g *= 0.9;
    
    // Wave distortion
    float waveAmount = Intensity * 0.03;
    float waveX = sin(texCoord.y * 8.0 + Time * 1.5) * waveAmount;
    float waveY = cos(texCoord.x * 6.0 + Time * 1.2) * waveAmount;
    vec2 distortedCoord = texCoord + vec2(waveX, waveY);
    
    // Sample from distorted position
    vec3 distorted = texture(InSampler, clamp(distortedCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Apply blue tint to distorted sample
    distorted.b *= 1.3;
    distorted.r *= 0.7;
    distorted.g *= 0.9;
    
    // Caustics effect (light patterns)
    float caustics = sin(texCoord.x * 10.0 + Time * 2.0) * sin(texCoord.y * 10.0 + Time * 1.8) * 0.1 + 0.9;
    distorted *= caustics;
    
    // Mix between original and underwater based on intensity
    vec3 finalColor = mix(InTexel.rgb, distorted, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


