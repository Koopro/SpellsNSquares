#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform OldFilmConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Sepia/vintage color grading
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    vec3 sepia = vec3(gray * 1.2, gray * 1.0, gray * 0.8);
    
    // Scratches overlay
    float scratch = random(vec2(Time * 0.1, texCoord.y * 1000.0));
    float scratchLine = step(0.98, scratch);
    sepia = mix(sepia, vec3(0.0), scratchLine * 0.3);
    
    // Dust particles
    float dust = random(vec2(Time * 0.05, texCoord * 50.0));
    float dustParticle = step(0.95, dust);
    sepia = mix(sepia, vec3(1.0), dustParticle * 0.2);
    
    // Flicker effect
    float flicker = sin(Time * 15.0) * 0.1 + 0.9;
    sepia *= flicker;
    
    // Vignette
    vec2 center = vec2(0.5, 0.5);
    float dist = length(texCoord - center);
    float vignette = 1.0 - dist * 0.5;
    sepia *= vignette;
    
    // Mix between original and old film
    vec3 finalColor = mix(InTexel.rgb, sepia, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}














