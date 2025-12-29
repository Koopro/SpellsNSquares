#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform LensFlareConfig {
    float Intensity;
    float FlareX;
    float FlareY;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Flare position
    vec2 flarePos = vec2(FlareX, FlareY);
    
    // Brightness detection
    float brightness = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Distance from flare position
    vec2 toFlare = texCoord - flarePos;
    float dist = length(toFlare);
    
    // Flare streaks
    vec2 dir = normalize(toFlare);
    float streak = 0.0;
    if (brightness > 0.7 && dist < 0.3) {
        float angle = atan(dir.y, dir.x);
        streak = sin(angle * 8.0) * 0.5 + 0.5;
        streak *= (1.0 - dist / 0.3);
    }
    
    // Halo around bright areas
    float halo = 0.0;
    if (brightness > 0.8) {
        halo = (brightness - 0.8) * 5.0;
    }
    
    // Glare effect
    float glare = 0.0;
    if (dist < 0.1) {
        glare = (1.0 - dist / 0.1) * brightness;
    }
    
    // Combine flare effects
    vec3 flare = vec3(streak + halo + glare) * Intensity;
    vec3 finalColor = InTexel.rgb + flare;
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}






