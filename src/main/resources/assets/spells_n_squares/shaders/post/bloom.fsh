#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform BloomConfig {
    float Intensity;
    float Threshold;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate brightness
    float brightness = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Threshold for bloom (bright areas glow)
    float bloomThreshold = mix(0.7, 0.5, Intensity * Threshold);
    
    // Extract bright areas
    float bloomAmount = max(0.0, brightness - bloomThreshold);
    bloomAmount = bloomAmount / (1.0 - bloomThreshold);
    
    // Create glow effect by sampling surrounding pixels
    vec3 bloom = vec3(0.0);
    float sampleCount = 0.0;
    
    // Simple blur approximation by sampling nearby pixels
    for (float x = -2.0; x <= 2.0; x += 1.0) {
        for (float y = -2.0; y <= 2.0; y += 1.0) {
            vec2 offset = vec2(x, y) * 0.005 * Intensity;
            vec3 sample = texture(InSampler, clamp(texCoord + offset, vec2(0.0), vec2(1.0))).rgb;
            float sampleBrightness = dot(sample, vec3(0.299, 0.587, 0.114));
            if (sampleBrightness > bloomThreshold) {
                bloom += sample;
                sampleCount += 1.0;
            }
        }
    }
    
    if (sampleCount > 0.0) {
        bloom /= sampleCount;
    }
    
    // Combine original with bloom
    vec3 finalColor = InTexel.rgb + bloom * bloomAmount * Intensity * 0.5;
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


