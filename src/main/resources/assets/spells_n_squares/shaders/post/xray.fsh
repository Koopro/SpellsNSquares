#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform XrayConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Edge detection using Sobel operator
    float step = 0.002;
    
    float tl = dot(texture(InSampler, clamp(texCoord + vec2(-step, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tr = dot(texture(InSampler, clamp(texCoord + vec2(step, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float ml = dot(texture(InSampler, clamp(texCoord + vec2(-step, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float mm = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    float mr = dot(texture(InSampler, clamp(texCoord + vec2(step, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bl = dot(texture(InSampler, clamp(texCoord + vec2(-step, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float br = dot(texture(InSampler, clamp(texCoord + vec2(step, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    
    float gx = (-1.0 * tl + 1.0 * tr) + (-2.0 * ml + 2.0 * mr) + (-1.0 * bl + 1.0 * br);
    float gy = (-1.0 * tl - 2.0 * tm - 1.0 * tr) + (1.0 * bl + 2.0 * bm + 1.0 * br);
    float edge = sqrt(gx * gx + gy * gy);
    edge = clamp(edge * 8.0, 0.0, 1.0);
    
    // X-ray look: inverted, high contrast, green tint
    vec3 xray = vec3(1.0 - mm);
    xray = pow(xray, vec3(0.7)); // Increase contrast
    xray *= vec3(0.5, 1.0, 0.7); // Green medical tint
    xray += vec3(edge * 0.5); // Add edge highlights
    
    // Mix between original and x-ray
    vec3 finalColor = mix(InTexel.rgb, xray, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}







