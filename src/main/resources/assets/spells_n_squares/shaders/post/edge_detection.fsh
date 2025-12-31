#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform EdgeDetectionConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Sobel operator for edge detection
    float step = 0.002;
    
    // Sample surrounding pixels
    float tl = dot(texture(InSampler, clamp(texCoord + vec2(-step, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float tr = dot(texture(InSampler, clamp(texCoord + vec2(step, -step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float ml = dot(texture(InSampler, clamp(texCoord + vec2(-step, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float mm = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    float mr = dot(texture(InSampler, clamp(texCoord + vec2(step, 0.0), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bl = dot(texture(InSampler, clamp(texCoord + vec2(-step, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float bm = dot(texture(InSampler, clamp(texCoord + vec2(0.0, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    float br = dot(texture(InSampler, clamp(texCoord + vec2(step, step), vec2(0.0), vec2(1.0))).rgb, vec3(0.299, 0.587, 0.114));
    
    // Sobel kernels
    float gx = (-1.0 * tl + 1.0 * tr) + (-2.0 * ml + 2.0 * mr) + (-1.0 * bl + 1.0 * br);
    float gy = (-1.0 * tl - 2.0 * tm - 1.0 * tr) + (1.0 * bl + 2.0 * bm + 1.0 * br);
    
    // Calculate edge magnitude
    float edge = sqrt(gx * gx + gy * gy);
    edge = clamp(edge * 5.0, 0.0, 1.0);
    
    // Invert for outline effect (edges are white, rest is black)
    vec3 edgeColor = vec3(1.0 - edge);
    
    // Mix between original and edge detection based on intensity
    vec3 finalColor = mix(InTexel.rgb, edgeColor, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}







