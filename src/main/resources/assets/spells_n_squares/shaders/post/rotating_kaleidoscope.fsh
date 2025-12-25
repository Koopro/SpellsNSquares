#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform RotatingKaleidoscopeConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    // Calculate animation time - use Time for rotation
    float animTime = Time * 0.15;
    
    // Center of screen
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    
    // Calculate angle and distance
    float angle = atan(offset.y, offset.x);
    float dist = length(offset);
    
    // Rotate coordinates
    float rotation = animTime * 2.0;
    float newAngle = angle + rotation;
    
    // Create kaleidoscope effect (mirror segments)
    float segments = 8.0;
    float segmentAngle = mod(newAngle, 3.14159 * 2.0 / segments);
    float mirroredAngle = min(segmentAngle, 3.14159 * 2.0 / segments - segmentAngle);
    
    // Calculate mirrored coordinates
    float finalAngle = floor(newAngle / (3.14159 * 2.0 / segments)) * (3.14159 * 2.0 / segments) + mirroredAngle;
    vec2 kaleidCoord = center + vec2(cos(finalAngle), sin(finalAngle)) * dist;
    
    // Sample with kaleidoscope coordinates
    vec4 InTexel = texture(InSampler, clamp(kaleidCoord, vec2(0.0), vec2(1.0)));
    
    // Add color shift based on segment
    float segmentId = floor(newAngle / (3.14159 * 2.0 / segments));
    float colorShift = sin(segmentId + animTime) * Intensity * 0.1;
    vec3 shiftedColor = InTexel.rgb;
    shiftedColor.r += colorShift;
    shiftedColor.b -= colorShift;
    
    // Mix between original and kaleidoscope
    vec4 originalTexel = texture(InSampler, texCoord);
    vec3 finalColor = mix(originalTexel.rgb, shiftedColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), originalTexel.a);
}

