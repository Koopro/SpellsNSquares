#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform RgbShiftConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // RGB channel separation with offset
    float shiftAmount = Intensity * 0.01;
    
    // Offset each channel in different directions
    vec2 rOffset = vec2(shiftAmount * 2.0, 0.0);
    vec2 gOffset = vec2(0.0, 0.0);
    vec2 bOffset = vec2(-shiftAmount * 2.0, 0.0);
    
    // Sample each channel separately
    float r = texture(InSampler, clamp(texCoord + rOffset, vec2(0.0), vec2(1.0))).r;
    float g = texture(InSampler, clamp(texCoord + gOffset, vec2(0.0), vec2(1.0))).g;
    float b = texture(InSampler, clamp(texCoord + bOffset, vec2(0.0), vec2(1.0))).b;
    
    vec3 rgbShifted = vec3(r, g, b);
    
    // Mix between original and RGB shifted based on intensity
    vec3 finalColor = mix(InTexel.rgb, rgbShifted, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}


