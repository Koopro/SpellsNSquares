#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ChromaticAberrationConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    
    // Calculate offset based on distance from center
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = (uv - center) * Intensity * 0.02;
    
    // Sample RGB channels with different offsets for chromatic aberration
    float r = texture(InSampler, uv + offset).r;
    float g = texture(InSampler, uv).g;
    float b = texture(InSampler, uv - offset).b;
    float a = texture(InSampler, uv).a;
    
    // Mix between original and separated colors based on intensity
    vec4 original = texture(InSampler, uv);
    vec4 separated = vec4(r, g, b, a);
    
    fragColor = mix(original, separated, Intensity);
}


