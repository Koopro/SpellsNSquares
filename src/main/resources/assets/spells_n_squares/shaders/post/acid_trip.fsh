#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform AcidTripConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

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
    
    // Extreme UV warping
    vec2 center = vec2(0.5, 0.5);
    vec2 coord = texCoord - center;
    float angle = atan(coord.y, coord.x);
    float radius = length(coord);
    
    // Warp distortion
    float warp = sin(radius * 10.0 + Time * 2.0) * Intensity * 0.1;
    coord += coord * warp;
    vec2 warpedCoord = coord + center;
    
    vec3 color = texture(InSampler, clamp(warpedCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Extreme color cycling
    vec3 hsv = rgb2hsv(color);
    hsv.x = mod(hsv.x + Time * 2.0 * Intensity, 1.0);
    hsv.y = min(1.0, hsv.y * 1.5); // Boost saturation
    color = hsv2rgb(hsv);
    
    // Multiple color overlays
    float overlay1 = sin(Time * 3.0 + texCoord.x * 5.0) * 0.3 + 0.7;
    float overlay2 = cos(Time * 4.0 + texCoord.y * 5.0) * 0.3 + 0.7;
    color.r *= overlay1;
    color.g *= overlay2;
    color.b *= (overlay1 + overlay2) * 0.5;
    
    // Kaleidoscope-like effect
    float segments = 6.0;
    angle = mod(angle, 2.0 * 3.14159 / segments);
    if (angle > 3.14159 / segments) {
        angle = 2.0 * 3.14159 / segments - angle;
    }
    vec2 kaleidCoord = center + radius * vec2(cos(angle), sin(angle));
    vec3 kaleid = texture(InSampler, clamp(kaleidCoord, vec2(0.0), vec2(1.0))).rgb;
    color = mix(color, kaleid, 0.3 * Intensity);
    
    // Mix between original and acid trip
    vec3 finalColor = mix(InTexel.rgb, color, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


