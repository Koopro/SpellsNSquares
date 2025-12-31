#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform DepthOfFieldConfig {
    float Intensity;
    float FocusX;
    float FocusY;
    float FocusRadius;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Focus point
    vec2 focus = vec2(FocusX, FocusY);
    
    // Distance from focus point
    float dist = length(texCoord - focus);
    
    // Blur amount based on distance from focus
    float blurAmount = 0.0;
    if (dist > FocusRadius) {
        blurAmount = (dist - FocusRadius) * Intensity * 0.1;
    }
    
    // Radial blur
    vec3 blurred = vec3(0.0);
    float sampleCount = 0.0;
    
    if (blurAmount > 0.001) {
        vec2 dir = normalize(texCoord - focus);
        for (float i = -5.0; i <= 5.0; i += 1.0) {
            vec2 offset = dir * blurAmount * i;
            vec3 sample = texture(InSampler, clamp(texCoord + offset, vec2(0.0), vec2(1.0))).rgb;
            blurred += sample;
            sampleCount += 1.0;
        }
        blurred /= sampleCount;
    } else {
        blurred = InTexel.rgb;
    }
    
    // Mix between original and blurred
    vec3 finalColor = mix(InTexel.rgb, blurred, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}







