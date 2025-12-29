#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform DrunkConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Wobble effect using sine waves
    float wobbleAmount = Intensity * 0.05;
    float wobbleX = sin(Time * 3.0 + texCoord.y * 10.0) * wobbleAmount;
    float wobbleY = cos(Time * 2.5 + texCoord.x * 8.0) * wobbleAmount;
    vec2 wobbledCoord = texCoord + vec2(wobbleX, wobbleY);
    
    // Double vision effect
    vec2 offset = vec2(Intensity * 0.01, 0.0);
    vec3 sample1 = texture(InSampler, clamp(wobbledCoord, vec2(0.0), vec2(1.0))).rgb;
    vec3 sample2 = texture(InSampler, clamp(wobbledCoord + offset, vec2(0.0), vec2(1.0))).rgb;
    
    // Blend double vision
    vec3 doubleVision = mix(sample1, sample2, 0.5);
    
    // Color shift (slight desaturation and color shift)
    float gray = dot(doubleVision, vec3(0.299, 0.587, 0.114));
    vec3 shifted = mix(doubleVision, vec3(gray * 1.1, gray * 0.9, gray * 0.95), Intensity * 0.3);
    
    // Mix between original and drunk effect
    vec3 finalColor = mix(InTexel.rgb, shifted, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}






