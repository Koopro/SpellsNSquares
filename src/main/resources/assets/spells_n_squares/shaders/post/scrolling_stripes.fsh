#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ScrollingStripesConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate scrolling offset - use Time if available, otherwise create wave pattern
    float scrollSpeed = 0.3;
    // Create diagonal wave pattern for scrolling effect
    float diagonalWave = sin(texCoord.x * 8.0 + texCoord.y * 6.0) * 0.5 + 0.5;
    float scrollOffset = texCoord.y + diagonalWave * 0.3 + Time * scrollSpeed;
    
    // Create stripe pattern
    float stripeWidth = 0.1;
    float stripe = mod(scrollOffset, stripeWidth * 2.0);
    float stripePattern = step(stripeWidth, stripe);
    
    // Alternate between bright and dark stripes
    vec3 stripeColor = mix(InTexel.rgb * 0.5, InTexel.rgb * 1.5, stripePattern);
    
    // Add smooth transition between stripes
    float smoothStripe = smoothstep(0.0, 0.02, abs(stripe - stripeWidth));
    stripeColor = mix(InTexel.rgb * 0.7, InTexel.rgb * 1.3, smoothStripe);
    
    // Mix between original and striped based on intensity
    vec3 finalColor = mix(InTexel.rgb, stripeColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

