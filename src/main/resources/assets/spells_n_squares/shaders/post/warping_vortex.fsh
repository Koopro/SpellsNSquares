#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform WarpingVortexConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

void main() {
    // Calculate animation time - use Time uniform for actual rotation
    // Create base pattern from position, add time for rotation
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    float dist = length(offset);
    float angle = atan(offset.y, offset.x);
    
    // Use time for rotation, position for distortion pattern
    float animTime = Time * 0.15 + dist * 2.0;
    
    // Center of screen
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    
    // Calculate distance and angle from center
    float dist = length(offset);
    float angle = atan(offset.y, offset.x);
    
    // Create rotating vortex distortion
    float rotationSpeed = animTime * 2.0;
    float vortexStrength = Intensity * 0.3;
    
    // Warp coordinates based on distance and rotation
    float warpAmount = dist * vortexStrength;
    float newAngle = angle + rotationSpeed + warpAmount * 2.0;
    float newDist = dist * (1.0 + sin(rotationSpeed + dist * 5.0) * vortexStrength * 0.2);
    
    // Calculate warped coordinates
    vec2 warpedCoord = center + vec2(cos(newAngle), sin(newAngle)) * newDist;
    
    // Sample with warped coordinates
    vec4 InTexel = texture(InSampler, clamp(warpedCoord, vec2(0.0), vec2(1.0)));
    
    // Add color shift based on rotation
    float colorShift = sin(rotationSpeed) * Intensity * 0.1;
    vec3 shiftedColor = InTexel.rgb;
    shiftedColor.r += colorShift;
    shiftedColor.b -= colorShift;
    
    // Mix between original and warped based on intensity
    vec4 originalTexel = texture(InSampler, texCoord);
    vec3 finalColor = mix(originalTexel.rgb, shiftedColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), originalTexel.a);
}

