#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform TunnelConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Calculate animated time based on view direction
// Use texture coordinates to create a tunnel that responds to where you look
float getTime() {
    // Create a scrolling effect based on screen position
    // This makes the tunnel change as you look around
    // Center of screen is (0.5, 0.5), so we offset from center
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    
    // Use angle and distance from center to create view-dependent animation
    float angle = atan(offset.y, offset.x);
    float dist = length(offset);
    
    // Create scrolling based on view direction (angle) and position
    float scrollSpeed = 0.3;
    float timeOffset = angle * 0.5 + dist * 2.0;
    
    // Add a slow global time component if Time uniform is provided
    if (Time > 0.0) {
        return Time * scrollSpeed + timeOffset;
    }
    
    // Use position-based scrolling for visible animation
    return timeOffset * scrollSpeed;
}

void main() {
    vec4 original = texture(InSampler, texCoord);
    
    if (Intensity < 0.01) {
        fragColor = original;
        return;
    }
    
    vec2 texSize = textureSize(InSampler, 0);
    vec2 r = texSize;
    vec2 I = texCoord * r; // Fragment coordinates
    
    vec3 a = vec3(0.7, -0.7, 0.1); // Approximately normalized rotation axis
    vec4 O = vec4(0.0);
    
    float i = 0.0;
    float t = 0.0;
    float v = 0.1;
    float s;
    
    // Main loop - creates layered 3D tunnel effect
    for (i = 0.0; i < 40.0; i++) {
        t += v * 0.1;
        
        // Start with isometric view, the term t*0.3 makes deeper layers move slower
        vec3 p = vec3((60.0 + t * 0.3) * (I + I - r.xy) / r.y, t);
        
        // Rotation by 180 degrees along axis vec3 a for a nice viewing angle
        // Followed by absolute value transformation for 4-fold symmetry
        p = abs(a * dot(a, p) * 2.0 - p);
        
        // Move layers in diagonal direction over time
        float animTime = getTime();
        p.xy -= animTime * 30.0; // Speed for visible animation
        
        // Triangle wave turbulence for patterns
        for (s = 0.02; s <= 0.04; s += 0.01) {
            p.xy += abs(fract(p.yx * s) - 0.5) / s;
        }
        
        // Repetition in all directions for layers & repeating patterns
        p = mod(p, 20.0) - 10.0;
        
        // Cosine to stretch out y values, looks nice and we can use length(p) instead of length(p.xz) for cylinder
        p.y = cos(p.y * 0.3);
        
        // Start with cylinder distance function, then add noise
        // The scaling by 4.0 makes the noise term only relevant when we are nearby/inside the cylinder
        v = 4.0 * max(0.01, length(p) - 5.0);
        for (s = 1.0; s < 3.0; s += 1.0) {
            v += abs(dot(sin(p * s) / s, vec3(1.0)));
        }
        
        // Color based on x coordinate which results in some areas having a single solid color
        // Color intensity decreases for deeper layers
        O += 0.4 / (25.0 + t) * exp(sin(p.x + vec4(1.0, 3.0, 5.0, 0.0))) / v;
    }
    
    // Tone mapping, forces rgb values between [0,1]
    O = tanh(O);
    
    // Mix tunnel effect with original scene - tunnel should overlay, not replace
    // Use additive blending for tunnel effect on top of original
    vec4 tunnelColor = O * Intensity;
    fragColor = original + tunnelColor * 0.5; // Additive blend with reduced intensity
    fragColor = mix(original, fragColor, Intensity);
}

