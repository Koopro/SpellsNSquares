#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform InvertedColorsConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Invert colors: 1.0 - color
    vec3 inverted = vec3(1.0) - InTexel.rgb;
    
    // Mix between original and inverted based on intensity
    vec3 finalColor = mix(InTexel.rgb, inverted, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}


