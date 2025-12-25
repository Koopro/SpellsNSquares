#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform OilPaintingConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Color quantization (reduce color palette)
    float levels = 8.0;
    vec3 quantized = floor(InTexel.rgb * levels) / levels;
    
    // Simple brush stroke effect by sampling nearby pixels with slight offset
    vec2 brushOffset = vec2(0.003, 0.003);
    vec3 brush1 = texture(InSampler, clamp(texCoord + brushOffset, vec2(0.0), vec2(1.0))).rgb;
    vec3 brush2 = texture(InSampler, clamp(texCoord - brushOffset, vec2(0.0), vec2(1.0))).rgb;
    
    // Average brush strokes
    vec3 brushStroke = (brush1 + brush2 + quantized) / 3.0;
    brushStroke = floor(brushStroke * levels) / levels;
    
    // Slight directional blur for paint texture
    vec3 blurred = vec3(0.0);
    float sampleCount = 0.0;
    for (float x = -1.0; x <= 1.0; x += 1.0) {
        for (float y = -1.0; y <= 1.0; y += 1.0) {
            vec2 offset = vec2(x, y) * 0.002;
            blurred += texture(InSampler, clamp(texCoord + offset, vec2(0.0), vec2(1.0))).rgb;
            sampleCount += 1.0;
        }
    }
    blurred /= sampleCount;
    blurred = floor(blurred * levels) / levels;
    
    // Mix brush stroke and blurred
    vec3 painting = mix(brushStroke, blurred, 0.5);
    
    // Mix between original and oil painting
    vec3 finalColor = mix(InTexel.rgb, painting, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


