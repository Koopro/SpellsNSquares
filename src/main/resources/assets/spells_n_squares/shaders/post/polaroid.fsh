#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform PolaroidConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to grayscale first for that classic polaroid look
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Apply warm, slightly faded color tone (vintage polaroid)
    vec3 polaroid = vec3(
        gray * 1.15,      // Slightly warm red
        gray * 1.05,      // Slightly warm green
        gray * 0.95       // Slightly cool blue
    );
    
    // Add vignette effect (darker edges)
    vec2 center = vec2(0.5, 0.5);
    vec2 dist = texCoord - center;
    float vignette = 1.0 - dot(dist, dist) * 0.8;
    vignette = smoothstep(0.0, 1.0, vignette);
    
    // Apply slight contrast boost
    polaroid = (polaroid - 0.5) * 1.1 + 0.5;
    
    // Clamp to prevent oversaturation
    polaroid = clamp(polaroid, 0.0, 1.0);
    
    // Apply vignette
    polaroid *= vignette;
    
    // Mix between original and polaroid based on intensity
    vec3 finalColor = mix(InTexel.rgb, polaroid, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}


