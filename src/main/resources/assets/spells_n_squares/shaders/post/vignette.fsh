#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform VignetteConfig {
    float Intensity;
    float Radius;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Center point
    vec2 center = vec2(0.5, 0.5);
    float dist = length(texCoord - center);
    
    // Vignette falloff
    float vignette = 1.0 - smoothstep(Radius, 1.0, dist);
    vignette = mix(1.0, vignette, Intensity);
    
    // Apply vignette
    vec3 finalColor = InTexel.rgb * vignette;
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


