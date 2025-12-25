#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ParticleRainConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function for particle positions
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate scrolling time for falling particles - use Time for actual falling motion
    float scrollSpeed = 0.5;
    float scrollTime = texCoord.y + Time * scrollSpeed;
    
    // Create particle grid
    float particleDensity = 30.0;
    vec2 particleCoord = floor(texCoord * particleDensity);
    float particleId = random(particleCoord);
    
    // Calculate particle position (falling)
    float particleY = mod(scrollTime + particleId * 2.0, 1.0);
    float particleX = particleCoord.x / particleDensity;
    
    // Distance from particle center
    vec2 particlePos = vec2(particleX, particleY);
    float dist = length(texCoord - particlePos);
    
    // Create particle glow
    float particleSize = 0.02;
    float particleGlow = 1.0 - smoothstep(0.0, particleSize, dist);
    particleGlow *= particleId; // Vary brightness per particle
    
    // Add particle trail
    float trailLength = 0.1;
    float trailY = mod(particleY - dist * trailLength, 1.0);
    float trailGlow = smoothstep(0.0, trailLength, abs(texCoord.y - trailY)) * particleGlow * 0.3;
    
    // Combine particle effects
    vec3 particleColor = InTexel.rgb + vec3(0.0, particleGlow * Intensity * 0.5, particleGlow * Intensity);
    particleColor += vec3(trailGlow * Intensity * 0.2);
    
    // Mix between original and particle effect
    vec3 finalColor = mix(InTexel.rgb, particleColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

