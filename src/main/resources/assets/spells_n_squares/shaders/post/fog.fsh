#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform FogConfig {
    float Intensity;
    float FogDistance;
    float FogColorR;
    float FogColorG;
    float FogColorB;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Distance-based fog (using Y coordinate as depth approximation)
    float depth = texCoord.y; // Simple depth approximation
    float fogFactor = clamp((depth - FogDistance) / (1.0 - FogDistance), 0.0, 1.0);
    fogFactor *= Intensity;
    
    // Fog color
    vec3 fogColor = vec3(FogColorR, FogColorG, FogColorB);
    
    // Blend with fog
    vec3 fogged = mix(InTexel.rgb, fogColor, fogFactor);
    
    fragColor = vec4(clamp(fogged, 0.0, 1.0), InTexel.a);
}


