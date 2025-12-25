#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform CartoonConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Posterization (color quantization)
    float levels = 4.0;
    vec3 posterized = floor(InTexel.rgb * levels) / levels;
    
    // Edge detection for outlines
    float texStep = 0.002;
    float tl = dot(texture(InSampler, clamp(texCoord + vec2(-texStep, -texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, -texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tr = dot(texture(InSampler, clamp(texCoord + vec2(texStep, -texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float ml = dot(texture(InSampler, clamp(texCoord + vec2(-texStep, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float mm = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    float mr = dot(texture(InSampler, clamp(texCoord + vec2(texStep, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bl = dot(texture(InSampler, clamp(texCoord + vec2(-texStep, texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float br = dot(texture(InSampler, clamp(texCoord + vec2(texStep, texStep), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    
    float gx = (-1.0 * tl + 1.0 * tr) + (-2.0 * ml + 2.0 * mr) + (-1.0 * bl + 1.0 * br);
    float gy = (-1.0 * tl - 2.0 * tm - 1.0 * tr) + (1.0 * bl + 2.0 * bm + 1.0 * br);
    float edge = sqrt(gx * gx + gy * gy);
    edge = step(0.1, edge); // Binary edge detection
    
    // Apply black outlines
    vec3 cartoon = posterized;
    cartoon = mix(cartoon, vec3(0.0), edge * 0.8);
    
    // Increase saturation for bold colors
    float gray = dot(cartoon, vec3(0.299, 0.587, 0.114));
    cartoon = mix(vec3(gray), cartoon, 1.5);
    
    // Mix between original and cartoon
    vec3 finalColor = mix(InTexel.rgb, cartoon, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}

