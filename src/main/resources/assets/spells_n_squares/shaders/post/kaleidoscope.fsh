#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform KaleidoscopeConfig {
    float Intensity;
    float Segments;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to polar coordinates centered at (0.5, 0.5)
    vec2 center = vec2(0.5, 0.5);
    vec2 coord = texCoord - center;
    
    // Calculate angle and radius
    float angle = atan(coord.y, coord.x);
    float radius = length(coord);
    
    // Create kaleidoscope effect by mirroring segments
    float segments = max(3.0, Segments * Intensity * 8.0 + 3.0);
    angle = mod(angle, 2.0 * 3.14159 / segments);
    
    // Mirror the angle within each segment
    if (angle > 3.14159 / segments) {
        angle = 2.0 * 3.14159 / segments - angle;
    }
    
    // Convert back to cartesian coordinates
    vec2 mirroredCoord = center + radius * vec2(cos(angle), sin(angle));
    
    // Sample from mirrored position
    vec3 kaleidoscope = texture(InSampler, clamp(mirroredCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Mix between original and kaleidoscope based on intensity
    vec3 finalColor = mix(InTexel.rgb, kaleidoscope, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}














