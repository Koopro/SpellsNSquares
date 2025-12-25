#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform FisheyeConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 original = texture(InSampler, texCoord);
    
    if (Intensity < 0.01) {
        fragColor = original;
        return;
    }
    
    vec2 center = vec2(0.5, 0.5);
    
    // Calculate distance and angle from center
    vec2 coord = texCoord - center;
    float dist = length(coord);
    
    // Early exit if we're at the center (no distortion needed)
    if (dist < 0.001) {
        fragColor = original;
        return;
    }
    
    float angle = atan(coord.y, coord.x);
    
    // Apply fisheye distortion (barrel/pincushion distortion)
    // Fisheye lenses create a "bulge" effect - center appears magnified
    // We use a different formula: r' = r * (1 - k * r^2) for barrel distortion
    // Or we can use: r' = atan(r * k) / atan(k) for proper fisheye
    
    float maxDist = 0.707; // Maximum distance from center (diagonal = sqrt(0.5^2 + 0.5^2))
    float normalizedDist = dist / maxDist;
    
    // Fisheye distortion: use stronger barrel distortion formula
    // Higher intensity = stronger fisheye effect
    // Use a more aggressive formula for visible fisheye effect
    float strength = Intensity * 3.0; // Much stronger effect (0 to 3.0)
    
    // Barrel distortion: r' = r * (1 + k * r^2)
    // For fisheye, we want the center to bulge outward
    float k = strength * 0.8; // Barrel distortion coefficient
    float distortedDist = normalizedDist * (1.0 + k * normalizedDist * normalizedDist);
    
    // Clamp to prevent sampling outside bounds
    distortedDist = min(distortedDist, 1.2); // Allow slight oversampling for edge effect
    
    // Convert back to texture coordinates
    vec2 distortedCoord = center + vec2(
        cos(angle) * distortedDist * maxDist,
        sin(angle) * distortedDist * maxDist
    );
    
    // Clamp coordinates to valid texture range
    distortedCoord = clamp(distortedCoord, vec2(0.0), vec2(1.0));
    
    // Sample the distorted texture
    vec4 distortedColor = texture(InSampler, distortedCoord);
    
    // Always apply the distortion, don't mix - fisheye should be visible
    fragColor = distortedColor;
}

