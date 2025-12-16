#version 150

in vec4 vColor;
in vec2 vUV;

uniform float Time;
uniform vec2 CutStart;
uniform vec2 CutEnd;
uniform float Intensity;

out vec4 fragColor;

// Calculate distance from a point to a line segment
float distanceToLineSegment(vec2 p, vec2 a, vec2 b) {
    vec2 ab = b - a;
    vec2 ap = p - a;
    
    float abLength = length(ab);
    if (abLength < 0.001) {
        return length(ap);
    }
    
    float t = clamp(dot(ap, ab) / (abLength * abLength), 0.0, 1.0);
    vec2 closest = a + t * ab;
    return length(p - closest);
}

void main() {
    // Calculate distance to cut line
    float dist = distanceToLineSegment(vUV, CutStart, CutEnd);
    
    // Create glow effect - brighter near the line, fades with distance
    float glow = exp(-dist * 20.0) * Intensity;
    
    // Add subtle pulse animation
    float pulse = 0.05 * sin(Time * 2.0);
    glow += pulse;
    
    // Fade out over time (Intensity decreases over time externally)
    float alpha = glow * vColor.a;
    
    // Red color with gradient
    vec3 coreColor = vec3(1.0, 0.0, 0.0); // Bright red at center
    vec3 edgeColor = vec3(0.5, 0.0, 0.0);  // Darker red at edges
    float colorMix = smoothstep(0.0, 0.1, dist);
    vec3 color = mix(coreColor, edgeColor, colorMix);
    
    // Apply incoming color modulation
    color *= vColor.rgb;
    
    if (alpha <= 0.01) discard;
    
    fragColor = vec4(color, alpha);
}






