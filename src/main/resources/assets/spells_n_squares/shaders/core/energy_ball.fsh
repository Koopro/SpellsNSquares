#version 150

in vec4 vColor;
in vec2 vUV;

uniform float Time;

out vec4 fragColor;

void main() {
    // Centered UV for radial gradient
    vec2 uv = vUV - vec2(0.5);
    float r = length(uv);
    
    // Animated pulse effect
    float pulse = 0.1 * sin(Time * 1.5) + 0.05 * sin(Time * 3.0);
    float baseRadius = 0.4;
    float radius = baseRadius + pulse;
    
    // Create bloom effect with multiple falloff zones
    // Outer bloom (soft glow extending beyond the core)
    float outerBloom = smoothstep(radius * 1.5, radius * 0.8, r);
    
    // Mid bloom (medium glow)
    float midBloom = smoothstep(radius * 1.2, radius * 0.6, r);
    
    // Inner core (bright center)
    float core = smoothstep(radius * 0.5, 0.0, r);
    
    // Combine bloom layers with different intensities
    float bloom = outerBloom * 0.3 + midBloom * 0.5 + core * 1.0;
    
    // Color gradient: white core -> yellow -> orange rim
    vec3 coreColor = vec3(1.0, 1.0, 1.0); // Bright white
    vec3 midColor = vec3(1.0, 0.95, 0.7); // Warm yellow-white
    vec3 rimColor = vec3(1.0, 0.8, 0.4);  // Golden orange
    
    // Mix colors based on distance from center
    vec3 color;
    if (r < radius * 0.3) {
        // Core: pure white
        color = coreColor;
    } else if (r < radius * 0.7) {
        // Mid: blend white to yellow
        float t = (r - radius * 0.3) / (radius * 0.4);
        color = mix(coreColor, midColor, t);
    } else {
        // Rim: blend yellow to orange
        float t = (r - radius * 0.7) / (radius * 0.5);
        color = mix(midColor, rimColor, t);
    }
    
    // Add subtle animated wobble to the edge
    float wobble = 0.02 * sin(Time * 2.0 + r * 10.0);
    float edgeDist = abs(r - radius);
    float edgeGlow = exp(-edgeDist * 8.0) * (1.0 + wobble);
    
    // Combine all effects
    float alpha = bloom * vColor.a;
    alpha = max(alpha, edgeGlow * 0.5); // Ensure edge glow is visible
    
    // Boost brightness for bloom effect
    color *= (1.0 + bloom * 0.5); // Make it brighter
    
    // Apply incoming color modulation
    color *= vColor.rgb;
    
    if (alpha <= 0.01) discard;
    
    fragColor = vec4(color, alpha);
}














