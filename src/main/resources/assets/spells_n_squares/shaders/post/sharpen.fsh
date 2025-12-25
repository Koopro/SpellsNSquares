#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SharpenConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Unsharp mask - sample surrounding pixels
    float step = 0.002;
    
    vec3 tl = texture(InSampler, clamp(texCoord + vec2(-step, -step), vec2(0.0), vec2(1.0))).rgb;
    vec3 tm = texture(InSampler, clamp(texCoord + vec2(0.0, -step), vec2(0.0), vec2(1.0))).rgb;
    vec3 tr = texture(InSampler, clamp(texCoord + vec2(step, -step), vec2(0.0), vec2(1.0))).rgb;
    vec3 ml = texture(InSampler, clamp(texCoord + vec2(-step, 0.0), vec2(0.0), vec2(1.0))).rgb;
    vec3 mm = InTexel.rgb;
    vec3 mr = texture(InSampler, clamp(texCoord + vec2(step, 0.0), vec2(0.0), vec2(1.0))).rgb;
    vec3 bl = texture(InSampler, clamp(texCoord + vec2(-step, step), vec2(0.0), vec2(1.0))).rgb;
    vec3 bm = texture(InSampler, clamp(texCoord + vec2(0.0, step), vec2(0.0), vec2(1.0))).rgb;
    vec3 br = texture(InSampler, clamp(texCoord + vec2(step, step), vec2(0.0), vec2(1.0))).rgb;
    
    // Blur kernel
    vec3 blurred = (tl + tm + tr + ml + mm + mr + bl + bm + br) / 9.0;
    
    // Unsharp mask: original + (original - blurred) * amount
    float amount = Intensity * 2.0;
    vec3 sharpened = mm + (mm - blurred) * amount;
    
    // Mix between original and sharpened
    vec3 finalColor = mix(InTexel.rgb, sharpened, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


