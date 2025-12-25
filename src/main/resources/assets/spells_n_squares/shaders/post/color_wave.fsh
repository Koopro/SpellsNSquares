#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ColorWaveConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Convert RGB to HSV
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

// Convert HSV to RGB
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate wave animation - use Time for actual wave movement
    float waveSpeed = 0.4;
    float waveTime = texCoord.x + Time * waveSpeed;
    
    // Create wave pattern (sine wave moving across screen)
    float wave = sin(waveTime * 3.14159 * 4.0) * 0.5 + 0.5;
    
    // Convert to HSV for color shifting
    vec3 hsv = rgb2hsv(InTexel.rgb);
    
    // Shift hue based on wave position
    float hueShift = wave * Intensity * 0.3;
    hsv.x = mod(hsv.x + hueShift, 1.0);
    
    // Increase saturation in wave peaks
    hsv.y = mix(hsv.y, min(hsv.y * 1.5, 1.0), wave * Intensity * 0.5);
    
    // Convert back to RGB
    vec3 waveColor = hsv2rgb(hsv);
    
    // Add brightness variation
    waveColor *= (1.0 + wave * Intensity * 0.2);
    
    // Mix between original and wave effect
    vec3 finalColor = mix(InTexel.rgb, waveColor, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

