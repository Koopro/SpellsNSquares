#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform GlitchConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Simple pseudo-random function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Create glitch effect with random offsets
    vec2 glitchCoord = texCoord;
    
    // Random horizontal offset for glitch effect
    float glitchAmount = Intensity * 0.02;
    float randomValue = random(vec2(Time * 10.0, texCoord.y * 100.0));
    
    // Apply random horizontal shift
    if (randomValue > 0.98) {
        glitchCoord.x += (randomValue - 0.98) * 50.0 * glitchAmount * (random(vec2(Time, texCoord.y)) - 0.5);
    }
    
    // Color channel separation (RGB shift)
    float rOffset = glitchAmount * 5.0 * (random(vec2(Time * 5.0, texCoord.y * 50.0)) - 0.5);
    float gOffset = glitchAmount * 3.0 * (random(vec2(Time * 7.0, texCoord.y * 50.0)) - 0.5);
    float bOffset = glitchAmount * 4.0 * (random(vec2(Time * 9.0, texCoord.y * 50.0)) - 0.5);
    
    vec2 rCoord = vec2(glitchCoord.x + rOffset, glitchCoord.y);
    vec2 gCoord = vec2(glitchCoord.x + gOffset, glitchCoord.y);
    vec2 bCoord = vec2(glitchCoord.x + bOffset, glitchCoord.y);
    
    float r = texture(InSampler, clamp(rCoord, vec2(0.0), vec2(1.0))).r;
    float g = texture(InSampler, clamp(gCoord, vec2(0.0), vec2(1.0))).g;
    float b = texture(InSampler, clamp(bCoord, vec2(0.0), vec2(1.0))).b;
    
    vec3 glitched = vec3(r, g, b);
    
    // Add scanline effect
    float scanline = sin(texCoord.y * 800.0 + Time * 10.0) * 0.02 + 1.0;
    glitched *= scanline;
    
    // Mix between original and glitched based on intensity
    vec3 finalColor = mix(InTexel.rgb, glitched, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}






