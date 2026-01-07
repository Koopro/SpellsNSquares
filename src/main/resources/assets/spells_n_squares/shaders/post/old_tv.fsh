#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform OldTvConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function for static
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Screen curvature (barrel distortion)
    vec2 center = vec2(0.5, 0.5);
    vec2 coord = texCoord - center;
    float dist = length(coord);
    vec2 curvedCoord = coord * (1.0 + dist * dist * Intensity * 0.3) + center;
    
    vec3 color = texture(InSampler, clamp(curvedCoord, vec2(0.0), vec2(1.0))).rgb;
    
    // Scanlines
    float scanline = sin(texCoord.y * 600.0) * 0.05 + 0.95;
    color *= scanline;
    
    // Static noise
    float staticNoise = random(vec2(Time * 20.0, texCoord * 100.0));
    staticNoise = (staticNoise - 0.5) * Intensity * 0.1;
    color += vec3(staticNoise);
    
    // Flicker effect
    float flicker = sin(Time * 10.0) * 0.05 + 0.95;
    color *= flicker;
    
    // Color bleed (RGB separation)
    float bleedAmount = Intensity * 0.01;
    float r = texture(InSampler, clamp(curvedCoord + vec2(bleedAmount, 0.0), vec2(0.0), vec2(1.0))).r;
    float g = color.g;
    float b = texture(InSampler, clamp(curvedCoord - vec2(bleedAmount, 0.0), vec2(0.0), vec2(1.0))).b;
    color = vec3(r, g, b);
    
    // Mix between original and old TV effect
    vec3 finalColor = mix(InTexel.rgb, color, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}














