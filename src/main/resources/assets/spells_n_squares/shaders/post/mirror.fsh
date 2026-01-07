#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform MirrorConfig {
    float Intensity;
    float Horizontal;
    float Vertical;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate mirrored coordinates
    vec2 mirroredCoord = texCoord;
    
    // Horizontal mirror
    if (Horizontal > 0.5) {
        mirroredCoord.x = 1.0 - mirroredCoord.x;
    }
    
    // Vertical mirror
    if (Vertical > 0.5) {
        mirroredCoord.y = 1.0 - mirroredCoord.y;
    }
    
    // Sample from mirrored position
    vec3 mirrored = texture(InSampler, mirroredCoord).rgb;
    
    // Mix between original and mirrored based on intensity
    vec3 finalColor = mix(InTexel.rgb, mirrored, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














