#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform UnifiedConfig {
    float Desat;
    float RedTint;
    float BlueTint;
    float Contrast;
    float Bright;
    float Satur;
    float Invert;
    float Chrom;
    float Vignette;
    float RadBlur;
    float Barrel;
    float Edge;
    float EdgeThk;
    float EdgeGlow;
    float Bloom;
    float Blur;
    float Sharp;
    float ShakeX;
    float ShakeY;
};

out vec4 fragColor;

void main() {
    // Apply screen shake offset
    vec2 uv = texCoord;
    uv.x += ShakeX * 0.01;
    uv.y += ShakeY * 0.01;
    uv = clamp(uv, vec2(0.0), vec2(1.0));
    
    vec4 InTexel = texture(InSampler, uv);
    vec3 color = InTexel.rgb;
    
    // Apply brightness
    color = color * Bright;
    
    // Apply contrast
    color = (color - 0.5) * Contrast + 0.5;
    
    // Apply saturation
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, Satur);
    
    // Apply desaturation
    color = mix(color, vec3(gray), Desat);
    
    // Apply color tints
    color.r += RedTint * 0.1;
    color.b += BlueTint * 0.1;
    
    // Apply chromatic aberration
    if (Chrom > 0.0) {
        vec2 chromOffset = vec2(Chrom * 0.005, 0.0);
        vec3 chromColor;
        chromColor.r = texture(InSampler, clamp(uv - chromOffset, vec2(0.0), vec2(1.0))).r;
        chromColor.g = color.g;
        chromColor.b = texture(InSampler, clamp(uv + chromOffset, vec2(0.0), vec2(1.0))).b;
        color = mix(color, chromColor, Chrom);
    }
    
    // Apply barrel distortion
    if (Barrel > 0.0) {
        vec2 center = vec2(0.5, 0.5);
        vec2 coord = uv - center;
        float dist = length(coord);
        float factor = 1.0 + Barrel * dist * dist;
        vec2 distorted = center + coord * factor;
        if (distorted.x >= 0.0 && distorted.x <= 1.0 && distorted.y >= 0.0 && distorted.y <= 1.0) {
            color = mix(color, texture(InSampler, distorted).rgb, Barrel);
        }
    }
    
    // Apply radial blur
    if (RadBlur > 0.0) {
        vec2 center = vec2(0.5, 0.5);
        vec2 dir = normalize(uv - center);
        vec3 blurColor = vec3(0.0);
        float samples = 5.0;
        for (float i = 0.0; i < samples; i += 1.0) {
            vec2 samplePos = uv - dir * (i / samples) * RadBlur * 0.01;
            if (samplePos.x >= 0.0 && samplePos.x <= 1.0 && samplePos.y >= 0.0 && samplePos.y <= 1.0) {
                blurColor += texture(InSampler, samplePos).rgb;
            }
        }
        blurColor /= samples;
        color = mix(color, blurColor, RadBlur);
    }
    
    // Apply blur
    if (Blur > 0.0) {
        vec3 blurColor = vec3(0.0);
        float samples = 0.0;
        for (float x = -2.0; x <= 2.0; x += 1.0) {
            for (float y = -2.0; y <= 2.0; y += 1.0) {
                vec2 offset = vec2(x, y) * Blur * 0.005;
                vec2 samplePos = clamp(uv + offset, vec2(0.0), vec2(1.0));
                blurColor += texture(InSampler, samplePos).rgb;
                samples += 1.0;
            }
        }
        blurColor /= samples;
        color = mix(color, blurColor, Blur);
    }
    
    // Apply sharpen
    if (Sharp > 0.0) {
        vec3 center = color;
        vec3 sample = vec3(0.0);
        float weight = 0.0;
        
        // Sample surrounding pixels
        for (float x = -1.0; x <= 1.0; x += 1.0) {
            for (float y = -1.0; y <= 1.0; y += 1.0) {
                vec2 offset = vec2(x, y) * 0.005;
                vec2 samplePos = clamp(uv + offset, vec2(0.0), vec2(1.0));
                float w = (x == 0.0 && y == 0.0) ? 5.0 : -1.0;
                sample += texture(InSampler, samplePos).rgb * w;
                weight += w;
            }
        }
        sample /= weight;
        color = mix(color, sample, Sharp);
    }
    
    // Apply edge detection
    if (Edge > 0.0) {
        vec3 edgeColor = vec3(0.0);
        float edgeStrength = 0.0;
        
        // Sobel edge detection
        float gx = 0.0;
        float gy = 0.0;
        
        float kernel[9];
        kernel[0] = -1.0; kernel[1] = 0.0; kernel[2] = 1.0;
        kernel[3] = -2.0; kernel[4] = 0.0; kernel[5] = 2.0;
        kernel[6] = -1.0; kernel[7] = 0.0; kernel[8] = 1.0;
        
        int idx = 0;
        for (float y = -1.0; y <= 1.0; y += 1.0) {
            for (float x = -1.0; x <= 1.0; x += 1.0) {
                vec2 offset = vec2(x, y) * EdgeThk * 0.005;
                vec2 samplePos = clamp(uv + offset, vec2(0.0), vec2(1.0));
                float gray = dot(texture(InSampler, samplePos).rgb, vec3(0.299, 0.587, 0.114));
                gx += gray * kernel[idx];
                gy += gray * kernel[idx + 6];
                idx++;
            }
        }
        
        edgeStrength = length(vec2(gx, gy));
        edgeColor = vec3(edgeStrength * EdgeGlow);
        color = mix(color, color + edgeColor, Edge);
    }
    
    // Apply bloom
    if (Bloom > 0.0) {
        float brightness = dot(color, vec3(0.299, 0.587, 0.114));
        if (brightness > 0.7) {
            vec3 bloomColor = vec3(0.0);
            float samples = 0.0;
            for (float x = -2.0; x <= 2.0; x += 1.0) {
                for (float y = -2.0; y <= 2.0; y += 1.0) {
                    vec2 offset = vec2(x, y) * Bloom * 0.01;
                    vec2 samplePos = clamp(uv + offset, vec2(0.0), vec2(1.0));
                    vec3 sample = texture(InSampler, samplePos).rgb;
                    float sampleBright = dot(sample, vec3(0.299, 0.587, 0.114));
                    if (sampleBright > 0.7) {
                        bloomColor += sample;
                        samples += 1.0;
                    }
                }
            }
            if (samples > 0.0) {
                bloomColor /= samples;
                color += bloomColor * Bloom * 0.5;
            }
        }
    }
    
    // Apply vignette
    if (Vignette > 0.0) {
        vec2 center = vec2(0.5, 0.5);
        float dist = length(uv - center);
        float vignette = 1.0 - smoothstep(0.3, 1.0, dist);
        color = mix(color, color * vignette, Vignette);
    }
    
    // Apply invert
    if (Invert > 0.0) {
        color = mix(color, vec3(1.0) - color, Invert);
    }
    
    // Clamp final color
    color = clamp(color, 0.0, 1.0);
    
    fragColor = vec4(color, InTexel.a);
}


