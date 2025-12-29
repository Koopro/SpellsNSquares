#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform NoiseConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random noise function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Generate noise/grain
    float noiseValue = random(texCoord + vec2(Time * 0.1, Time * 0.1));
    noiseValue = (noiseValue - 0.5) * 2.0; // Range from -1 to 1
    
    // Apply noise based on intensity
    float noiseAmount = Intensity * 0.1;
    vec3 noisy = InTexel.rgb + vec3(noiseValue * noiseAmount);
    
    // Clamp to valid range
    noisy = clamp(noisy, 0.0, 1.0);
    
    // Mix between original and noisy based on intensity
    vec3 finalColor = mix(InTexel.rgb, noisy, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}






